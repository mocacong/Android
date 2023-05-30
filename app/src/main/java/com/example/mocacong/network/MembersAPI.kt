package com.example.mocacong.network

import retrofit2.Response
import retrofit2.http.DELETE

interface MembersAPI {

    @DELETE("/members")
    suspend fun withdrawMember() : Response<Void>

}