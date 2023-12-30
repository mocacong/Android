package com.konkuk.mocacong.util

import android.util.Log
import com.google.gson.GsonBuilder
import com.kakao.sdk.common.Constants.AUTHORIZATION
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "http://3.37.64.38:8080/"
//    private const val BASE_URL = "https://mocacong.com/"

    //interceptor 생성
    private val interceptorClient = OkHttpClient().newBuilder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .authenticator(TokenAuthenticator())
        .addInterceptor(ResponseInterceptor())
        .build()

    // Retrofit 객체 생성
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(interceptorClient)
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder()
                    .setLenient()
                    .create()
            )
        )
        .build()


    // API 인터페이스 반환
    fun <T> create(service: Class<T>): T {
        return retrofit.create(service)
    }
}

class ResponseInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val token : String? = runBlocking {
            TokenManager.getAccessToken().first()
        }

        val request = chain.request().newBuilder().header(AUTHORIZATION, "Bearer $token").build()

        val response = chain.proceed(request)
        return if (response.isSuccessful) response
        else {
            val errorJson = JSONObject(response.peekBody(2048).string())
            val code = errorJson.getInt("code")
            val message = errorJson.getString("message")
            Log.e("interceptor", "Network Error 발생! code = $code, message = $message")

            response
        }
    }
}