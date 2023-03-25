package com.example.mocacong.signin

import android.util.Log
import com.example.mocacong.RetrofitClient
import com.example.mocacong.data.request.SignInRequest
import com.example.mocacong.data.response.SignInResponse
import com.example.mocacong.network.SignInAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInController {

    fun signIn(signInRequest: SignInRequest){
        Log.d("signIn", "signIn function Called")

        val signInAPI = RetrofitClient.create(SignInAPI::class.java)
        val call = signInAPI.signIn(signInRequest)

        call.enqueue(object :Callback<SignInResponse>{
            override fun onResponse(
                call: Call<SignInResponse>,
                response: Response<SignInResponse>
            ) {
                Log.d("signIn", "signIn http Success Token:"+response.body()?.token)
            }

            override fun onFailure(call: Call<SignInResponse>, t: Throwable) {
                Log.d("signIn", "signIn http Failed :"+t.message)
            }

        })
    }

}