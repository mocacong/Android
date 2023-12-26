package com.konkuk.mocacong.presentation.models

import com.konkuk.mocacong.remote.models.response.MyReviewResponse

data class ReviewsUiModel(
    val wifi: String?,
    val parking: String?,
    val toilet: String?,
    val power: String?,
    val sound: String?,
    val desk: String?,
) {
    companion object {
        fun responseToUIModel(myReviewResponse: MyReviewResponse): ReviewsUiModel {
            return ReviewsUiModel(
                wifi = myReviewResponse.myWifi,
                power = myReviewResponse.myPower,
                toilet = myReviewResponse.myToilet,
                sound = myReviewResponse.mySound,
                desk = myReviewResponse.myDesk,
                parking = myReviewResponse.myParking
            )
        }
    }
}