package com.example.mocacong.network

import com.example.mocacong.data.objects.Member
import com.example.mocacong.data.request.EditProfileRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.PUT

interface MembersAPI {

    @DELETE("/members")
    suspend fun withdrawMember() : Response<Void>

    @PUT("/members")
    suspend fun editProfileInfo(
        @Header("Authorization") token: String? = "Bearer ${Member.getAuthToken()}",
        @Body info : EditProfileRequest
    ) : Response<Void>

}