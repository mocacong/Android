package com.konkuk.mocacong.remote.models.response

import android.util.Log
import com.konkuk.mocacong.objects.NetworkUtil
import com.konkuk.mocacong.util.ApiState
import retrofit2.Response
import java.io.IOException

abstract class BaseFlowResponse {

    suspend fun <T> flowCall(apiCall: suspend () -> Response<T>): ApiState<T> {
        try {
            val response = apiCall.invoke()
            var result : ApiState<T> = ApiState.Loading<T>()
            if (response.isSuccessful) {
                Log.d("Map","FlowCall 호출됨. response successful\n${response.body().toString()}")
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
            return ApiState.Error<T>(
                com.konkuk.mocacong.remote.models.response.ErrorResponse(
                    code = 0,
                    "getCafeDetailInfo Error!"
                )
            )
        }

    }
}

