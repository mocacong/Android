package com.example.mocacong.repositories

import com.example.mocacong.data.objects.RetrofitClient
import com.example.mocacong.data.response.BaseFlowResponse
import com.example.mocacong.data.response.CafeResponse
import com.example.mocacong.data.util.ApiState
import com.example.mocacong.network.CafeDetailAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class CafeDetailRepository : BaseFlowResponse() {
    //flow를 생성하고 서버에 데이터 요청

    private val api = RetrofitClient.create(CafeDetailAPI::class.java)

    suspend fun getCafeDetailInfo(id: String): Flow<ApiState<CafeResponse>> = flow {
        emit(flowCall { api.getCafeResponse(cafeId = id) })
    }.flowOn(Dispatchers.IO)

    suspend fun postFavorite(id: String) : Flow<ApiState<Void>> = flow {
        emit(flowCall { api.postFavorite(cafeId = id) })
    }

    suspend fun deleteFavorite(id: String) : Flow<ApiState<Void>> = flow {
        emit(flowCall { api.deleteFavorite(cafeId = id) })
    }
}