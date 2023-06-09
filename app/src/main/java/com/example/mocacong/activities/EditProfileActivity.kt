package com.example.mocacong.activities


import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.mocacong.data.objects.Member
import com.example.mocacong.data.objects.RetrofitClient
import com.example.mocacong.data.response.ProfileResponse
import com.example.mocacong.databinding.ActivityEditProfileBinding
import com.example.mocacong.network.MyPageAPI
import com.example.mocacong.ui.MessageDialog
import gun0912.tedimagepicker.builder.TedImagePicker
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class EditProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityEditProfileBinding
    private val api = RetrofitClient.create(MyPageAPI::class.java)
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

        setLayout()
        setContentView(binding.root)
    }

    private fun setLayout() {
        setEditImgBtn()
        setMemberInfo()
//        setEditPwBtn()

        binding.completeBtn.setOnClickListener {
            completeBtnClicked()
            finish()
        }
        binding.cancelBtn.setOnClickListener {
            onBackPressed()
        }

        binding.logoutBtn.setOnClickListener {
            setLogoutBtn()
        }

        binding.gotoEditPasswordBtn.setOnClickListener {
            MessageDialog("서비스 준비 중입니다.").show(supportFragmentManager, "MessageDialog")
        }

    }

    private fun completeBtnClicked() {
        lifecycleScope.launch {
            body?.let {
                sendImage(it)
            }
            putInfo()
            val intent = Intent(this@EditProfileActivity, MainActivity::class.java)
            intent.putExtra("tabNumber", 1)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun putInfo() {
//        lifecycleScope.launch {
//            val message = async {
//                val response = api.editProfileInfo()
//                if(response.isSuccessful){
//                    //로그인 성공
//                    try {
//                        Member.setAuthToken(response.body()!!.token)
//                    }catch (e: NullPointerException){
//                        Log.d("signIn", "성공했는데 responseBody 없음")
//                    }
//                    return "로그인 성공";
//                }
//                else{
//                    //로그인 실패
//                    val json = JSONObject(response.errorBody()?.string())
//                    val code = json.getInt("code")
//                    val msg = json.getString("message")
//                    Log.d("signIn", "Error: $msg Code: $code")
//                    return msg
//                }
//            }
//        }
    }

    private fun setEditImgBtn() {
        binding.editImageBtn.setOnClickListener {
            TedImagePicker.with(this).start {
                binding.profileImg.setImageURI(it)
                val file = File(absolutePath(it))
                val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
                body = MultipartBody.Part.createFormData("file", file.name, requestFile)
            }
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
            if (Member.email == null) {
                val profile = async { getProfileInfo() }.await()
                Member.email = profile.email
                Member.nickname = profile.nickname
                Member.phone = profile.phone
                Member.imgUrl = profile.imgUrl
            }

            Member.imgUrl?.let {
                binding.profileImg.setImageURI(Uri.parse(it))
            }

            if (Member.imgUrl != null)
                Glide.with(this@EditProfileActivity).load(Member.imgUrl).into(binding.profileImg)
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