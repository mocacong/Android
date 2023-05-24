package com.example.mocacong.network

import com.example.mocacong.data.objects.Member
import com.example.mocacong.data.response.CafeImageResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface CafeImagesAPI {

    @GET("/cafes/{cafeId}/img")
    suspend fun getCafeImagesResponse(
        @Header("Authorization") token : String? = "Bearer ${Member.getAuthToken()}",
        @Path("cafeId") cafeId : String,
        @Query("page") page : Int = 10,
        @Query("count") count : Int = 10
    ) : Response<CafeImageResponse>

    @Multipart
    @POST("/cafes/{cafeId}/img")
    suspend fun postCafeImage(
        @Header("Authorization") token: String? = "Bearer ${Member.getAuthToken()}",
        @Header("header") header: String = "MULTIPART_FORM_DATA_VALUE",
        @Path("cafeId") cafeId : String,
        @Part file: MultipartBody.Part
    ): Response<Void>
}