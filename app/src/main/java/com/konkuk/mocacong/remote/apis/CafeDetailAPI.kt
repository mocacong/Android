package com.konkuk.mocacong.remote.apis

import com.konkuk.mocacong.objects.Member
import com.konkuk.mocacong.remote.models.response.CafeResponse
import com.konkuk.mocacong.remote.models.response.CommentsResponse
import com.konkuk.mocacong.remote.models.response.MyReviewResponse
import retrofit2.Response
import retrofit2.http.*

interface CafeDetailAPI {

    @GET("/cafes/{cafeId}")
    suspend fun getCafeResponse(
        @Header("Authorization") token : String? = "Bearer ${Member.getAuthToken()}",
        @Path("cafeId") cafeId : String
    ) : Response<CafeResponse>


    @POST("/cafes/{cafeId}/comments")
    suspend fun postCafeComment(
        @Header("Authorization") token : String? = "Bearer ${Member.getAuthToken()}",
        @Path("cafeId") cafeId : String,
        @Body content : String
    ) : Response<Unit>

    @POST("/cafes/{cafeId}")
    suspend fun postReview(
        @Header("Authorization") token: String? = "Bearer ${Member.getAuthToken()}",
        @Path("cafeId") cafeId: String,
        @Body myReview: com.konkuk.mocacong.remote.models.request.ReviewRequest
    ) : Response<Unit>

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
        @Body myReview: com.konkuk.mocacong.remote.models.request.ReviewRequest
    ) : Response<Unit>

    @POST("/cafes/{cafeId}/favorites")
    suspend fun postFavorite(
        @Header("Authorization") token: String? = "Bearer ${Member.getAuthToken()}",
        @Path("cafeId") cafeId: String
    ) : Response<Unit>

    @DELETE("/cafes/{cafeId}/favorites")
    suspend fun deleteFavorite(
        @Header("Authorization") token: String? = "Bearer ${Member.getAuthToken()}",
        @Path("cafeId") cafeId: String
    ) : Response<Unit>


}