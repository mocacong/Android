package com.example.mocacong.signup

import android.util.Log

class SignUpController(private val view: SignUpActivity) {

    fun signUp(member : SignUpData) : Boolean{
        //회원 가입 로직 처리 함수
        Log.d("hi","signup")
        return false
    }

    fun emailConfirm(email: String) : Boolean{
        Log.d("hi","emailconfirm"+email)
        return false
    }

    fun nicknameConfirm(nickname: String):Boolean{
        Log.d("hi","nickconfirm"+nickname)
        return false
    }


    fun passwordRegex(password: String): Boolean {
        return password.matches(Regex("^(?=.*[a-z])(?=.*\\d)[a-z\\d]{8,20}$"))
    }


}
