package com.example.mocacong.controllers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ImageController(private val activity: AppCompatActivity, private val listener: ImageSelectedListener) {
    private var imageUrl: Uri? = null

    companion object {
        const val REQ_GALLERY = 1

    }

    interface ImageSelectedListener {
        fun onImageSelected(body: MultipartBody.Part?) {}
        fun onImageSelected(imageUri: Uri) {}
        fun onImageSelected(imageParts: List<MultipartBody.Part>) {}
    }

    private val imageLauncher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val clipData = data?.clipData
            val selectedImages = mutableListOf<Uri>()

            if (clipData != null) {
                // 다중 이미지 선택
                val count = clipData.itemCount.coerceAtMost(3)
                for (i in 0 until count) {
                    val imageUri = clipData.getItemAt(i).uri
                    selectedImages.add(imageUri)
                }
            } else {
                // 단일 이미지 선택
                val imageUri = data?.data
                if (imageUri != null) {
                    selectedImages.add(imageUri)
                }
            }

            val imageParts = selectedImages.map { uri ->
                Log.d("CafeImage","uri : $uri")
                val file = File(absolutePath(uri, activity))
                val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
                MultipartBody.Part.createFormData("files", file.name, requestFile)
            }

            listener.onImageSelected(imageParts)
            imageUrl?.let { listener.onImageSelected(it) }
        }
    }

    fun getImageUrl(): Uri? {
        return imageUrl
    }

    private fun absolutePath(uri: Uri?, context: Context): String {
        val proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        val c: Cursor? = context.contentResolver.query(uri!!, proj, null, null, null)
        val index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c?.moveToFirst()

        val result = c?.getString(index!!)
        c?.close()

        return result!!
    }


    fun launchGallery() {
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
            val intent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                Intent(MediaStore.ACTION_PICK_IMAGES)
            else
                Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            imageLauncher.launch(intent)
        }
    }

    fun launchMultipleGallery() {
        Log.d("CafeImage","multiple됨")
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
            Log.d("CafeImage","멀티플 인텐트 보낸당")
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

            imageLauncher.launch(Intent.createChooser(intent, "Select Image"))

        }
    }
}
