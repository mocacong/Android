package com.example.mocacong.controllers

import android.util.Log
import com.example.mocacong.data.objects.Member
import com.example.mocacong.data.objects.RetrofitClient
import com.example.mocacong.data.request.SignInRequest
import com.example.mocacong.network.SignInAPI
import org.json.JSONObject

class SignInController {

    suspend fun signIn(signInRequest: SignInRequest) : String{
        val response = RetrofitClient.create(SignInAPI::class.java).signIn(signInRequest)
        if(response.isSuccessful){
            //로그인 성공
            try {
                Member.setAuthToken(response.body()!!.token)
            }catch (e: NullPointerException){
                Log.d("signIn", "성공했는데 responseBody 없음")
            }
            return "로그인 성공";
        }
        else{
            return response.message()
        }
    }
}