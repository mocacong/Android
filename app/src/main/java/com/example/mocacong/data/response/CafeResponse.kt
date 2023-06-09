package com.example.mocacong.data.response

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
)
data class CafeImage(
    val id : Int,
    val imageUrl : String?,
    val isMe: Boolean
)

data class Comment(
    val imgUrl: String?,
    val nickname: String?,
    val content: String,
    val isMe: Boolean
)