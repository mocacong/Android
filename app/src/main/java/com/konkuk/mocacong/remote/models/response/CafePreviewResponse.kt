package com.konkuk.mocacong.remote.models.response

data class CafePreviewResponse(
    val favorite: Boolean,
    val score: Float,
    val studyType: String?,
    val reviewsCount: Int
) : java.io.Serializable
