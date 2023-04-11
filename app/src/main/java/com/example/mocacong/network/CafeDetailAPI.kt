package com.example.mocacong.network

import com.example.mocacong.data.objects.Member
import com.example.mocacong.data.request.CafeDetailRequest
import com.example.mocacong.data.response.CafeResponse
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
}