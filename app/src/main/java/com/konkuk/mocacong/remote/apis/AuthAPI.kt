package com.konkuk.mocacong.remote.apis

import com.konkuk.mocacong.remote.models.request.OAuthRequest
import com.konkuk.mocacong.remote.models.request.SignUpRequest
import com.konkuk.mocacong.remote.models.response.CheckDuplicateResponse
import com.konkuk.mocacong.remote.models.response.KakaoLoginResponse
import com.konkuk.mocacong.remote.models.response.SignInResponse
import com.konkuk.mocacong.remote.models.response.SignUpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

data class KakaoRequest(val email: String, val platformId: String)
interface AuthAPI {
    @POST("/login")
    suspend fun signIn(@Body member: com.konkuk.mocacong.remote.models.request.SignInRequest): Response<SignInResponse>

    @POST("/login/kakao")
    suspend fun kakaoLoginPost(@Body req: KakaoRequest): Response<KakaoLoginResponse>

    @POST("/members")
    suspend fun signUp(@Body member: SignUpRequest): Response<SignUpResponse>

    @POST("/members/oauth")
    suspend fun oAuthSignUp(@Body request: OAuthRequest): Response<Unit>

    @GET("/members/check-duplicate/nickname")
    suspend fun checkNickname(
        @Query("value") value: String
    ): Response<CheckDuplicateResponse>

    @GET("/members/check-duplicate/email")
    suspend fun checkEmail(
        @Query("value") value: String
    ): Response<CheckDuplicateResponse>

}