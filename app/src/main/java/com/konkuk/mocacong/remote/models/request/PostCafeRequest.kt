package com.konkuk.mocacong.remote.models.request

data class PostCafeRequest(
    val mapId: String,
    val name: String,
    val roadAddress: String,
    val phoneNumber: String
)
