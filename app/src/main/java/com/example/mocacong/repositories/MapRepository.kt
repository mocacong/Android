package com.example.mocacong.repositories

import com.example.mocacong.data.objects.KakaoLocalClient
import com.example.mocacong.data.objects.NetworkUtil
import com.example.mocacong.data.objects.RetrofitClient
import com.example.mocacong.data.request.FilteringRequest
import com.example.mocacong.data.response.BaseFlowResponse
import com.example.mocacong.data.response.CafePreviewResponse
import com.example.mocacong.data.response.FilteringResponse
import com.example.mocacong.data.response.LocalSearchResponse
import com.example.mocacong.data.util.ApiState
import com.example.mocacong.network.KakaoSearchAPI
import com.example.mocacong.network.MapApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MapRepository {

    private val api = RetrofitClient.create(MapApi::class.java)
    private val kakaoApi = KakaoLocalClient.create(KakaoSearchAPI::class.java)
    suspend fun getFilterings(
        studyType: String,
        filteringRequest: FilteringRequest
    ): ApiState<FilteringResponse> {
        val response =
            api.getFilteredCafes(studyType = studyType, filteringRequest = filteringRequest)
        return if (response.isSuccessful) {
            ApiState.Success(response.body())
        } else {
            val errorResponse = NetworkUtil.getErrorResponse(response.errorBody()!!)
            ApiState.Error(errorResponse!!)
        }
    }

    suspend fun getFavorites(filteringRequest: FilteringRequest): ApiState<FilteringResponse> {
        val response = api.getFavCafes(filteringRequest = filteringRequest)
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

    suspend fun getKeywordBasedCafes(query: String): ApiState<LocalSearchResponse>  {
        val response =  kakaoApi.getKeywordSearchResponse(query = query)
        return if (response.isSuccessful) {
            ApiState.Success(response.body())
        } else {
            val errorResponse = NetworkUtil.getErrorResponse(response.errorBody()!!)
            ApiState.Error(errorResponse = errorResponse!!)
        }
    }

    suspend fun getPreviewInfo(cafeId: String): ApiState<CafePreviewResponse> {
       val response = api.getPreview(cafeId = cafeId)
        return if (response.isSuccessful) {
            ApiState.Success(response.body())
        } else {
            val errorResponse = NetworkUtil.getErrorResponse(response.errorBody()!!)
            ApiState.Error(errorResponse = errorResponse!!)
        }
    }

}