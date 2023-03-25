package com.example.mocacong.network

import com.example.mocacong.data.request.SignInRequest
import com.example.mocacong.data.response.SignInResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface SignInAPI {
    @POST("/members")
    fun signIn(@Body member: SignInRequest): Call<SignInResponse>
}