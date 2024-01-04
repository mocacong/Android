package com.konkuk.mocacong.remote.repositories

import com.konkuk.mocacong.remote.apis.KakaoSearchAPI
import com.konkuk.mocacong.remote.models.response.LocalSearchResponse
import com.konkuk.mocacong.util.ApiState
import com.konkuk.mocacong.util.KakaoRetrofit
import retrofit2.Retrofit
import javax.inject.Inject

class KakaoRepository @Inject constructor(
    private val kakaoApi: KakaoSearchAPI,
    @KakaoRetrofit retrofit: Retrofit
) : BaseRepository(retrofit) {


    suspend fun getPlaces(x: String, y: String, radius: Int): ApiState<LocalSearchResponse> =
        makeRequest { kakaoApi.getLocalSearchResponse(x = x, y = y, radius = radius) }

    suspend fun getSearchResult(
        x: String,
        y: String,
        keyword: String
    ): ApiState<LocalSearchResponse> =
        makeRequest { kakaoApi.getKeywordSearchResponse(query = keyword, x = x, y = y) }

    suspend fun getMyPageSearchResult(
        keyword: String
    ): ApiState<LocalSearchResponse> =
        makeRequest {
            kakaoApi.getKeywordSearchResponse(
                query = keyword,
                sort = "accuracy"
            )
        }
}