package com.example.mocacong.activities

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.mocacong.adapter.ImageAdapter
import com.example.mocacong.controllers.ImageController
import com.example.mocacong.data.objects.RetrofitClient
import com.example.mocacong.data.response.CafeImage
import com.example.mocacong.data.response.CafeImageResponse
import com.example.mocacong.databinding.ActivityCafeImagesBinding
import com.example.mocacong.network.CafeImagesAPI
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class CafeImagesActivity : AppCompatActivity(),ImageController.ImageSelectedListener {

    lateinit var binding: ActivityCafeImagesBinding

    private lateinit var adapter: ImageAdapter
    private lateinit var cafeId: String
    private lateinit var imageController: ImageController
    private val imageUriList = mutableListOf<CafeImage>()
    private var currentPage = 0
    private var isEnd = false
    private val api = RetrofitClient.create(CafeImagesAPI::class.java)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCafeImagesBinding.inflate(layoutInflater)
        imageController = ImageController(this, this)

        getCafeId()
        setLayout()
        setContentView(binding.root)
    }

    private fun getCafeId() {
        cafeId = intent.getStringExtra("cafeId").toString()
    }


    private fun setLayout() {
        lifecycleScope.launch {
            val response = getCafeImages(page = currentPage++)
            if (response != null) {
                imageUriList.addAll(response.cafeImages)
                isEnd = response.isEnd
                adapter = ImageAdapter(imageUriList)
                binding.recyclerView.adapter = adapter
                binding.recyclerView.layoutManager = GridLayoutManager(this@CafeImagesActivity,3)
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
        imageController.selectGallery()
    }


    private suspend fun postCafeImage(body: MultipartBody.Part) {
        val response = api.postCafeImage(cafeId = cafeId, file = body)
        if (response.isSuccessful) {
            Log.d("CafeImage", "이미지 포스트 성공")
        } else {
            Log.d("CafeImage", "이미지 포스트 실패 : ${response.errorBody()?.string()}")
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

    override fun onImageSelected(body: MultipartBody.Part?) {
        if (body != null) {
            lifecycleScope.launch {
                postCafeImage(body)
                imageUriList.clear()
                currentPage = 0
                val response = getCafeImages(page = currentPage++)
                if (response != null) {
                    imageUriList.addAll(response.cafeImages)
                    adapter.notifyDataSetChanged()
                    isEnd = response.isEnd
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    override fun onImageSelected(imageUri: Uri) {
        //uriget
    }

}

