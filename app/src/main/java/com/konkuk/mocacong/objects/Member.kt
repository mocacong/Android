package com.konkuk.mocacong.objects

object Member {

    private var authToken: String? = null
    var email: String? = null
    var nickname: String? = ""
    var imgUrl: String? = null

    fun deleteInfo(){
        authToken = null
        email = null
        nickname = null
        imgUrl = null
    }

    fun setAuthToken(token: String) {
        authToken = token
    }

    fun getAuthToken(): String? {
        return authToken
    }

}