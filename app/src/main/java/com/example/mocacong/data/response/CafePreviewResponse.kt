package com.example.mocacong.data.response

data class CafePreviewResponse(
    val favorite: Boolean,
    val score: Float,
    val studyType: String?,
    val reviewsCount: Int
) : java.io.Serializable
