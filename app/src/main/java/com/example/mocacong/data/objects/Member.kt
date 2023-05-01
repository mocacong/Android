package com.example.mocacong.data.objects

object Member{

    private var authToken: String? = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqc3NoaW4wMDAxMDdAbmF2ZXIuY29tIiwiaWF0IjoxNjgyMDgwNDA2LCJleHAiOjE2ODIxNjY4MDZ9.vYKDnDIa8dbT8fW37FeyEZioBofpf5iGwGRMGXROe5g"
    fun setAuthToken(token: String) {
        authToken = token
    }

    fun getAuthToken(): String? {
        return authToken
    }

}