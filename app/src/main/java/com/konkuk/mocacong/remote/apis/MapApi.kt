package com.konkuk.mocacong.remote.apis

import com.konkuk.mocacong.objects.Member
import com.konkuk.mocacong.remote.models.response.CafePreviewResponse
import com.konkuk.mocacong.remote.models.response.FilteringResponse
import retrofit2.Response
import retrofit2.http.*

interface MapApi {
    @POST("/cafes/studytypes")
    suspend fun getFilteredCafes(
        @Header("Authorization") token: String? = "Bearer ${Member.getAuthToken()}",
        @Query("studytype") studyType: String,
        @Body filteringRequest: com.konkuk.mocacong.remote.models.request.FilteringRequest
    ): Response<FilteringResponse>

    @POST("/cafes/favorites")
    suspend fun getFavCafes(
        @Header("Authorization") token: String? = "Bearer ${Member.getAuthToken()}",
        @Body filteringRequest: com.konkuk.mocacong.remote.models.request.FilteringRequest
    ): Response<FilteringResponse>

    @GET("/cafes/{mapId}/preview")
    suspend fun getPreview(
        @Header("Authorization") token: String? = "Bearer ${Member.getAuthToken()}",
        @Path("mapId") cafeId: String,
    ): Response<CafePreviewResponse>

}