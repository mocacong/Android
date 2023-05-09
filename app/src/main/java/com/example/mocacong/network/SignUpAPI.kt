package com.example.mocacong.network

import com.example.mocacong.data.request.OAuthRequest
import com.example.mocacong.data.request.SignUpRequest
import com.example.mocacong.data.response.SignUpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SignUpAPI {
    @POST("/members")
    suspend fun signUp(@Body member: SignUpRequest): Response<SignUpResponse>

    @POST("/members/oauth")
    suspend fun oAuthSignUp(
        @Body request: OAuthRequest
    ): Response<Void>
}