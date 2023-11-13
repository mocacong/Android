package com.konkuk.mocacong.presentaion.login

import android.util.Log
import com.konkuk.mocacong.objects.Member
import com.konkuk.mocacong.objects.RetrofitClient
import com.konkuk.mocacong.remote.apis.SignInAPI
import com.konkuk.mocacong.remote.apis.SignUpAPI

class SignUpController() {

    val api = RetrofitClient.create(SignUpAPI::class.java)

    suspend fun signUp(member: com.konkuk.mocacong.remote.models.request.SignUpRequest): String {
        Log.d("dd", "signupfun called")
        //회원 가입 로직 처리 함수
        val response = api.signUp(member)
        return if (response.isSuccessful) {
            Log.d("signUp", "signUp SUCCESSED : ${response.code()}")
            "성공"
        } else {
            throw Exception("회원가입 에러!!")
        }

    }

    suspend fun emailConfirm(email: String): Boolean {
        Log.d("hi", "emailconfirm" + email)
        val response = api.checkEmail(email)
        if (response.isSuccessful) {
            return response.body()!!.result
        } else {
            throw java.lang.Exception("이메일 중복체크 실패!")
        }
    }

    suspend fun nicknameConfirm(nickname: String): Boolean {
        Log.d("hi", "nicknameConfirm" + nickname)
        val response = api.checkNickname(nickname)
        if (response.isSuccessful) {
            return response.body()!!.result
        } else {
            throw java.lang.Exception("닉네임 중복체크 실패!")
        }
    }

    suspend fun signIn(signInRequest: com.konkuk.mocacong.remote.models.request.SignInRequest): String {
        val response = RetrofitClient.create(SignInAPI::class.java).signIn(signInRequest)
        if (response.isSuccessful) {
            //로그인 성공
            try {
                Member.setAuthToken(response.body()!!.token)
            } catch (e: NullPointerException) {
                Log.d("signIn", "성공했는데 responseBody 없음")
            }
            return "로그인 성공";
        } else {
            //로그인 실패
            return response.message()
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

}
