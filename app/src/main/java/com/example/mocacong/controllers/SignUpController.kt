package com.example.mocacong.controllers

import android.util.Log
import com.example.mocacong.data.objects.RetrofitClient
import com.example.mocacong.data.request.SignUpRequest
import com.example.mocacong.network.SignUpAPI
import org.json.JSONObject

class SignUpController() {

    suspend fun signUp(member: SignUpRequest) : Int{
        Log.d("dd", "signupfun called")
        //회원 가입 로직 처리 함수
        val response = RetrofitClient.create(SignUpAPI::class.java).signUp(member)
        return if(response.isSuccessful){
            response.code()
            Log.d("signUp", "signUp SUCCESSED : ${response.code()}")
        }else{
            val json = JSONObject(response.errorBody()?.string())
            val code = json.getInt("code")
            val msg = json.getString("message")
            Log.d("signUp","Err MSG: $msg, code: $code")
            code
        }

    }

    fun emailConfirm(email: String): Boolean {
        Log.d("hi", "emailconfirm" + email)
        return false
    }

    fun nicknameConfirm(nickname: String): Boolean {
        Log.d("hi", "nickconfirm" + nickname)
        return false
    }


    fun passwordRegex(password: String): Boolean {
        return password.matches(Regex("^(?=.*[a-z])(?=.*\\d)[a-z\\d]{8,20}$"))
    }


}
