package com.konkuk.mocacong.remote.models.response

import android.graphics.Color

data class CafeResponse(
    val favorite: Boolean,
    val favoriteId: Long,
    val score: Double,
    val myScore: Int,
    val studyType: String,
    val reviewsCount: Int,
    val wifi: String?,
    val parking: String?,
    val toilet: String?,
    val power: String?,
    val sound: String?,
    val desk: String?,
    val commentsCount: Int,
    val comments: List<Comment>,
    val cafeImages: List<CafeImage>
) {
    val f_score: Float
        get() = score.toFloat()

    val wifiString: String
        get() = if (wifi == null) "리뷰를 등록해주세요" else "와이파이는 $wifi"

    val parkingString: String
        get() = if (parking == null) "리뷰를 등록해주세요" else "주차장은 $parking"

    val soundString: String
        get() =
            if (sound == null) "리뷰를 등록해주세요" else "분위기는 $sound"

    val powerString: String
        get() =
            if (power == null) "리뷰를 등록해주세요" else "콘센트는 $power"

    val deskString: String
        get() =
            if (desk == null) "리뷰를 등록해주세요" else "책상은 $desk"

    val toiletString: String
        get() =
            if (toilet == null) "리뷰를 등록해주세요" else "화장실은 $toilet"

    fun getRVStringColor(reviewString: String): Int {
        return if (reviewString == "리뷰를 등록해주세요") Color.parseColor("#BABABA")
        else Color.parseColor("#FF483F30")
    }

}

data class CafeImage(
    val id: Int, val imageUrl: String?, val isMe: Boolean
)
