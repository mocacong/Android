package com.example.mocacong.network

import com.example.mocacong.data.objects.Member
import com.example.mocacong.data.request.EditProfileRequest
import com.example.mocacong.data.response.MypageCafesResponse
import com.example.mocacong.data.response.ProfileResponse
import okhttp3.MultipartBody
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