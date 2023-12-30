package com.konkuk.mocacong.remote.repositories

import com.konkuk.mocacong.objects.KakaoLocalClient
import com.konkuk.mocacong.remote.apis.KakaoSearchAPI
import com.konkuk.mocacong.remote.apis.MyPageAPI
import com.konkuk.mocacong.remote.models.response.LocalSearchResponse
import com.konkuk.mocacong.util.ApiState
import com.konkuk.mocacong.util.RetrofitClient

class MypageRepository : BaseRepository() {

    val mypageApi = RetrofitClient.create(MyPageAPI::class.java)
    private val kakaoApi = KakaoLocalClient.create(KakaoSearchAPI::class.java)

    suspend fun getMyComments(page: Int) =
        makeRequest { mypageApi.getMyComments(page = page) }

    suspend fun getMyReviews(page: Int) =
        makeRequest { mypageApi.getMyReviews(page = page) }

    suspend fun getMyFavs(page: Int) =
        makeRequest { mypageApi.getMyFavorites(page = page) }

    suspend fun getSearchResult(
        keyword: String
    ): ApiState<LocalSearchResponse> =
        makeRequest {
            kakaoApi.getKeywordSearchResponse(
                query = keyword,
                sort = "accuracy"
            )
        }

    suspend fun getMyProfile() =
        makeRequest { mypageApi.getMyProfile() }
}