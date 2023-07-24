package com.example.mocacong.network

import com.example.mocacong.data.objects.Member
import com.example.mocacong.data.request.CafeDetailRequest
import com.example.mocacong.data.request.ReviewRequest
import com.example.mocacong.data.response.CafeResponse
import com.example.mocacong.data.response.CommentsResponse
import com.example.mocacong.data.response.MyReviewResponse
import com.example.mocacong.data.response.ReviewResponse
import retrofit2.Response
import retrofit2.http.*

interface CafeDetailAPI {

    @GET("/cafes/{cafeId}")
    suspend fun getCafeResponse(
        @Header("Authorization") token : String? = "Bearer ${Member.getAuthToken()}",
        @Path("cafeId") cafeId : String
    ) : Response<CafeResponse>

    @POST("/cafes")
    suspend fun postCafe(
        @Body cafe : CafeDetailRequest
    ) : Response<Unit>

    @POST("/cafes/{cafeId}/comments")
    suspend fun postCafeComment(
        @Header("Authorization") token : String? = "Bearer ${Member.getAuthToken()}",
        @Path("cafeId") cafeId : String,
        @Body content : String
    ) : Response<Void>

    @POST("/cafes/{cafeId}")
    suspend fun postReview(
        @Header("Authorization") token: String? = "Bearer ${Member.getAuthToken()}",
        @Path("cafeId") cafeId: String,
        @Body myReview: ReviewRequest
    ) : Response<ReviewResponse>

    @GET("/cafes/{cafeId}/comments")
    suspend fun getCafeComment(
        @Header("Authorization") token : String? = "Bearer ${Member.getAuthToken()}",
        @Path("cafeId") cafeId : String,
        @Query("page") page : Int = 0,
        @Query("count") count : Int = 15
    ) : Response<CommentsResponse>

    @GET("/cafes/{cafeId}/me")
    suspend fun getMyReview(
        @Header("Authorization") token : String? = "Bearer ${Member.getAuthToken()}",
        @Path("cafeId") cafeId : String,
    ) : Response<MyReviewResponse>

    @PUT("/cafes/{cafeId}")
    suspend fun putReview(
        @Header("Authorization") token: String? = "Bearer ${Member.getAuthToken()}",
        @Path("cafeId") cafeId: String,
        @Body myReview: ReviewRequest
    ) : Response<ReviewResponse>

    @POST("/cafes/{cafeId}/favorites")
    suspend fun postFavorite(
        @Header("Authorization") token: String? = "Bearer ${Member.getAuthToken()}",
        @Path("cafeId") cafeId: String
    ) : Response<Void>

    @DELETE("/cafes/{cafeId}/favorites")
    suspend fun deleteFavorite(
        @Header("Authorization") token: String? = "Bearer ${Member.getAuthToken()}",
        @Path("cafeId") cafeId: String
    ) : Response<Void>


}