package com.konkuk.mocacong.remote.repositories

import com.konkuk.mocacong.remote.apis.MapApi
import com.konkuk.mocacong.remote.models.request.FilteringRequest
import com.konkuk.mocacong.remote.models.request.PostCafeRequest
import com.konkuk.mocacong.remote.models.response.CafePreviewResponse
import com.konkuk.mocacong.remote.models.response.FilteringResponse
import com.konkuk.mocacong.util.ApiState
import com.konkuk.mocacong.util.MocacongRetrofit
import retrofit2.Retrofit
import javax.inject.Inject

class MapRepository @Inject constructor(
    private val mapApi: MapApi,
    @MocacongRetrofit retrofit: Retrofit
) : BaseRepository(retrofit) {

    suspend fun filterStudyType(type: String, fr: FilteringRequest): ApiState<FilteringResponse> =
        makeRequest { mapApi.getFilteredCafes(studyType = type, filteringRequest = fr) }

    suspend fun filterFavorite(fr: FilteringRequest): ApiState<FilteringResponse> =
        makeRequest { mapApi.getFavCafes(filteringRequest = fr) }

    suspend fun postCafe(cafe: PostCafeRequest): ApiState<Unit> =
        makeRequest { mapApi.postCafe(cafe) }

    suspend fun getPreview(id: String): ApiState<CafePreviewResponse> =
        makeRequest { mapApi.getPreview(cafeId = id) }
}