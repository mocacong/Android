package com.example.mocacong.data.response

import com.example.mocacong.data.objects.NetworkUtil
import com.example.mocacong.data.util.ApiState
import retrofit2.Response
import java.io.IOException

abstract class BaseFlowResponse {

    suspend fun <T> flowCall(apiCall: suspend () -> Response<T>): ApiState<T> {
        try {
            val response = apiCall.invoke()
            var result : ApiState<T> = ApiState.Loading<T>()
            if (response.isSuccessful) {
               result = ApiState.Success(response.body())
            } else {
                try {
                    val errorResponse = NetworkUtil.getErrorResponse(response.errorBody()!!)
                    result = ApiState.Error<T>(errorResponse = errorResponse!!)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return result
        } catch (e: Exception) {
            return ApiState.Error<T>(ErrorResponse(code = 0, "getCafeDetailInfo Error!"))
        }

    }
}

