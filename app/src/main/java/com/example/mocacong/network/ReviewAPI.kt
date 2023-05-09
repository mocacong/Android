package com.example.mocacong.network

import com.example.mocacong.data.objects.Member
import com.example.mocacong.data.request.ReviewRequest
import com.example.mocacong.data.response.ReviewResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ReviewAPI {

    @POST("/cafes/{cafeId}")
    suspend fun postReview(
        @Header("Authorization") token: String? = "Bearer ${Member.getAuthToken()}",
        @Path("cafeId") cafeId: String,
        @Body myReview: ReviewRequest
    ) : Response<ReviewResponse>
}