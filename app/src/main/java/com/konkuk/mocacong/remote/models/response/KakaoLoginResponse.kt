package com.konkuk.mocacong.remote.models.response

data class KakaoLoginResponse(
    val token : String,
    val email : String,
    val isRegistered : Boolean,
    val platformId : String
)
