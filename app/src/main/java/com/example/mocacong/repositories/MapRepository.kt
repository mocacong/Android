package com.example.mocacong.repositories

import com.example.mocacong.data.objects.RetrofitClient
import com.example.mocacong.data.request.FilteringRequest
import com.example.mocacong.data.response.BaseFlowResponse
import com.example.mocacong.data.response.FilteringResponse
import com.example.mocacong.data.util.ApiState
import com.example.mocacong.network.MapApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MapRepository : BaseFlowResponse() {

    private val api = RetrofitClient.create(MapApi::class.java)

    suspend fun getFilter(filteringRequest: FilteringRequest): Flow<ApiState<FilteringResponse>> = flow {
        emit(flowCall { api.getFavCafes(filteringRequest = filteringRequest) })
    }.flowOn(Dispatchers.IO)






}