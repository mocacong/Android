package com.example.mocacong

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Base URL 설정
    private const val BASE_URL = "http://13.124.3.176:8080/"

    // Retrofit 객체 생성
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // API 인터페이스 반환
    fun <T> create(service: Class<T>): T {
        return retrofit.create(service)
    }

}