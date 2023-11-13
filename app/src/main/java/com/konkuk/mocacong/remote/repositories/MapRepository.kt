package com.konkuk.mocacong.remote.repositories

import com.konkuk.mocacong.objects.KakaoLocalClient
import com.konkuk.mocacong.objects.NetworkUtil
import com.konkuk.mocacong.objects.RetrofitClient
import com.konkuk.mocacong.remote.apis.KakaoSearchAPI
import com.konkuk.mocacong.remote.apis.MapApi
import com.konkuk.mocacong.remote.apis.MyPageAPI
import com.konkuk.mocacong.remote.models.response.CafePreviewResponse
import com.konkuk.mocacong.remote.models.response.FilteringResponse
import com.konkuk.mocacong.remote.models.response.LocalSearchResponse
import com.konkuk.mocacong.remote.models.response.ProfileResponse
import com.konkuk.mocacong.util.ApiState

class MapRepository {

    private val mapApi = RetrofitClient.create(MapApi::class.java)
    private val kakaoApi = KakaoLocalClient.create(KakaoSearchAPI::class.java)
    private val myPageApi = RetrofitClient.create(MyPageAPI::class.java)

    suspend fun getFilterings(
        studyType: String,
        filteringRequest: com.konkuk.mocacong.remote.models.request.FilteringRequest
    ): ApiState<FilteringResponse> {
        val response =
            mapApi.getFilteredCafes(studyType = studyType, filteringRequest = filteringRequest)
        return if (response.isSuccessful) {
            ApiState.Success(response.body())
        } else {
            val errorResponse = NetworkUtil.getErrorResponse(response.errorBody()!!)
            ApiState.Error(errorResponse!!)
        }
    }

    suspend fun getFavorites(filteringRequest: com.konkuk.mocacong.remote.models.request.FilteringRequest): ApiState<FilteringResponse> {
        val response = mapApi.getFavCafes(filteringRequest = filteringRequest)
        return if (response.isSuccessful) {
            ApiState.Success(response.body())
        } else {
            val errorResponse = NetworkUtil.getErrorResponse(response.errorBody()!!)
            ApiState.Error(errorResponse!!)
        }
    }

    suspend fun getLocationBasedCafes(mapx: String, mapy: String): ApiState<LocalSearchResponse> {
        val response = kakaoApi.getLocalSearchResponse(x = mapx, y = mapy)
        if (response.isSuccessful) {
            return ApiState.Success(response.body())
        } else {
            val errorResponse = NetworkUtil.getErrorResponse(response.errorBody()!!)
            return ApiState.Error(errorResponse = errorResponse!!)
        }
    }


    suspend fun getPreviewInfo(cafeId: String): ApiState<CafePreviewResponse> {
        val response = mapApi.getPreview(cafeId = cafeId)
        return if (response.isSuccessful) {
            ApiState.Success(response.body())
        } else {
            val errorResponse = NetworkUtil.getErrorResponse(response.errorBody()!!)
            ApiState.Error(errorResponse = errorResponse!!)
        }
    }

    suspend fun getProfileInfo(): ApiState<ProfileResponse> {
        val response = myPageApi.getMyProfile()
        return if (response.isSuccessful) ApiState.Success(response.body())
        else {
            val errorResponse = NetworkUtil.getErrorResponse(response.errorBody()!!)
            ApiState.Error(errorResponse = errorResponse!!)
        }
    }

}