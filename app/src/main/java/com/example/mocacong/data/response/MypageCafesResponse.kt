package com.example.mocacong.data.response

data class MypageCafesResponse(
    val isEnd: Boolean,
    val cafes: List<Cafe>
)

data class Cafe(
    val name: String,
    val myScore: Int?,
    val score: Int?
) {
    val mergedScore: Int
        get() = score ?: myScore ?: 0
}