package com.example.mocacong.repositories

import com.example.mocacong.data.objects.NetworkUtil
import com.example.mocacong.data.objects.RetrofitClient
import com.example.mocacong.data.response.BaseFlowResponse
import com.example.mocacong.data.response.CafeResponse
import com.example.mocacong.data.util.ApiState
import com.example.mocacong.network.CafeDetailAPI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CafeDetailRepository : BaseFlowResponse() {
    //flow를 생성하고 서버에 데이터 요청

    private val api = RetrofitClient.create(CafeDetailAPI::class.java)

    suspend fun getCafeDetailInfo(id: String): ApiState<CafeResponse> {
        val response = api.getCafeResponse(cafeId = id)
        return if (response.isSuccessful) ApiState.Success(response.body())
        else {
            val errorResponse = NetworkUtil.getErrorResponse(response.errorBody()!!)
            ApiState.Error(errorResponse = errorResponse!!)
        }
    }

    suspend fun postFavorite(id: String): ApiState<Void> {
        val response = api.postFavorite(cafeId = id)
        return if (response.isSuccessful) ApiState.Success(response.body())
        else {
            val errorResponse = NetworkUtil.getErrorResponse(response.errorBody()!!)
            ApiState.Error(errorResponse = errorResponse!!)
        }
    }

    suspend fun deleteFavorite(id: String): ApiState<Void> {
        val response = api.deleteFavorite(cafeId = id)
        return if (response.isSuccessful) ApiState.Success(response.body())
        else {
            val errorResponse = NetworkUtil.getErrorResponse(response.errorBody()!!)
            ApiState.Error(errorResponse = errorResponse!!)
        }
    }
}