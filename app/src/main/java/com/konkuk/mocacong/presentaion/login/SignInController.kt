package com.konkuk.mocacong.presentaion.login

import android.util.Log
import com.konkuk.mocacong.objects.Member
import com.konkuk.mocacong.objects.NetworkUtil
import com.konkuk.mocacong.objects.RetrofitClient
import com.konkuk.mocacong.remote.apis.SignInAPI

class SignInController {

    suspend fun signIn(signInRequest: com.konkuk.mocacong.remote.models.request.SignInRequest): String {
        val response = RetrofitClient.create(SignInAPI::class.java).signIn(signInRequest)
        return if (response.isSuccessful) {
            //로그인 성공
            try {
                Member.setAuthToken(response.body()!!.token)
            } catch (e: NullPointerException) {
                Log.d("signIn", "성공했는데 responseBody 없음")
            }
            "로그인 성공";
        } else {
            val errorResponse = NetworkUtil.getErrorResponse(response.errorBody()!!)
            errorResponse!!.message
        }
    }
}