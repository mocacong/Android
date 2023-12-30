package com.konkuk.mocacong.remote.repositories

import com.konkuk.mocacong.objects.KakaoLocalClient
import com.konkuk.mocacong.util.RetrofitClient
import com.konkuk.mocacong.remote.apis.KakaoSearchAPI
import com.konkuk.mocacong.remote.apis.MapApi
import com.konkuk.mocacong.remote.models.request.FilteringRequest
import com.konkuk.mocacong.remote.models.request.PostCafeRequest
import com.konkuk.mocacong.remote.models.response.CafePreviewResponse
import com.konkuk.mocacong.remote.models.response.FilteringResponse
import com.konkuk.mocacong.remote.models.response.LocalSearchResponse
import com.konkuk.mocacong.util.ApiState

class MapRepository : BaseRepository() {

    private val mapApi = RetrofitClient.create(MapApi::class.java)
    private val kakaoApi = KakaoLocalClient.create(KakaoSearchAPI::class.java)

    suspend fun getPlaces(x: String, y: String, radius: Int): ApiState<LocalSearchResponse> =
        makeRequest { kakaoApi.getLocalSearchResponse(x = x, y = y, radius = radius) }

    suspend fun getSearchResult(
        x: String,
        y: String,
        keyword: String
    ): ApiState<LocalSearchResponse> =
        makeRequest { kakaoApi.getKeywordSearchResponse(query = keyword, x = x, y = y) }

    suspend fun filterStudyType(type: String, fr: FilteringRequest): ApiState<FilteringResponse> =
        makeRequest { mapApi.getFilteredCafes(studyType = type, filteringRequest = fr) }

    suspend fun filterFavorite(fr: FilteringRequest): ApiState<FilteringResponse> =
        makeRequest { mapApi.getFavCafes(filteringRequest = fr) }

    suspend fun postCafe(cafe: PostCafeRequest): ApiState<Unit> =
        makeRequest { mapApi.postCafe(cafe) }

    suspend fun getPreview(id: String): ApiState<CafePreviewResponse> =
        makeRequest { mapApi.getPreview(cafeId = id) }
}