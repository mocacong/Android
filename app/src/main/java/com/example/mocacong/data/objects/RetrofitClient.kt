package com.example.mocacong.data.objects

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {


    private const val BASE_URL = "http://3.37.64.38:8080/"
//    private const val BASE_URL = "http://localhost:8080/"
//    private const val BASE_URL = "http://mocacong.com/"

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