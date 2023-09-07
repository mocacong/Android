package com.konkuk.mocacong.network

import com.konkuk.mocacong.data.request.SignInRequest
import com.konkuk.mocacong.data.response.KakaoLoginResponse
import com.konkuk.mocacong.data.response.SignInResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SignInAPI {
    @POST("/login")
    suspend fun signIn(@Body member: SignInRequest): Response<SignInResponse>

    @POST("/login/kakao")
    suspend fun kakaoLoginPost(@Body code : String?) : Response<KakaoLoginResponse>
}