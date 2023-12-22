package com.konkuk.mocacong.data.entities

data class BasicPlaceInfo(
    val id: String,
    val name: String,
    val address: String?,
    val phone: String?
) : java.io.Serializable {
    val phoneString = if (phone.isNullOrBlank()) {
        "전화번호가 없어요"
    } else phone
    val addressString = if (address.isNullOrBlank()) {
        "주소 정보가 없어요"
    } else address
}