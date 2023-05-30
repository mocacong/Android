package com.example.mocacong.data.objects

object Member {

    private var authToken: String? = null
    var email: String? = null
    var nickname: String? = ""
    var phone: String? = ""
    var imgUrl: String? = null

    fun deleteInfo(){
        authToken = null
        email = null
        nickname = null
        phone = null
        imgUrl = null
    }

    fun setAuthToken(token: String) {
        authToken = token
    }

    fun getAuthToken(): String? {
        return authToken
    }

}