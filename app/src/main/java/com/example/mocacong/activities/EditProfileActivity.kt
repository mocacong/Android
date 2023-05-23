package com.example.mocacong.activities


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mocacong.controllers.ImageController
import com.example.mocacong.data.objects.RetrofitClient
import com.example.mocacong.databinding.ActivityEditProfileBinding
import com.example.mocacong.network.MyPageAPI
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
class EditProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityEditProfileBinding
    private val api = RetrofitClient.create(MyPageAPI::class.java)
    lateinit var imgController : ImageController


    private suspend fun sendImage(body: MultipartBody.Part) {
        val response = api.putMyProfileImage(file = body)
        if (response.isSuccessful)
            Log.d("editProfile", "프로필 이미지 put 성공")
        else
            Log.d("editProfile", "프로필 이미지 put 실패 : ${response.errorBody()?.string()}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditProfileBinding.inflate(layoutInflater)

        imgController = ImageController(this, binding.profileImg)
        setLayout()
        setContentView(binding.root)
    }

    private fun setLayout() {
        setEditImgBtn()
        setMemberInfo()
//        setLogoutBtn()
//        setEditPwBtn()

        binding.completeBtn.setOnClickListener {
            completeBtnClicked()
        }
        binding.cancelBtn.setOnClickListener {
            onBackPressed()
        }

    }

    private fun completeBtnClicked() {
        lifecycleScope.launch {
            val body = imgController.getBody()
            if (body != null) sendImage(body)
            val intent = Intent(this@EditProfileActivity, MainActivity::class.java)
            intent.putExtra("tabNumber", 1)
            startActivity(intent)
        }

    }


    private fun setEditImgBtn() {
        binding.editImageBtn.setOnClickListener {
            imgController.selectGallery()
        }
    }

    private fun setLogoutBtn() {
    }

    private fun setEditPwBtn() {
    }

    private fun setMemberInfo() {


    }

    private suspend fun getProfileInfo(){

    }


}