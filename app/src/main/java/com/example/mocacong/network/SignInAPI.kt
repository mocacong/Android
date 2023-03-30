package com.example.mocacong.network

import com.example.mocacong.data.request.SignInRequest
import com.example.mocacong.data.response.SignInResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SignInAPI {
    @POST("/login")
    suspend fun signIn(@Body member: SignInRequest): Response<SignInResponse>
}