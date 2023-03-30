package com.example.mocacong

object Member{

    private var authToken: String? = null

    fun setAuthToken(token: String) {
        authToken = token
    }

    fun getAuthToken(): String? {
        return authToken
    }

}