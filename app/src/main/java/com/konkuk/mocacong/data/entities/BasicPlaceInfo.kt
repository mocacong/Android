package com.konkuk.mocacong.data.entities

data class BasicPlaceInfo(
    val id: String,
    val name: String,
    val address: String,
    val phone: String
) : java.io.Serializable{
    val phoneString = phone.ifBlank { "전화번호가 없어요" }
    val addressString = address.ifBlank { "주소 정보를 불러올 수 없어요" }
}