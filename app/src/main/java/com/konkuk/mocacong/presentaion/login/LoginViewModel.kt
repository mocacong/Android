package com.konkuk.mocacong.presentaion.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.mocacong.remote.apis.KakaoRequest
import com.konkuk.mocacong.remote.models.request.OAuthRequest
import com.konkuk.mocacong.remote.models.response.KakaoLoginResponse
import com.konkuk.mocacong.remote.repositories.LoginRepository
import com.konkuk.mocacong.util.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(val repository: LoginRepository) : ViewModel(){
    var mKakaoLoginResponse: MutableLiveData<ApiState<KakaoLoginResponse>> = MutableLiveData(ApiState.Loading())
    var kakaoLoginResponse: LiveData<ApiState<KakaoLoginResponse>> = mKakaoLoginResponse

    var mKakaoSignUpResponse: MutableLiveData<ApiState<Void>> = MutableLiveData(ApiState.Loading())
    var kakaoSignUpResponse: LiveData<ApiState<Void>> = mKakaoSignUpResponse


    fun postKakaoLogin(kakaoRequest: KakaoRequest) = viewModelScope.launch(Dispatchers.IO) {
        mKakaoLoginResponse.postValue(ApiState.Loading())
        mKakaoLoginResponse.postValue(repository.postKakaoLogin(kakaoRequest))
    }

    fun postKakaoSignUp(oAuthRequest: OAuthRequest) = viewModelScope.launch(Dispatchers.IO){
        mKakaoSignUpResponse.postValue(ApiState.Loading())
        mKakaoSignUpResponse.postValue(repository.postKakaoSignUp(oAuthRequest))
    }



}