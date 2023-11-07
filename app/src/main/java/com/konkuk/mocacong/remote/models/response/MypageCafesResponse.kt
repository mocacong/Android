package com.konkuk.mocacong.remote.models.response

data class MypageCafesResponse(
    val isEnd: Boolean,
    val cafes: List<Cafe>
)

data class Cafe(
    val name: String,
    val mapId : String,
    val myScore: Float?,
    val score: Float?,
    val comment : String?
) {
    val mergedScore: Float
        get() = score ?: myScore ?: 0f
}