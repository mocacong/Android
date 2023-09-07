package com.konkuk.mocacong.repositories

import com.konkuk.mocacong.data.objects.KakaoLocalClient
import com.konkuk.mocacong.data.objects.NetworkUtil
import com.konkuk.mocacong.data.objects.RetrofitClient
import com.konkuk.mocacong.data.request.FilteringRequest
import com.konkuk.mocacong.data.response.CafePreviewResponse
import com.konkuk.mocacong.data.response.FilteringResponse
import com.konkuk.mocacong.data.response.LocalSearchResponse
import com.konkuk.mocacong.data.response.ProfileResponse
import com.konkuk.mocacong.data.util.ApiState
import com.konkuk.mocacong.network.KakaoSearchAPI
import com.konkuk.mocacong.network.MapApi
import com.konkuk.mocacong.network.MyPageAPI

class MapRepository {

    private val mapApi = RetrofitClient.create(MapApi::class.java)
    private val kakaoApi = KakaoLocalClient.create(KakaoSearchAPI::class.java)
    private val myPageApi = RetrofitClient.create(MyPageAPI::class.java)

    suspend fun getFilterings(
        studyType: String,
        filteringRequest: FilteringRequest
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

    suspend fun getFavorites(filteringRequest: FilteringRequest): ApiState<FilteringResponse> {
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