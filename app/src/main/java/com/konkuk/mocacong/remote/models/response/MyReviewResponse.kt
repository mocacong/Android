package com.konkuk.mocacong.remote.models.response

data class MyReviewResponse(
    val myScore : Int,
    val myStudyType: String?,
    val myWifi: String?,
    val myParking: String?,
    val myToilet: String?,
    val myPower: String?,
    val mySound: String?,
    val myDesk: String?
)