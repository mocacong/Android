package com.example.mocacong.activities

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mocacong.adapter.ImageAdapter
import com.example.mocacong.data.objects.RetrofitClient
import com.example.mocacong.data.response.CafeImage
import com.example.mocacong.data.response.CafeImageResponse
import com.example.mocacong.databinding.ActivityCafeImagesBinding
import com.example.mocacong.network.CafeImagesAPI
import gun0912.tedimagepicker.builder.TedImagePicker
import gun0912.tedimagepicker.builder.type.MediaType
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class CafeImagesActivity : AppCompatActivity() {

    lateinit var binding: ActivityCafeImagesBinding

    private lateinit var adapter: ImageAdapter
    private lateinit var cafeId: String
    private val imageUriList = mutableListOf<CafeImage>()
    private var currentPage = 0
    private var isEnd = false
    private val api = RetrofitClient.create(CafeImagesAPI::class.java)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCafeImagesBinding.inflate(layoutInflater)

        getCafeId()
        setLayout()
        setContentView(binding.root)
    }

    private fun getCafeId() {
        cafeId = intent.getStringExtra("cafeId").toString()
    }


    private fun setLayout() {
        lifecycleScope.launch {
            val response = async { getCafeImages(page = currentPage++) }.await()
            if (response != null) {
                imageUriList.addAll(response.cafeImages)

                isEnd = response.isEnd
                adapter = ImageAdapter(imageUriList)
                binding.recyclerView.adapter = adapter
                binding.recyclerView.layoutManager = GridLayoutManager(this@CafeImagesActivity, 3)
            }
        }

        binding.recyclerView.setOnScrollChangeListener { view, _, _, _, _ ->
            if (!view.canScrollVertically(1) && !isEnd)
                loadNextPage()
        }

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.plusBtn.setOnClickListener {
            addImage()
        }


    }

    private fun addImage() {
        // 이미지 선택
//        imageController.launchMultipleGallery()
        val picker = TedImagePicker.with(this)
        picker.apply {
            mediaType(MediaType.IMAGE)
            max(3, "이미지는 최대 3장까지 선택 가능합니다.")
        }.startMultiImage { selectedImages ->

            val parts = selectedImages.map { uri ->
                Log.d("CafeImage", "uri : $uri")
                val file = File(absolutePath(uri))
                val requestFile = RequestBody.create(okhttp3.MediaType.parse("image/*"), file)
                Log.d("Image", "files에 추가됐따!!!!!!!!!!!!!!!!!! ${file.name}")
                MultipartBody.Part.createFormData("files", file.name, requestFile)
            }
            onImageSelected(parts)
        }
    }


    private suspend fun postCafeImage(body: List<MultipartBody.Part>) {
        val response = api.postCafeImages(cafeId = cafeId, files = body)

        Log.d("CafeImage", "이미지 보낸다 : ${response.raw().request().body()}")
        if (response.isSuccessful) {
            Log.d("CafeImage", "이미지 포스트 성공")
        } else {
            Log.d(
                "CafeImage",
                "이미지 포스트 실패 : ${response.errorBody()?.string()} , ${response.code()}"
            )
        }
    }

    private suspend fun getCafeImages(page: Int): CafeImageResponse? {
        val response = api.getCafeImagesResponse(cafeId = cafeId, page = page)
        Log.d("CafeImage", "카페이미지 get 호출함")
        return if (response.isSuccessful) {
            Log.d("CafeImage", "카페이미지 get : ${response.body()}")
            response.body()
        } else {
            Log.d("CafeImage", "카페이미지 GET 실패!!! ${response.errorBody()?.string()}")
            null
        }
    }


    private fun loadNextPage() {
        binding.progressBar.visibility = View.VISIBLE

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
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun onImageSelected(imageParts: List<MultipartBody.Part>) {
        lifecycleScope.launch {
            async { postCafeImage(imageParts) }.await()
            imageUriList.clear()
            currentPage = 0
            val response = async { getCafeImages(page = currentPage++) }.await()
            if (response != null) {
                imageUriList.addAll(response.cafeImages)
                adapter.notifyDataSetChanged()
                isEnd = response.isEnd
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun absolutePath(uri: Uri?): String {
        val proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        val c: Cursor? = contentResolver.query(uri!!, proj, null, null, null)
        val index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c?.moveToFirst()

        val result = c?.getString(index!!)
        c?.close()

        return result!!
    }


}

