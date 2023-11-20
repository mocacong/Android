package com.konkuk.mocacong.presentation.main.settings


import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.konkuk.mocacong.R
import com.konkuk.mocacong.databinding.ActivityEditProfileBinding
import com.konkuk.mocacong.objects.Member
import com.konkuk.mocacong.objects.Utils
import com.konkuk.mocacong.objects.Utils.handleEnterKey
import com.konkuk.mocacong.presentation.login.LoginActivity
import com.konkuk.mocacong.remote.repositories.EditProfileRepository
import com.konkuk.mocacong.util.ApiState
import com.konkuk.mocacong.util.TokenExceptionHandler
import com.konkuk.mocacong.util.ViewModelFactory
import gun0912.tedimagepicker.builder.TedImagePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class EditProfileActivity : AppCompatActivity() {
    val TAG = "EditProfile"
    lateinit var binding: ActivityEditProfileBinding

    private var body: MultipartBody.Part? = null
    private lateinit var firstNickname: String

    private lateinit var profileViewModel: EditProfileViewModel
    private lateinit var profileViewModelFactory: ViewModelFactory<EditProfileRepository>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)

        profileViewModelFactory = ViewModelFactory(EditProfileRepository())
        profileViewModel =
            ViewModelProvider(this, profileViewModelFactory)[EditProfileViewModel::class.java]
        setLayout()
        setContentView(binding.root)
    }

    private fun setLayout() {
        setEditImgBtn()
        setMemberInfo()
//        setEditPwBtn()

        binding.nameText.handleEnterKey()

        binding.completeBtn.setOnClickListener {
            completeBtnClicked()
        }
        binding.cancelBtn.setOnClickListener {
            onBackPressed()
        }

        binding.logoutBtn.setOnClickListener {
            setLogoutBtn()
        }

        binding.gotoEditPasswordBtn.setOnClickListener {
//            MessageDialog("서비스 준비 중입니다.").show(supportFragmentManager, "MessageDialog")
        }
    }

    private fun completeBtnClicked() {
        //닉네임이 변하지 않았으면 이미지만 send
        lifecycleScope.launch {
            val nickname = binding.nameText.text.toString()
            val isNicknameSuccess =
                withContext(Dispatchers.IO) { if (nickname != firstNickname) putNickName(nickname) else true }
            if (isNicknameSuccess) {
                val isImageSuccess = withContext(Dispatchers.IO) {
                    if (body != null) {
                        putProfileImage(body!!)
                    } else true
                }
                if (isImageSuccess) {
                    Utils.showToast(this@EditProfileActivity, "회원 정보가 변경되었습니다")
                    setResult(RESULT_OK)
                    finish()
                }
            }
        }
    }

    private suspend fun putProfileImage(body: MultipartBody.Part): Boolean {
        profileViewModel.apply {
            putProfileImage(body).join()
            when (val apiState = profileImagePUTFlow.value) {
                is ApiState.Success -> {
                    return true
                }
                is ApiState.Error -> {
                    apiState.errorResponse?.let {
                        TokenExceptionHandler.handleTokenException(this@EditProfileActivity, it)
                        Log.e(TAG, it.message)
                    }
                    return false
                }
                is ApiState.Loading -> {
                    return false
                }
            }
        }
    }

    private suspend fun putNickName(nickname: String): Boolean {
        profileViewModel.apply {
            putProfileInfo(com.konkuk.mocacong.remote.models.request.EditProfileRequest(nickname)).join()
            when (val apiState = profileInfoPUTFlow.value) {
                is ApiState.Success -> {
                    return true
                }
                is ApiState.Error -> {
                    apiState.errorResponse?.let {
                        TokenExceptionHandler.handleTokenException(this@EditProfileActivity, it)
                        Log.e(TAG, it.message)
                        when (it.code) {
                            1002 -> {
//                                MessageDialog("닉네임 형식은 영어, 한글 2~6자 입니다").show(supportFragmentManager, "MessageDialog")
                            }
                            1004 -> {
//                                MessageDialog("이미 존재하는 닉네임입니다").show(supportFragmentManager, "MessageDialog")
                            }
                        }
                    }
                    return false
                }
                is ApiState.Loading -> {
                    return false
                }
            }
        }
    }

    private fun setEditImgBtn() {
        binding.editImageBtn.setOnClickListener {
            TedImagePicker.with(this).start {
                binding.profileImg.setImageURI(it)
                val file = File(absolutePath(it))
                val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                Log.d("editProfile", "body = $body")
            }
        }
    }


    private fun setLogoutBtn() {
        Member.deleteInfo()
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }


    private fun setMemberInfo() =
        lifecycleScope.launch {
            profileViewModel.apply {
                requestProfileInfo().join()
                when (val apiState = profileGETFlow.value) {
                    is ApiState.Success -> {
                        apiState.data?.let { profile ->
                            firstNickname = profile.nickname
                            binding.emailText.text = profile.email
                            binding.nameText.setText(profile.nickname)
                            if (profile.imgUrl == null) {
                                binding.profileImg.setImageResource(R.drawable.profile_no_image)
                            } else {
                                Glide.with(this@EditProfileActivity).load(profile.imgUrl)
                                    .into(binding.profileImg)
                            }
                        }
                    }
                    is ApiState.Error -> {
                        apiState.errorResponse?.let {
                            TokenExceptionHandler.handleTokenException(this@EditProfileActivity, it)
                            Log.e(TAG, it.message)
                        }
                    }
                    is ApiState.Loading -> {}
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