package com.konkuk.mocacong.presentaion.main.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.mocacong.remote.models.response.ProfileResponse
import com.konkuk.mocacong.remote.repositories.EditProfileRepository
import com.konkuk.mocacong.util.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class EditProfileViewModel(private val editProfileRepository: EditProfileRepository) : ViewModel() {
    var mProfileInfoPUTFlow: MutableStateFlow<ApiState<Void>> =
        MutableStateFlow(ApiState.Loading())
    var profileInfoPUTFlow: StateFlow<ApiState<Void>> = mProfileInfoPUTFlow

    var mProfileGETFlow: MutableStateFlow<ApiState<ProfileResponse>> =
        MutableStateFlow(ApiState.Loading())
    var profileGETFlow: StateFlow<ApiState<ProfileResponse>> = mProfileGETFlow

    var mProfileImagePUTFlow: MutableStateFlow<ApiState<Void>> =
        MutableStateFlow(ApiState.Loading())
    var profileImagePUTFlow: StateFlow<ApiState<Void>> = mProfileImagePUTFlow

    fun putProfileInfo(info: com.konkuk.mocacong.remote.models.request.EditProfileRequest) =
        viewModelScope.launch(Dispatchers.IO) {
            mProfileInfoPUTFlow.value = ApiState.Loading()
            mProfileInfoPUTFlow.value = editProfileRepository.putProfileInfo(info)
        }

    fun putProfileImage(body: MultipartBody.Part) = viewModelScope.launch(Dispatchers.IO) {
        mProfileImagePUTFlow.value = ApiState.Loading()
        mProfileImagePUTFlow.value = editProfileRepository.sendImage(body)
    }

    fun requestProfileInfo() = viewModelScope.launch(Dispatchers.IO) {
        mProfileGETFlow.value = ApiState.Loading()
        mProfileGETFlow.value = editProfileRepository.getProfileInfo()
    }


}