package com.example.mocacong.data.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.example.mocacong.activities.SignInActivity
import com.example.mocacong.data.objects.Utils
import com.example.mocacong.data.response.ErrorResponse

object TokenExceptionHandler {

    fun handleTokenException(context: Context, errorResponse: ErrorResponse) {
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
            1014 or 1015 -> {
                Utils.showConfirmDialog(context,
                    errorResponse.message,
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
        val intent = Intent(context, SignInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        if (context is Activity) {
            context.finish()
        }
    }
}
