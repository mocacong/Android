package com.example.mocacong.network

import com.example.mocacong.data.objects.Member
import com.example.mocacong.data.response.ProfileResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface MyPageAPI {

    @GET("/members/mypage")
    suspend fun getMyReview(
        @Header("Authorization") token : String? = "Bearer ${Member.getAuthToken()}"
    ) : Response<ProfileResponse>

}