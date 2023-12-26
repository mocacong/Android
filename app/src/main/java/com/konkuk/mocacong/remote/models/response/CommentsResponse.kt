package com.konkuk.mocacong.remote.models.response

import com.konkuk.mocacong.data.entities.Comment

data class CommentsResponse(
    val isEnd: Boolean,
    val count: Int?,
    val comments: List<Comment>
)

