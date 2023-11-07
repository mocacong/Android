package com.konkuk.mocacong.remote.models.request

data class ReviewRequest(
    val myScore: Int?,
    val myStudyType: String?,
    val myWifi: String?,
    val myParking: String?,
    val myToilet: String?,
    val myPower: String?,
    val mySound: String?,
    val myDesk: String?
)
