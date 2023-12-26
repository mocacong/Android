package com.konkuk.mocacong.remote.models.response

import com.konkuk.mocacong.presentation.models.MyCafeBasicUiModel

data class MyReviewsResponse(
    val isEnd: Boolean,
    val cafes: List<MyReviews>
)

data class MyCommentsResponse(
    val isEnd: Boolean,
    val cafes: List<MyComments>
)

data class MyFavResponse(
    val isEnd: Boolean,
    val cafes: List<MyFavorites>
)

data class MyReviews(
    val name: String,
    val mapId: String,
    val roadAddress: String?,
    val myStudyType: String,
    val myScore: Int,
    val myWifi: String?,
    val myParking: String?,
    val myToilet: String?,
    val myPower: String?,
    val mySound: String?,
    val myDesk: String?,
){
    fun toBasicModel() : MyCafeBasicUiModel{
        return MyCafeBasicUiModel(
            name, mapId, roadAddress, myScore.toDouble(), myStudyType
        )
    }
}


data class MyFavorites(
    val name: String,
    val mapId: String,
    val roadAddress: String?,
    val score: Double?,
    val studyType: String?
) {
    fun toBasicModel() : MyCafeBasicUiModel{
        return MyCafeBasicUiModel(
            name, mapId, roadAddress, score, studyType
        )
    }
}

data class MyComments(
    val name: String,
    val mapId: String,
    val roadAddress: String?,
    val commentContents: List<String>,
    val studyType: String?,
    val score: Double
) {
    fun toBasicModel() : MyCafeBasicUiModel{
        return MyCafeBasicUiModel(
            name, mapId, roadAddress, score, studyType
        )
    }
}

