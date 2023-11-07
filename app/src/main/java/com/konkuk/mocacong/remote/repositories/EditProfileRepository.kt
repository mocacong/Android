package com.konkuk.mocacong.remote.repositories

import com.konkuk.mocacong.objects.NetworkUtil
import com.konkuk.mocacong.objects.RetrofitClient
import com.konkuk.mocacong.remote.apis.MembersAPI
import com.konkuk.mocacong.remote.models.response.ProfileResponse
import com.konkuk.mocacong.util.ApiState
import okhttp3.MultipartBody

class EditProfileRepository {

    private val api = RetrofitClient.create(MembersAPI::class.java)

     suspend fun sendImage(body: MultipartBody.Part): ApiState<Void> {
        val response = api.putMyProfileImage(file = body)
        return if (response.isSuccessful) ApiState.Success(response.body())
        else {
            val errorResponse = NetworkUtil.getErrorResponse(response.errorBody()!!)
            ApiState.Error(errorResponse = errorResponse!!)
        }
    }

     suspend fun getProfileInfo(): ApiState<ProfileResponse> {
        val response = api.getMyProfile()
        return if (response.isSuccessful) ApiState.Success(response.body())
        else {
            val errorResponse = NetworkUtil.getErrorResponse(response.errorBody()!!)
            ApiState.Error(errorResponse = errorResponse!!)
        }
    }

     suspend fun putProfileInfo(info: com.konkuk.mocacong.remote.models.request.EditProfileRequest): ApiState<Void> {
        val response = api.putProfileInfo(info = info)
        return if (response.isSuccessful) ApiState.Success(response.body())
        else {
            val errorResponse = NetworkUtil.getErrorResponse(response.errorBody()!!)
            ApiState.Error(errorResponse = errorResponse!!)
        }
    }


}