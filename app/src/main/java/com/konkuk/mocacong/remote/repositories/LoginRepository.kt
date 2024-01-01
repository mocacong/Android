package com.konkuk.mocacong.remote.repositories

import com.konkuk.mocacong.remote.apis.AuthAPI
import com.konkuk.mocacong.remote.apis.KakaoRequest
import com.konkuk.mocacong.remote.models.request.OAuthRequest
import com.konkuk.mocacong.remote.models.response.CheckDuplicateResponse
import com.konkuk.mocacong.remote.models.response.KakaoLoginResponse
import com.konkuk.mocacong.util.ApiState
import com.konkuk.mocacong.util.RetrofitClient

class LoginRepository : BaseRepository() {
    val api = RetrofitClient.create(AuthAPI::class.java)

    suspend fun postKakaoLogin(kakaoRequest: KakaoRequest): ApiState<KakaoLoginResponse> =
        makeRequest { api.kakaoLoginPost(kakaoRequest) }

    suspend fun postKakaoSignUp(oAuthRequest: OAuthRequest): ApiState<Unit> =
        makeRequest { api.oAuthSignUp(oAuthRequest) }

    suspend fun isNicknameDuplicate(nickname: String): ApiState<CheckDuplicateResponse> =
        makeRequest { api.checkNickname(nickname) }


}