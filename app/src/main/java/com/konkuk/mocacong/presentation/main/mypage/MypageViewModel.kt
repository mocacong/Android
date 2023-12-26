package com.konkuk.mocacong.presentation.main.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.mocacong.remote.models.response.MyCommentsResponse
import com.konkuk.mocacong.remote.models.response.MyFavResponse
import com.konkuk.mocacong.remote.models.response.MyReviewsResponse
import com.konkuk.mocacong.remote.repositories.MypageRepository
import com.konkuk.mocacong.util.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MypageViewModel(val repository: MypageRepository) : ViewModel() {


    val mFavResponse = MutableLiveData<ApiState<MyFavResponse>>()
    val favResponse: LiveData<ApiState<MyFavResponse>> = mFavResponse

    val mReviewsResponse = MutableLiveData<ApiState<MyReviewsResponse>>()
    val reviewsResponse: LiveData<ApiState<MyReviewsResponse>> = mReviewsResponse

    val mCommentsResponse = MutableLiveData<ApiState<MyCommentsResponse>>()
    val commentsResponse: LiveData<ApiState<MyCommentsResponse>> = mCommentsResponse

    fun requestFavsLaunch(page: Int) = viewModelScope.launch(Dispatchers.IO) {
        mFavResponse.postValue(repository.getMyFavs(page))
    }

    fun requestReviewsLaunch(page: Int) = viewModelScope.launch(Dispatchers.IO) {
        mReviewsResponse.postValue(repository.getMyReviews(page))
    }

    fun requestCommentsLaunch(page: Int) = viewModelScope.launch(Dispatchers.IO) {
        mCommentsResponse.postValue(repository.getMyComments(page))
    }
}