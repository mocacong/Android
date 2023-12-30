package com.konkuk.mocacong.presentation.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.mocacong.remote.apis.KakaoRequest
import com.konkuk.mocacong.remote.models.request.OAuthRequest
import com.konkuk.mocacong.remote.models.request.ReIssueRequest
import com.konkuk.mocacong.remote.models.response.CheckDuplicateResponse
import com.konkuk.mocacong.remote.models.response.KakaoLoginResponse
import com.konkuk.mocacong.remote.repositories.LoginRepository
import com.konkuk.mocacong.util.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LoginViewModel(val repository: LoginRepository) : ViewModel() {
    val TAG = "LoginViewModel"
    var mKakaoLoginResponse: MutableLiveData<ApiState<KakaoLoginResponse>> =
        MutableLiveData(ApiState.Loading())
    var kakaoLoginResponse: LiveData<ApiState<KakaoLoginResponse>> = mKakaoLoginResponse

    var mKakaoSignUpResponse: MutableLiveData<ApiState<Unit>> = MutableLiveData(ApiState.Loading())
    var kakaoSignUpResponse: LiveData<ApiState<Unit>> = mKakaoSignUpResponse

    var mNickDupResponse: MutableLiveData<ApiState<CheckDuplicateResponse>> =
        MutableLiveData(ApiState.Loading())
    var nickDupResponse: LiveData<ApiState<CheckDuplicateResponse>> = mNickDupResponse

    var kakaoID: String = ""
    var kakaoEmail: String = ""

    suspend fun postRefresh(token: String) = repository.refresh(ReIssueRequest(token))

    fun postKakaoLogin() = viewModelScope.launch(Dispatchers.IO) {
        val kakaoRequest = KakaoRequest(email = kakaoEmail, platformId = kakaoID)
        Log.d(TAG, "PostKakaoLogin 실행 Request = $kakaoRequest")
        mKakaoLoginResponse.postValue(ApiState.Loading())
        mKakaoLoginResponse.postValue(repository.postKakaoLogin(kakaoRequest))
    }

    fun requestKakaoSignUp(nickname: String) = viewModelScope.launch(Dispatchers.IO) {
        val oAuthRequest =
            OAuthRequest(nickname = nickname, platformId = kakaoID, email = kakaoEmail)
        Log.d(TAG, "requestKakaoSignUp 실행 request = $oAuthRequest")
        mKakaoSignUpResponse.postValue(ApiState.Loading())
        mKakaoSignUpResponse.postValue(repository.postKakaoSignUp(oAuthRequest))
    }

    fun nicknameRegex(nickname: String): Boolean {
        val pattern = "^[a-zA-Zㄱ-힣]{2,6}\$".toRegex()
        return nickname.matches(pattern)
    }

    fun requestNicknameCheck(nickname: String) = viewModelScope.launch(Dispatchers.IO) {
        Log.d(TAG, "뷰모델 닉네임 체크")
        mNickDupResponse.postValue(ApiState.Loading())
        mNickDupResponse.postValue(repository.isNicknameDuplicate(nickname))
    }

    val _pageFlow = MutableStateFlow(LoginPage.LOGIN)
    val pageFlow = _pageFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, LoginPage.LOGIN)

    fun goto(page: LoginPage) {
        viewModelScope.launch {
            _pageFlow.emit(page)
        }
    }

}