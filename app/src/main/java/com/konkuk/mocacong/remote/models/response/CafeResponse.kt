package com.konkuk.mocacong.remote.models.response

import com.konkuk.mocacong.data.entities.Comment

data class CafeResponse(
    val favorite: Boolean,
    val favoriteId: Long,
    val score: Float,
    val myScore: Int,
    val studyType: String?,
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
)

data class CafeImage(
    val id: Int, val imageUrl: String?, val isMe: Boolean
)
