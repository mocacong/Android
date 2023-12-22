package com.konkuk.mocacong.remote.models.response

data class KakaoLoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val email: String,
    val isRegistered: Boolean,
    val platformId: String,
    val userReportCount: Int
)
