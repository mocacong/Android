package com.konkuk.mocacong.remote.apis

import com.konkuk.mocacong.remote.models.response.*
import retrofit2.Response
import retrofit2.http.*

interface MyPageAPI {
    @GET("/members/mypage")
    suspend fun getMyProfile(
    ): Response<ProfileResponse>

    @GET("/members/mypage/reviews")
    suspend fun getMyReviews(
        @Query("page") page: Int,
        @Query("count") count: Int = 20
    ): Response<MyReviewsResponse>

    @GET("/members/mypage/stars")
    suspend fun getMyFavorites(
        @Query("page") page: Int,
        @Query("count") count: Int = 20
    ): Response<MyFavResponse>

    @GET("/members/mypage/comments")
    suspend fun getMyComments(
        @Query("page") page: Int,
        @Query("count") count: Int = 20
    ): Response<MyCommentsResponse>

    @DELETE("/members")
    suspend fun withdrawMember(): Response<Void>

}