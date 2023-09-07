package com.konkuk.mocacong.network

import com.konkuk.mocacong.data.objects.Member
import com.konkuk.mocacong.data.request.FilteringRequest
import com.konkuk.mocacong.data.response.CafePreviewResponse
import com.konkuk.mocacong.data.response.FilteringResponse
import retrofit2.Response
import retrofit2.http.*

interface MapApi {
    @POST("/cafes/studytypes")
    suspend fun getFilteredCafes(
        @Header("Authorization") token: String? = "Bearer ${Member.getAuthToken()}",
        @Query("studytype") studyType: String,
        @Body filteringRequest: FilteringRequest
    ): Response<FilteringResponse>

    @POST("/cafes/favorites")
    suspend fun getFavCafes(
        @Header("Authorization") token: String? = "Bearer ${Member.getAuthToken()}",
        @Body filteringRequest: FilteringRequest
    ): Response<FilteringResponse>

    @GET("/cafes/{mapId}/preview")
    suspend fun getPreview(
        @Header("Authorization") token: String? = "Bearer ${Member.getAuthToken()}",
        @Path("mapId") cafeId: String,
    ): Response<CafePreviewResponse>

}