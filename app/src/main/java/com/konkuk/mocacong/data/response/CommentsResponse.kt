package com.konkuk.mocacong.data.response

data class CommentsResponse(
    val isEnd: Boolean,
    val count: Int?,
    val comments: List<Comment>
)

data class Comment(
    val id: Long,
    val imgUrl: String?,
    val nickname: String?,
    val content: String,
    val isMe: Boolean
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
