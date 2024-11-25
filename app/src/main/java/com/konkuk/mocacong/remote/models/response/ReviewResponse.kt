package com.konkuk.mocacong.remote.models.response

data class ReviewResponse (
    val score : Float,
    val studyType: String?,
    val wifi: String?,
    val parking: String?,
    val toilet: String?,
    val power: String?,
    val sound: String?,
    val desk: String?,
    val reviewsCount : Int
)