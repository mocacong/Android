package com.konkuk.mocacong.remote.apis

import com.konkuk.mocacong.remote.models.request.FilteringRequest
import com.konkuk.mocacong.remote.models.request.PostCafeRequest
import com.konkuk.mocacong.remote.models.response.CafePreviewResponse
import com.konkuk.mocacong.remote.models.response.FilteringResponse
import retrofit2.Response
import retrofit2.http.*

interface MapApi {
    @POST("/cafes/studytypes")
    suspend fun getFilteredCafes(
        @Query("studytype") studyType: String,
        @Body filteringRequest: com.konkuk.mocacong.remote.models.request.FilteringRequest
    ): Response<FilteringResponse>

    @POST("/cafes/favorites")
    suspend fun getFavCafes(
        @Body filteringRequest: FilteringRequest
    ): Response<FilteringResponse>

    @POST("/cafes")
    suspend fun postCafe(
        @Body cafe : PostCafeRequest
    ) : Response<Unit>

    @GET("/cafes/{mapId}/preview")
    suspend fun getPreview(
        @Path("mapId") cafeId: String,
    ): Response<CafePreviewResponse>

}