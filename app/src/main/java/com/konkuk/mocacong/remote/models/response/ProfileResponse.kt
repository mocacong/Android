package com.konkuk.mocacong.remote.models.response

data class ProfileResponse(
    val nickname: String,
    val imgUrl: String?,
    val email: String
){
    val nicknameString: String get() = "${nickname}님의 댓글 쓰기"
}