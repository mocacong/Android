package com.konkuk.mocacong.presentation.models

import android.view.View
import com.konkuk.mocacong.remote.models.response.CafePreviewResponse

data class CafePreviewUiModel(
    val favorite: Boolean,
    val score: String,
    val reviewsCount: String,
    val soloVisibility: Int,
    val groupVisibility: Int
) {

    val noVisibility: Int =
        if (soloVisibility == View.GONE && groupVisibility == View.GONE) View.VISIBLE else View.GONE

    companion object {
        fun responseToUIModel(cafePreviewResponse: CafePreviewResponse): CafePreviewUiModel {
            val reviewsCount =
                if (cafePreviewResponse.reviewsCount == 0) "리뷰가 없어요" else "${cafePreviewResponse.reviewsCount}개"
            val score = String.format(" X %.1f", cafePreviewResponse.score)
            var soloVisibility = View.GONE
            var groupVisibility = View.GONE
            when (cafePreviewResponse.studyType) {
                "solo" -> {
                    soloVisibility = View.VISIBLE
                }
                "group" -> {
                    groupVisibility = View.VISIBLE
                }
                else -> {
                    soloVisibility = View.VISIBLE
                    groupVisibility = View.VISIBLE
                }
            }

            return CafePreviewUiModel(
                favorite = cafePreviewResponse.favorite,
                score = score,
                reviewsCount = reviewsCount,
                soloVisibility, groupVisibility
            )
        }
    }
}