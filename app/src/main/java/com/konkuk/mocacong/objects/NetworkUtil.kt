package com.konkuk.mocacong.objects

import com.konkuk.mocacong.remote.models.response.ErrorResponse
import com.konkuk.mocacong.util.RetrofitClient
import okhttp3.ResponseBody

object NetworkUtil {
    fun getErrorResponse(errorBody: ResponseBody): ErrorResponse? {
        return RetrofitClient.retrofit.responseBodyConverter<ErrorResponse>(
            ErrorResponse::class.java,
            ErrorResponse::class.java.annotations
        ).convert(errorBody)
    }
}