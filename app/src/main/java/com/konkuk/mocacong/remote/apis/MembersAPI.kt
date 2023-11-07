package com.konkuk.mocacong.remote.apis

import com.konkuk.mocacong.objects.Member
import com.konkuk.mocacong.remote.models.response.ProfileResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface MembersAPI {

    @GET("/members/mypage")
    suspend fun getMyProfile(
        @Header("Authorization") token: String? = "Bearer ${Member.getAuthToken()}"
    ): Response<ProfileResponse>

    @PUT("/members/info")
    suspend fun putProfileInfo(
        @Header("Authorization") token: String? = "Bearer ${Member.getAuthToken()}",
        @Body info : com.konkuk.mocacong.remote.models.request.EditProfileRequest
    ) : Response<Void>
    @Multipart
    @PUT("/members/mypage/img")
    suspend fun putMyProfileImage(
        @Header("Authorization") token: String? = "Bearer ${Member.getAuthToken()}",
        @Header("header") header: String = "MULTIPART_FORM_DATA_VALUE",
        @Part file: MultipartBody.Part
    ): Response<Void>

}