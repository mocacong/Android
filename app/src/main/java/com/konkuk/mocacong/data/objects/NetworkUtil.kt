package com.konkuk.mocacong.data.objects

import com.konkuk.mocacong.data.response.ErrorResponse
import okhttp3.ResponseBody

object NetworkUtil {
    fun getErrorResponse(errorBody: ResponseBody): ErrorResponse? {
        return RetrofitClient.retrofit.responseBodyConverter<ErrorResponse>(
            ErrorResponse::class.java,
            ErrorResponse::class.java.annotations
        ).convert(errorBody)
    }
}