package com.example.mocacong.controllers

import android.util.Log
import android.view.View
import com.example.mocacong.data.objects.RetrofitClient
import com.example.mocacong.data.request.SignUpRequest
import com.example.mocacong.network.SignUpAPI
import org.json.JSONObject

class SignUpController() {

    val api = RetrofitClient.create(SignUpAPI::class.java)

    suspend fun signUp(member: SignUpRequest): Int {
        Log.d("dd", "signupfun called")
        //회원 가입 로직 처리 함수
        val response = api.signUp(member)
        return if (response.isSuccessful) {
            200
            Log.d("signUp", "signUp SUCCESSED : ${response.code()}")
        } else {
            val json = JSONObject(response.errorBody()?.string())
            val code = json.getInt("code")
            val msg = json.getString("message")
            Log.d("signUp", "Err MSG: $msg, code: $code")
            code
        }

    }

    suspend fun emailConfirm(email: String): Boolean {
        Log.d("hi", "emailconfirm" + email)
        val response = api.checkEmail(email)
        if (response.isSuccessful) {
            return response.body()!!.result
        } else {
            Log.e("hi", "이메일 중복체크 실패 이유 ${response.errorBody()?.string()}")
            throw java.lang.Exception("이메일 중복체크 실패!")
        }
    }

    suspend fun nicknameConfirm(nickname: String): Boolean {
        Log.d("hi", "nicknameConfirm" + nickname)
        val response = api.checkNickname(nickname)
        if (response.isSuccessful) {
            return response.body()!!.result
        } else {
            Log.e("hi", "닉네임 중복체크 실패 이유 ${response.errorBody()?.string()}")
            throw java.lang.Exception("닉네임 중복체크 실패!")
        }
    }


    fun passwordRegex(password: String): Boolean {
        return password.matches(Regex("^(?=.*[a-z])(?=.*\\d)[a-z\\d]{8,20}$"))
    }

    fun nicknameRegex(nickname: String): Boolean {
        val pattern = "^[a-zA-Zㄱ-힣]{2,6}\$".toRegex()
        return nickname.matches(pattern)
    }

    fun emailRegex(email: String): Boolean {
        val pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
        return email.matches(pattern)
    }

    fun phoneRegex(phone: String): Boolean {
        val pattern1 = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}\$".toRegex()
        val pattern2 = "^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}\$".toRegex()

        return phone.matches(pattern1) or phone.matches(pattern2)
    }

}
