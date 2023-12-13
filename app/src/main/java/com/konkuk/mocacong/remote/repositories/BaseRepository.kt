package com.konkuk.mocacong.remote.repositories

import android.util.Log
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
                Log.d("Network", "API Succeed Response: $response")
                ApiState.Success(response.body())
            } else {
                val errorResponse = getErrorResponse(response.errorBody()!!)
                Log.e("Network", "API Error Response: $errorResponse")
                ApiState.Error(errorResponse = errorResponse!!)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ApiState.Error(errorResponse = ErrorResponse(message = e.message ?: "Unknown Error", code = 1234))
        }
    }


    private fun getErrorResponse(errorBody: ResponseBody): ErrorResponse? {
        return RetrofitClient.retrofit.responseBodyConverter<ErrorResponse>(
            ErrorResponse::class.java, ErrorResponse::class.java.annotations
        ).convert(errorBody)
    }
}