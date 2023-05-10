package com.example.mocacong.network

import com.example.mocacong.data.objects.Member
import com.example.mocacong.data.request.CafeDetailRequest
import com.example.mocacong.data.response.CafeResponse
import com.example.mocacong.data.response.CommentsResponse
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

    @GET("/cafes/{cafeId}/comments")
    suspend fun getCafeComment(
        @Header("Authorization") token : String? = "Bearer ${Member.getAuthToken()}",
        @Path("cafeId") cafeId : String,
        @Query("page") page : Int = 0,
        @Query("count") count : Int = 3
    ) : Response<CommentsResponse>

}