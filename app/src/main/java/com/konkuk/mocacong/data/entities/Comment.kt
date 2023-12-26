package com.konkuk.mocacong.data.entities

data class Comment(
    val id: Long,
    val imgUrl: String?,
    val nickname: String? = "알 수 없음",
    val content: String,
    val likeCount: Int,
    var isMe: Boolean
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Comment
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
