package com.konkuk.mocacong.network

import com.konkuk.mocacong.data.response.LocalSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface KakaoSearchAPI {

    @GET("v2/local/search/keyword.json")
    suspend fun getKeywordSearchResponse(
        @Query("query") query : String,
        @Query("category_group_code") code: String = "CE7"
    ) : Response<LocalSearchResponse>


    @GET("v2/local/search/category.json")
    suspend fun getLocalSearchResponse(
        @Query("category_group_code") code : String = "CE7",
        @Query("x") x : String,
        @Query("y") y : String,
        @Query("radius") radius : Int = 500,
        @Query("page") page : Int = 1,
        @Query("size") size : Int = 15,
        @Query("sort") sort : String = "distance",
    ) : Response<LocalSearchResponse>
}