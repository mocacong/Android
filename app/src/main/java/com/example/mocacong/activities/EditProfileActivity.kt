package com.example.mocacong.activities


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mocacong.controllers.ImageController
import com.example.mocacong.data.objects.Member
import com.example.mocacong.data.objects.RetrofitClient
import com.example.mocacong.data.response.ProfileResponse
import com.example.mocacong.databinding.ActivityEditProfileBinding
import com.example.mocacong.network.MyPageAPI
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class EditProfileActivity : AppCompatActivity(), ImageController.ImageSelectedListener {

    lateinit var binding: ActivityEditProfileBinding
    private val api = RetrofitClient.create(MyPageAPI::class.java)
    lateinit var imgController: ImageController
    private var body: MultipartBody.Part? = null


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
        imgController = ImageController(this, this)

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
            body?.let { sendImage(it) }
            val intent = Intent(this@EditProfileActivity, MainActivity::class.java)
            intent.putExtra("tabNumber", 1)
            startActivity(intent)
        }

    }


    private fun setEditImgBtn() {
        binding.editImageBtn.setOnClickListener {
            imgController.selectGallery()
            binding.profileImg.setImageURI(imgController.getImageUrl())
        }
    }

    private fun setLogoutBtn() {
        Member.deleteInfo()
        val intent = Intent(this, SignInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }


    private fun setMemberInfo() {
        lifecycleScope.launch {
            if(Member.email == null) {
                val profile = async { getProfileInfo() }.await()
                Member.email = profile.email
                Member.nickname = profile.nickname
                Member.phone = profile.phone
                Member.imgUrl = profile.imgUrl
            }

            Member.imgUrl?.let {
                binding.profileImg.setImageURI(Uri.parse(it))
            }
            binding.emailText.text = Member.email
            binding.nameText.setText(Member.nickname)
            binding.phoneText.setText(Member.phone)
        }
    }

    private suspend fun getProfileInfo(): ProfileResponse {
        val api = RetrofitClient.create(MyPageAPI::class.java)
        val response = api.getMyProfile()
        if (response.isSuccessful) {
            Log.d("Mypage", "프로필 정보 get 성공")
            return response.body() ?: throw Exception("프로필 정보가 null입니다.")
        } else {
            Log.d("Mypage", "프로필 get 실패 : ${response.errorBody()?.string()}")
            throw Exception("프로필 정보를 가져오는 데 실패했습니다.")
        }
    }


    override fun onImageSelected(body: MultipartBody.Part?) {
        this.body = body
    }

    override fun onImageSelected(imageUri: Uri) {
        binding.profileImg.setImageURI(imageUri)
    }


}