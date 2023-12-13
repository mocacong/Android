package com.konkuk.mocacong.remote.models.response

data class MyReviewsResponse(
    val isEnd: Boolean,
    val cafes: List<MyCafe.MyReviews>
)

data class MyCommentsResponse(
    val isEnd: Boolean,
    val cafes: List<MyCafe.MyComments>
)

data class MyFavResponse(
    val isEnd: Boolean,
    val cafes: List<MyCafe.MyFavorites>
)


sealed class MyCafe(
    open val name: String,
    open val mapId: String,
    open val roadAddress: String?,
    open val score: Double?
) {
    val addrString get() = if(roadAddress.isNullOrBlank()) "주소 정보 없음" else roadAddress.toString()

    class MyReviews(
        override val name: String, override val mapId: String, override val roadAddress: String?,
        val myStudyType: String,
        val myScore: Int,
        val wifi: String?,
        val parking: String?,
        val toilet: String?,
        val power: String?,
        val sound: String?,
        val desk: String?,
    ) : MyCafe(name, mapId, roadAddress, myScore.toDouble())

    class MyFavorites(
        override val name: String, override val mapId: String, override val roadAddress: String?,
        override val score: Double?
    ) : MyCafe(name, mapId, roadAddress, score)

    class MyComments(
        override val name: String, override
        val mapId: String, override
        val roadAddress: String?,
        val comment: List<String>,
        val studyType: String?,
        override val score: Double
    ) : MyCafe(name, mapId, roadAddress, score)
}