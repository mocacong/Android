package com.konkuk.mocacong.util

import com.konkuk.mocacong.remote.models.response.ErrorResponse

sealed class ApiState<T>(
) {
    class Success<T>(val data: T?) : ApiState<T>() {
        var hasBeenHandled = false
            private set

        fun getContentIfNotHandled(): T? {
            return if (hasBeenHandled) {
                null
            } else {
                hasBeenHandled = true
                data
            }
        }
    }

    class Error<T>(val errorResponse: ErrorResponse) : ApiState<T>()
    class Loading<T> : ApiState<T>()

    fun <R> byState(
        onSuccess: (T) -> (R?),
        onFailure: (ErrorResponse) -> (Unit) = {},
        onLoading: () -> (Unit) = {}
    ): R? {
        when (this) {
            is Success -> {
                return getContentIfNotHandled()?.let(onSuccess)
            }
            is Error -> {
                this.errorResponse.let { er ->
                    onFailure.invoke(er)
                }
            }
            is Loading -> {
                onLoading.invoke()
            }
        }
        return null
    }


}
