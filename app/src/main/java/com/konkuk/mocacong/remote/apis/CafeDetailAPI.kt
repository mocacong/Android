package com.konkuk.mocacong.remote.apis

import com.konkuk.mocacong.remote.models.response.CafeImageResponse
import com.konkuk.mocacong.remote.models.response.CafeResponse
import com.konkuk.mocacong.remote.models.response.CommentsResponse
import com.konkuk.mocacong.remote.models.response.MyReviewResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface CafeDetailAPI {

    @GET("/cafes/{cafeId}")
    suspend fun getCafeResponse(
        @Path("cafeId") cafeId: String
    ): Response<CafeResponse>


    @POST("/cafes/{cafeId}/comments")
    suspend fun postCafeComment(
        @Path("cafeId") cafeId: String,
        @Body content: String
    ): Response<Unit>

    @POST("/cafes/{cafeId}")
    suspend fun postReview(
        @Path("cafeId") cafeId: String,
        @Body myReview: com.konkuk.mocacong.remote.models.request.ReviewRequest
    ): Response<Unit>

    @GET("/cafes/{cafeId}/comments")
    suspend fun getCafeComment(
        @Path("cafeId") cafeId: String,
        @Query("page") page: Int = 0,
        @Query("count") count: Int = 15
    ): Response<CommentsResponse>

    @GET("/cafes/{cafeId}/me")
    suspend fun getMyReview(
        @Path("cafeId") cafeId: String,
    ): Response<MyReviewResponse>

    @PUT("/cafes/{cafeId}")
    suspend fun putReview(
        @Path("cafeId") cafeId: String,
        @Body myReview: com.konkuk.mocacong.remote.models.request.ReviewRequest
    ): Response<Unit>

    @POST("/cafes/{cafeId}/favorites")
    suspend fun postFavorite(
        @Path("cafeId") cafeId: String
    ): Response<Unit>

    @DELETE("/cafes/{cafeId}/favorites")
    suspend fun deleteFavorite(
        @Path("cafeId") cafeId: String
    ): Response<Unit>

    @GET("/cafes/{cafeId}/img")
    suspend fun getCafeImages(
        @Path("cafeId") cafeId: String,
        @Query("page") page: Int = 0,
        @Query("count") count: Int = 20
    ): Response<CafeImageResponse>

    @Multipart
    @POST("/cafes/{cafeId}/img")
    suspend fun postCafeImages(
        @Header("header") header: String = "MULTIPART_FORM_DATA_VALUE",
        @Path("cafeId") cafeId: String,
        @Part files: List<MultipartBody.Part>
    ): Response<Unit>

    @DELETE("/cafes/{cafeId}/comments/{commentId}")
    suspend fun deleteComment(
        @Path("cafeId") cafeId: String,
        @Path("commentId") commentId: String
    ): Response<Unit>

}