package com.example.mocacong.network

import com.example.mocacong.data.request.OAuthRequest
import com.example.mocacong.data.request.SignUpRequest
import com.example.mocacong.data.response.CheckDuplicateResponse
import com.example.mocacong.data.response.SignUpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SignUpAPI {
    @POST("/members")
    suspend fun signUp(@Body member: SignUpRequest): Response<SignUpResponse>

    @POST("/members/oauth")
    suspend fun oAuthSignUp(
        @Body request: OAuthRequest
    ): Response<Void>

    @GET("/members/check-duplicate/nickname")
    suspend fun checkNickname(
        @Query("value") value : String
    ) : Response<CheckDuplicateResponse>

    @GET("/members/check-duplicate/email")
    suspend fun checkEmail(
        @Query("value") value : String
    ) : Response<CheckDuplicateResponse>
}