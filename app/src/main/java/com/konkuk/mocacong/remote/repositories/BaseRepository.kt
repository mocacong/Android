package com.konkuk.mocacong.remote.repositories

import com.konkuk.mocacong.objects.RetrofitClient
import com.konkuk.mocacong.remote.models.response.ErrorResponse
import com.konkuk.mocacong.util.ApiState
import okhttp3.ResponseBody
import retrofit2.Response

abstract class BaseRepository {

    protected suspend fun <T> makeRequest(call: suspend () -> Response<T>): ApiState<T> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                ApiState.Success(response.body()!!)
            } else {
                val errorResponse = getErrorResponse(response.errorBody()!!)
                ApiState.Error(errorResponse = errorResponse!!)
            }
        } catch (e: Exception) {
            ApiState.Error(errorResponse = ErrorResponse(message = e.message ?: "Unknown Error", code = 9999))
        }
    }

    private fun getErrorResponse(errorBody: ResponseBody): ErrorResponse? {
        return RetrofitClient.retrofit.responseBodyConverter<ErrorResponse>(
            ErrorResponse::class.java, ErrorResponse::class.java.annotations
        ).convert(errorBody)
    }
}