package com.konkuk.mocacong.presentation.models

import com.konkuk.mocacong.remote.models.response.CafeResponse

class CafeDetailUiModel(
    val score: Float,
    val studyType: String?,
    val reviewsCount: Int,
    val reviews: ReviewsUiModel,
    val commentsCount: Int
) {
    val studyTypeString: String
    val scoreString: String
    val reviewsCountString: String
    val commentsCountString: String

    init {
        studyTypeString = when (studyType) {
            "solo" -> {
                "#혼자"
            }
            "both" -> {
                "#혼자 #같이"
            }
            "group" -> {
                "#같이"
            }
            else -> {
                ""
            }
        }
        scoreString = String.format("%.1f / 5", score)
        reviewsCountString = "리뷰 ${reviewsCount}개"
        commentsCountString = "댓글 ${commentsCount}개"
    }

    companion object {
        fun responseToModel(cafeResponse: CafeResponse): CafeDetailUiModel {
            val reviews = ReviewsUiModel(
                wifi = cafeResponse.wifi,
                parking = cafeResponse.parking,
                toilet = cafeResponse.toilet,
                power = cafeResponse.power,
                sound = cafeResponse.sound,
                desk = cafeResponse.desk
            )
            return CafeDetailUiModel(
                cafeResponse.score,
                cafeResponse.studyType,
                cafeResponse.reviewsCount,
                reviews,
                cafeResponse.commentsCount
            )
        }
    }

}

