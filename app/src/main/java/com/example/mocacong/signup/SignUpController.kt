package com.example.mocacong.signup

import android.util.Log
import com.example.mocacong.RetrofitClient
import com.example.mocacong.data.SignUpRequest
import com.example.mocacong.data.SignUpResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

class SignUpController(private val view: SignUpActivity) {

    interface SignUpAPI{
    @POST("/members")
    fun signUp(@Body member: SignUpRequest): Call<SignUpResponse>
    }

    fun signUp(member : SignUpRequest) {
        //회원 가입 로직 처리 함수

        //retrofit 객체 생성
        fun signUp(member: SignUpRequest) {

            val signUpApi = RetrofitClient.create(SignUpAPI::class.java)
            val call = signUpApi.signUp(member)

            call.enqueue(object: Callback<SignUpResponse> {
                override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                    if(response.isSuccessful) {
                        val id = response.body()?.id
                        // id 받음

                    }
                }
                override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                    // 실패
                }
            })

        }

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
