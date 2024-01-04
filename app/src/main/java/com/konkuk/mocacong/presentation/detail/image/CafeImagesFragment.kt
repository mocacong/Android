package com.konkuk.mocacong.presentation.detail.image

import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.konkuk.mocacong.databinding.FragmentCafeImagesBinding
import com.konkuk.mocacong.presentation.base.BaseFragment
import com.konkuk.mocacong.presentation.detail.CafeDetailViewModel
import com.konkuk.mocacong.remote.models.response.CafeImage
import com.konkuk.mocacong.util.ApiState
import gun0912.tedimagepicker.builder.TedImagePicker
import gun0912.tedimagepicker.builder.type.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class CafeImagesFragment : BaseFragment<FragmentCafeImagesBinding>() {
    private val detailViewModel: CafeDetailViewModel by activityViewModels()

    private fun addImage() {
        // 이미지 선택
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
            detailViewModel.postMyImage(parts)
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

    override val TAG: String
        get() = "CafeImagesFragment"
    override val layoutRes: Int = com.konkuk.mocacong.R.layout.fragment_cafe_images

    lateinit var adapter: ImageAdapter
    override fun afterViewCreated() {
        binding.vm = detailViewModel

        val clickListener = object : ImageAdapter.ButtonClickListener {
            override fun onImageClicked(cafeImage: CafeImage) {
                //메뉴
//                detailViewModel.currentImage = cafeImage
//                val dialog = FullImageDialog()
//                dialog.show(childFragmentManager, dialog.tag)
            }

            override fun onMoreClicked() {
                //더보기
                detailViewModel.requestCafeImages(detailViewModel.imagePage++)
            }

        }

        adapter = ImageAdapter(clickListener)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = adapter

        binding.plusBtn.setOnClickListener {
            addImage()
        }


        detailViewModel.postImagesResponse.observe(this) {
            it.byState(
                onSuccess = {
                    showToast("이미지가 성공적으로 등록되었습니다")
                    detailViewModel.imagePage = 0
                    detailViewModel.requestCafeImages()
                    detailViewModel.postImagesResponse.value = ApiState.Loading()
                }
            )
        }
    }

}