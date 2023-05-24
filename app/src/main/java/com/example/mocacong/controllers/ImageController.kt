package com.example.mocacong.controllers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ImageController(private val activity: AppCompatActivity, private val listener : ImageSelectedListener) {
    private var imageUrl: Uri? = null
    private var body: MultipartBody.Part? = null

    companion object {
        const val REQ_GALLERY = 1
    }

    interface ImageSelectedListener {
        fun onImageSelected(body: MultipartBody.Part?)

        fun onImageSelected(imageUri: Uri)
    }


    private var imageResult =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imageUrl = result.data?.data ?: return@registerForActivityResult
                val file = File(absolutelyPath(imageUrl, activity))
                val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
                body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                Log.d("Image", "이미지 파일 이름 : ${file.name}")

                //이미지 선택 완료
                listener.onImageSelected(body)
                imageUrl?.let { listener.onImageSelected(it) }
            }
        }



    fun getImageUrl(): Uri? {
        return imageUrl
    }

    fun getBody(): MultipartBody.Part? {
        val tmp = body
        body = null
        return tmp
    }

    private fun absolutelyPath(path: Uri?, context: Context): String {
        val proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        val c: Cursor? = context.contentResolver.query(path!!, proj, null, null, null)
        val index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c?.moveToFirst()

        val result = c?.getString(index!!)
        c?.close()

        return result!!
    }

    fun selectGallery() {
        val writePermission = ContextCompat.checkSelfPermission(
            activity,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val readPermission = ContextCompat.checkSelfPermission(
            activity,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (writePermission == PackageManager.PERMISSION_DENIED ||
            readPermission == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                activity,
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