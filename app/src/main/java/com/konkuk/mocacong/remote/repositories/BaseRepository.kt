package com.konkuk.mocacong.remote.repositories

import android.util.Log
import com.konkuk.mocacong.remote.models.response.ErrorResponse
import com.konkuk.mocacong.util.ApiState
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit

abstract class BaseRepository(private val retrofit: Retrofit) {

    protected suspend fun <T> makeRequest(call: suspend () -> Response<T>): ApiState<T> {
        val response = call()
        return if (response.isSuccessful) {
            Log.d("Network", "API Succeed Response: $response")
            ApiState.Success(response.body())
        } else {
            val errorResponse = getErrorResponse(response.errorBody()!!)
            Log.e("Network", "API Error Response: $errorResponse")
            ApiState.Error(errorResponse = errorResponse!!)
        }
    }


    private fun getErrorResponse(errorBody: ResponseBody): ErrorResponse? {
        return retrofit.responseBodyConverter<ErrorResponse>(
            ErrorResponse::class.java, ErrorResponse::class.java.annotations
        ).convert(errorBody)
    }
}