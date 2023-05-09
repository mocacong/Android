package com.example.mocacong.data.response

data class KakaoLoginResponse(
    val token : String,
    val email : String,
    val isRegistered : Boolean,
    val platformId : String
)
