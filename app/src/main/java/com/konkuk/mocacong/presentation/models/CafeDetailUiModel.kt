package com.konkuk.mocacong.presentation.models

import android.view.View
import com.konkuk.mocacong.remote.models.response.CafeResponse
import com.konkuk.mocacong.remote.models.response.Comment

class CafeDetailUiModel(
    val score: Float,
    val studyType: String?,
    val reviewsCount: Int,
    val reviews: ReviewsUiModel,
    val commentsCount: Int,
    val comments: List<Comment?>
) {
    val studyTypeString: String = when (studyType) {
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
    val scoreString: String
    val reviewsCountString: String
    val commentsCountString: String
    val noCommentVisibility: Int = if (commentsCount > 0) View.GONE else View.VISIBLE
    val allCommentsVisibility: Int = if (commentsCount > 2) View.VISIBLE else View.GONE

    init {
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

            val comments = MutableList<Comment?>(3) { null }
            cafeResponse.comments.forEachIndexed { index, comment ->
                comments[index] = comment
            }

            return CafeDetailUiModel(
                cafeResponse.score,
                cafeResponse.studyType,
                cafeResponse.reviewsCount,
                reviews,
                cafeResponse.commentsCount,
                comments
            )
        }
    }

}

