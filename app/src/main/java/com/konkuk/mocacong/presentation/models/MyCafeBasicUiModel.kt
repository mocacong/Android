package com.konkuk.mocacong.presentation.models

import android.view.View

class MyCafeBasicUiModel(
    val name: String,
    val mapId: String,
    val roadAddress: String?,
    val score: Double?,
    val studyType: String?
) {
    val addrString get() = if (roadAddress.isNullOrBlank()) "주소 정보 없음" else roadAddress.toString()
    val scoreString get() = String.format("X %.1f", score)

    var soloVisibility = View.GONE
    var groupVisibility = View.GONE

    init {
        when (studyType) {
            "solo" -> {
                soloVisibility = View.VISIBLE
            }
            "group" -> {
                groupVisibility = View.VISIBLE
            }
            "both" -> {
                soloVisibility = View.VISIBLE
                groupVisibility = View.VISIBLE
            }
            else -> {}
        }
    }
}