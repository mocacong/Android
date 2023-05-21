package com.example.mocacong.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.mocacong.data.objects.RetrofitClient
import com.example.mocacong.databinding.ActivityEditProfileBinding
import com.example.mocacong.network.MyPageAPI
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class EditProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityEditProfileBinding
    private val api = RetrofitClient.create(MyPageAPI::class.java)
    var body: MultipartBody.Part? = null

    companion object {
        const val REQ_GALLERY = 1
    }

    private val imageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUrl = result.data?.data ?: return@registerForActivityResult
                val file = File(absolutelyPath(imageUrl, this))
                val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
                body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                Log.d("editProfile", "프로필 이미지 파일 이름 : ${file.name}")
                binding.profileImg.setImageURI(imageUrl)
            }
        }

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
//        getMemberInfo()
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
            if (body != null) sendImage(body!!)
            val intent = Intent(this@EditProfileActivity, MainActivity::class.java)
            intent.putExtra("tabNumber", 1)
            startActivity(intent)
        }

    }


    private fun setEditImgBtn() {
        binding.editImageBtn.setOnClickListener {
            selectGallery()
        }
    }

    private fun setLogoutBtn() {
    }

    private fun setEditPwBtn() {
    }

    private fun getMemberInfo() {
    }


    //절대경로 반환환
    fun absolutelyPath(path: Uri?, context: Context): String {
        val proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        val c: Cursor? = context.contentResolver.query(path!!, proj, null, null, null)
        val index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c?.moveToFirst()

        val result = c?.getString(index!!)
        c?.close()

        return result!!
    }

    private fun selectGallery() {
        val writePermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val readPermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (writePermission == PackageManager.PERMISSION_DENIED ||
            readPermission == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                REQ_GALLERY
            )
        } else {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*"
            )
            imageResult.launch(intent)
        }


    }

}