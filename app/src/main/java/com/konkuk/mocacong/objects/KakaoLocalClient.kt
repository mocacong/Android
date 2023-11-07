package com.konkuk.mocacong.objects

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object KakaoLocalClient {
    // Base URL 설정
    private const val BASE_URL = "https://dapi.kakao.com/"

    // Retrofit 객체 생성
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(provideOKHttpClient(AppInterceptor()))
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // API 인터페이스 반환
    fun <T> create(service: Class<T>): T {
        return retrofit.create(service)
    }

    private fun provideOKHttpClient(interceptor: AppInterceptor): OkHttpClient =
        OkHttpClient.Builder().run {
            addInterceptor(interceptor)
            build()
        }

    class AppInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
            val newRequest = request().newBuilder()
                .addHeader("Authorization", "KakaoAK 3e7c72e35c239c324a59419fa82812a4")
                .build()
            proceed(newRequest)
        }

    }
}