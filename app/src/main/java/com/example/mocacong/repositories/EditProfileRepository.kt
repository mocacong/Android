package com.example.mocacong.repositories

import com.example.mocacong.data.objects.NetworkUtil
import com.example.mocacong.data.objects.RetrofitClient
import com.example.mocacong.data.request.EditProfileRequest
import com.example.mocacong.data.response.ProfileResponse
import com.example.mocacong.data.util.ApiState
import com.example.mocacong.network.MembersAPI
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

     suspend fun putProfileInfo(info: EditProfileRequest): ApiState<Void> {
        val response = api.putProfileInfo(info = info)
        return if (response.isSuccessful) ApiState.Success(response.body())
        else {
            val errorResponse = NetworkUtil.getErrorResponse(response.errorBody()!!)
            ApiState.Error(errorResponse = errorResponse!!)
        }
    }


}