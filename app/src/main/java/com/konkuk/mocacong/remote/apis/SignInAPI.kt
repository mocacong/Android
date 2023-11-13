package com.konkuk.mocacong.remote.apis

import com.konkuk.mocacong.remote.models.response.KakaoLoginResponse
import com.konkuk.mocacong.remote.models.response.SignInResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class KakaoRequest(val email: String, val platformId: String)
interface SignInAPI {
    @POST("/login")
    suspend fun signIn(@Body member: com.konkuk.mocacong.remote.models.request.SignInRequest): Response<SignInResponse>

    @POST("/login/kakao")
    suspend fun kakaoLoginTest(@Body req: KakaoRequest): Response<KakaoLoginResponse>

    @POST("/login/kakao")
    suspend fun kakaoLoginPost(@Body token: String?): Response<KakaoLoginResponse>
}