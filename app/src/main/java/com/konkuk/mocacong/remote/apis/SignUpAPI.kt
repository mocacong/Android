package com.konkuk.mocacong.remote.apis

import com.konkuk.mocacong.remote.models.response.CheckDuplicateResponse
import com.konkuk.mocacong.remote.models.response.SignUpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SignUpAPI {
    @POST("/members")
    suspend fun signUp(@Body member: com.konkuk.mocacong.remote.models.request.SignUpRequest): Response<SignUpResponse>

    @POST("/members/oauth")
    suspend fun oAuthSignUp(
        @Body request: com.konkuk.mocacong.remote.models.request.OAuthRequest
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