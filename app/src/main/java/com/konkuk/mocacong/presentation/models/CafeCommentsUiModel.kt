package com.konkuk.mocacong.presentation.models

import com.konkuk.mocacong.data.entities.Comment
import com.konkuk.mocacong.remote.models.response.CommentsResponse

data class CafeCommentsUiModel(
    val count: Int,
    val comments: MutableList<Comment>,
    var page: Int,
    var isEnd: Boolean = false
) {

    companion object {
        fun newInstance(page: Int, commentsResponse: CommentsResponse): CafeCommentsUiModel {
            return CafeCommentsUiModel(
                count = commentsResponse.count,
                comments = commentsResponse.comments as MutableList<Comment>,
                page = page,
                isEnd = commentsResponse.isEnd
            )
        }

        fun addComments(
            existingModel: CafeCommentsUiModel,
            page: Int,
            isEnd: Boolean,
            newComments: List<Comment>
        ): CafeCommentsUiModel {
            val updatedComments = existingModel.comments.apply {
                addAll(newComments)
            }
            return existingModel.copy(
                page = page,
                isEnd = isEnd,
                comments = updatedComments,
                count = existingModel.count
            )
        }

        fun addComments(
            existingModel: CafeCommentsUiModel,
            page: Int,
            commentsResponse: CommentsResponse
        ): CafeCommentsUiModel {
            return addComments(
                existingModel,
                page,
                commentsResponse.isEnd,
                commentsResponse.comments
            )
        }
    }
}
