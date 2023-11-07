package com.konkuk.mocacong.remote.apis

import com.konkuk.mocacong.objects.Member
import com.konkuk.mocacong.remote.models.response.MypageCafesResponse
import com.konkuk.mocacong.remote.models.response.ProfileResponse
import retrofit2.Response
import retrofit2.http.*

interface MyPageAPI {
    @GET("/members/mypage")
    suspend fun getMyProfile(
        @Header("Authorization") token: String? = "Bearer ${Member.getAuthToken()}"
    ): Response<ProfileResponse>

    @GET("/members/mypage/reviews")
    suspend fun getMyReviews(
        @Header("Authorization") token: String? = "Bearer ${Member.getAuthToken()}",
        @Query("page") page: Int,
        @Query("count") count: Int = 20
    ): Response<MypageCafesResponse>

    @GET("/members/mypage/stars")
    suspend fun getMyFavorites(
        @Header("Authorization") token: String? = "Bearer ${Member.getAuthToken()}",
        @Query("page") page: Int,
        @Query("count") count: Int = 20
    ): Response<MypageCafesResponse>

    @GET("/members/mypage/comments")
    suspend fun getMyComments(
        @Header("Authorization") token: String? = "Bearer ${Member.getAuthToken()}",
        @Query("page") page: Int,
        @Query("count") count: Int = 20
    ): Response<MypageCafesResponse>

    @DELETE("/members")
    suspend fun withdrawMember() : Response<Void>

}