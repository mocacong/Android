package com.konkuk.mocacong.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.konkuk.mocacong.objects.Utils
import com.konkuk.mocacong.presentation.login.LoginActivity
import com.konkuk.mocacong.remote.models.response.ErrorResponse

object TokenExceptionHandler {

    fun handleTokenException(context: Context, errorResponse: ErrorResponse) {
        Log.e("NetworkError",errorResponse.code.toString())
        when (errorResponse.code) {
            1013 -> {
                Utils.showConfirmDialog(context,
                    "로그인이 필요한 서비스입니다. 로그인 페이지로 이동하시겠습니까?",
                    confirmAction = {
                        gotoSignInActivity(context)
                    },
                    cancelAction = {

                    }
                )
            }
            1014, 1015 , 1018 , 1021-> {
                Log.d("NetworkError","when 들어왔음")
                Utils.showConfirmDialog(context,
                    "세션이 만료되었습니다. 다시 한 번 로그인 해주세요",
                    confirmAction = {
                        gotoSignInActivity(context)
                    },
                    cancelAction = {

                    }
                )
            }

        }
    }

    private fun gotoSignInActivity(context: Context) {
        val intent = Intent(context, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        if (context is Activity) {
            context.finish()
        }
    }
}
