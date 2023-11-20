package com.konkuk.mocacong.remote.repositories

import com.konkuk.mocacong.objects.RetrofitClient
import com.konkuk.mocacong.remote.apis.AuthAPI
import com.konkuk.mocacong.remote.apis.KakaoRequest
import com.konkuk.mocacong.remote.models.request.OAuthRequest
import com.konkuk.mocacong.remote.models.response.KakaoLoginResponse
import com.konkuk.mocacong.util.ApiState

class LoginRepository : BaseRepository() {
    val api = RetrofitClient.create(AuthAPI::class.java)

    suspend fun postKakaoLogin(kakaoRequest: KakaoRequest): ApiState<KakaoLoginResponse> =
        makeRequest { api.kakaoLoginPost(kakaoRequest) }

    suspend fun postKakaoSignUp(oAuthRequest: OAuthRequest): ApiState<Void> =
        makeRequest { api.oAuthSignUp(oAuthRequest) }


}