package com.konkuk.mocacong.controllers

import android.util.Log
import com.konkuk.mocacong.data.objects.Member
import com.konkuk.mocacong.data.objects.NetworkUtil
import com.konkuk.mocacong.data.objects.RetrofitClient
import com.konkuk.mocacong.data.request.SignInRequest
import com.konkuk.mocacong.network.SignInAPI

class SignInController {

    suspend fun signIn(signInRequest: SignInRequest) : String{
        val response = RetrofitClient.create(SignInAPI::class.java).signIn(signInRequest)
        return if(response.isSuccessful){
            //로그인 성공
            try {
                Member.setAuthToken(response.body()!!.token)
            }catch (e: NullPointerException){
                Log.d("signIn", "성공했는데 responseBody 없음")
            }
            "로그인 성공";
        } else{
            val errorResponse = NetworkUtil.getErrorResponse(response.errorBody()!!)
            errorResponse!!.message
        }
    }
}