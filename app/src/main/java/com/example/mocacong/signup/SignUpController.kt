package com.example.mocacong.signup

import android.util.Log
import com.example.mocacong.RetrofitClient
import com.example.mocacong.data.request.SignUpRequest
import com.example.mocacong.data.response.SignUpResponse
import com.example.mocacong.network.SignUpAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class SignUpController(private val view: SignUpActivity) {

    fun signUp(member : SignUpRequest) {
        Log.d("dd","signupfun called")
        //회원 가입 로직 처리 함수
            val signUpApi = RetrofitClient.create(SignUpAPI::class.java)
            val call = signUpApi.signUp(member)

            call.enqueue(object: Callback<SignUpResponse> {
                override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                    if(response.isSuccessful) {
                        val id = response.body()?.id
                        // id 받음
                        Log.d("dd","Success id: "+id.toString())
                    }
                }
                override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                    // 실패
                    Log.d("dd", "Failure code: "+t.message)
                }
            })

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
