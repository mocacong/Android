package com.example.mocacong.network

import com.example.mocacong.data.request.SignUpRequest
import com.example.mocacong.data.response.SignUpResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface SignUpAPI{
    @POST("/members")
    fun signUp(@Body member: SignUpRequest): Call<SignUpResponse>
}