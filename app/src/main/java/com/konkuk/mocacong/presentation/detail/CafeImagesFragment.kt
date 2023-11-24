package com.konkuk.mocacong.presentation.detail

import android.app.Dialog
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.konkuk.mocacong.databinding.FragmentCafeImagesBinding
import com.konkuk.mocacong.objects.NetworkUtil
import com.konkuk.mocacong.objects.RetrofitClient
import com.konkuk.mocacong.remote.apis.CafeImagesAPI
import com.konkuk.mocacong.remote.models.response.CafeImage
import com.konkuk.mocacong.remote.models.response.CafeImageResponse
import com.konkuk.mocacong.util.ApiState
import com.konkuk.mocacong.util.TokenExceptionHandler
import gun0912.tedimagepicker.builder.TedImagePicker
import gun0912.tedimagepicker.builder.type.MediaType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class CafeImagesFragment : BottomSheetDialogFragment() {
    private lateinit var adapter: ImageAdapter
    private lateinit var cafeId: String
    private val imageUriList = mutableListOf<CafeImage>()
    private var currentPage = 0
    private var isEnd = false
    private val api = RetrofitClient.create(CafeImagesAPI::class.java)

    private var _binding: FragmentCafeImagesBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            cafeId = it.getString("cafeId")!!
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme).apply {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable = false
        }

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCafeImagesBinding.inflate(inflater, container, false)
        setLayout()

        return binding.root
    }


    private fun setLayout() {
        lifecycleScope.launch {
            val response: CafeImageResponse? =
                withContext(Dispatchers.Default) {
                    getCafeImages(
                        page = currentPage++
                    )
                }

            if (response != null) {
                imageUriList.addAll(response.cafeImages)

                isEnd = response.isEnd
                adapter = ImageAdapter(imageUriList)
                binding.recyclerView.adapter = adapter
                binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
            }
        }

        binding.recyclerView.setOnScrollChangeListener { view, _, _, _, _ ->
            if (!view.canScrollVertically(1) && !isEnd)
                loadNextPage()
        }

        binding.plusBtn.setOnClickListener {
            addImage()
        }
    }

    private fun addImage() {
        // 이미지 선택
//        imageController.launchMultipleGallery()
        val picker = TedImagePicker.with(requireContext())
        picker.apply {
            mediaType(MediaType.IMAGE)
            max(3, "이미지는 최대 3장까지 선택 가능합니다.")
        }.startMultiImage { selectedImages ->
            val parts = selectedImages.map { uri ->
                Log.d("CafeImage", "uri : $uri")
                val file = File(absolutePath(uri))
                val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                Log.d("Image", "files에 추가됐따!!!!!!!!!!!!!!!!!! ${file.name}")
                MultipartBody.Part.createFormData("files", file.name, requestFile)
            }
            onImageSelected(parts)
        }
    }


    private suspend fun postCafeImage(body: List<MultipartBody.Part>): ApiState<Void> {
        val response = api.postCafeImages(cafeId = cafeId, files = body)

        return if (response.isSuccessful) ApiState.Success(response.body())
        else {
            val errorResponse = NetworkUtil.getErrorResponse(response.errorBody()!!)
            ApiState.Error(errorResponse = errorResponse!!)
        }
    }

    private suspend fun getCafeImages(page: Int): CafeImageResponse? {
        val response = api.getCafeImagesResponse(cafeId = cafeId, page = page)
        Log.d("CafeImage", "카페이미지 get 호출함")
        return if (response.isSuccessful) {
            Log.d("CafeImage", "카페이미지 get : ${response.body()}")
            response.body()
        } else {
            null
        }
    }


    private fun loadNextPage() {
        // 다음 페이지 가져옴
        lifecycleScope.launch {
            val response = getCafeImages(page = currentPage++)
            if (response != null) {
                imageUriList.addAll(response.cafeImages)
                adapter.notifyItemRangeInserted(
                    imageUriList.size - response.cafeImages.size,
                    response.cafeImages.size
                )
                isEnd = response.isEnd
            }
        }
    }

    private fun onImageSelected(imageParts: List<MultipartBody.Part>) {
        lifecycleScope.launch {
            val imgPostState = withContext(Dispatchers.IO) {
                postCafeImage(imageParts)
            }
            when (imgPostState) {
                is ApiState.Success -> {
                    imageUriList.clear()
                    currentPage = 0
                    val response = withContext(Dispatchers.IO) {
                        getCafeImages(page = currentPage++)
                    }
                    if (response != null) {
                        imageUriList.addAll(response.cafeImages)
                        adapter.notifyDataSetChanged()
                        isEnd = response.isEnd
                    }
                }
                is ApiState.Error -> {
                    imgPostState.errorResponse?.let {
                        TokenExceptionHandler.handleTokenException(requireContext(), it)
                        Log.e("Image", it.message)
                        if (it.code == 2008) {
//                            MessageDialog("카페 이미지는 세 장까지 업로드 가능합니다").show(
//                                childFragmentManager,
//                                "MessageDialog"
//                            )
                        }
                    }
                }
                is ApiState.Loading -> {}
            }
        }
    }

    private fun absolutePath(uri: Uri?): String {
        val proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        val c: Cursor? = requireActivity().contentResolver.query(uri!!, proj, null, null, null)
        val index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c?.moveToFirst()

        val result = c?.getString(index!!)
        c?.close()

        return result!!
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        (activity as CafeDetailActivity).refreshDetailInfo()
        _binding = null
    }


}