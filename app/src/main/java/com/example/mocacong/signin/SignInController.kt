package com.example.mocacong.signin

import android.util.Log
import com.example.mocacong.Member
import com.example.mocacong.RetrofitClient
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
            //로그인 실패
            val json = JSONObject(response.errorBody()?.string())
            val code = json.getInt("code")
            val msg = json.getString("message")
            Log.d("signIn", "Error: $msg Code: $code")
            return msg
        }
    }




}