package com.konkuk.mocacong.remote.models.request

data class OAuthRequest(
    val email: String, val nickname: String, val platform: String = "kakao", val platformId: String
)
