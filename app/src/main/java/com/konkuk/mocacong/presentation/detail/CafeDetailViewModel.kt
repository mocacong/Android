package com.konkuk.mocacong.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.mocacong.data.entities.BasicPlaceInfo
import com.konkuk.mocacong.remote.models.request.ReviewRequest
import com.konkuk.mocacong.remote.models.response.CafeResponse
import com.konkuk.mocacong.remote.models.response.CommentsResponse
import com.konkuk.mocacong.remote.models.response.MyReviewResponse
import com.konkuk.mocacong.remote.repositories.CafeDetailRepository
import com.konkuk.mocacong.util.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CafeDetailViewModel(private val cafeDetailRepository: CafeDetailRepository) : ViewModel() {

    lateinit var cafeBasicInfo: BasicPlaceInfo
    lateinit var cafeId: String

    val _cafeDetailInfoResponse = MutableLiveData<ApiState<CafeResponse>>()
    val cafeDetailInfoResponse : LiveData<ApiState<CafeResponse>> = _cafeDetailInfoResponse
    fun requestCafeDetailInfo() = viewModelScope.launch(Dispatchers.IO) {
        _cafeDetailInfoResponse.postValue(ApiState.Loading())
        _cafeDetailInfoResponse.postValue(cafeDetailRepository.getCafeDetailInfo(cafeId))
    }

    var _favoriteResponse = MutableLiveData<ApiState<Unit>>()
    var favoriteResponse : LiveData<ApiState<Unit>> = _favoriteResponse


    var mCommentsResponseResponse: MutableLiveData<ApiState<CommentsResponse>> =
        MutableLiveData(ApiState.Loading())
    var commentsResponseResponse: LiveData<ApiState<CommentsResponse>> = mCommentsResponseResponse

    var mCommentPostResponse: MutableLiveData<ApiState<Unit>> = MutableLiveData(ApiState.Loading())
    var commentPostResponse: LiveData<ApiState<Unit>> = mCommentPostResponse

    var mPostReviewResponse: MutableLiveData<ApiState<Unit>> = MutableLiveData(ApiState.Loading())
    var postReviewResponse: LiveData<ApiState<Unit>> = mPostReviewResponse

    var mPutReviewResponse: MutableLiveData<ApiState<Unit>> = MutableLiveData(ApiState.Loading())
    var putReviewResponse: LiveData<ApiState<Unit>> = mPutReviewResponse

    var mMyReviewResponse: MutableLiveData<ApiState<MyReviewResponse>> =
        MutableLiveData(ApiState.Loading())
    var myReviewResponse: LiveData<ApiState<MyReviewResponse>> = mMyReviewResponse

    var isCommentEditing: Boolean = false

    //isLoading, errorState livedata로 한 번에 관리


    fun requestFavoritePost(isRegister: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            _favoriteResponse.postValue(ApiState.Loading())
            if (isRegister) {
                _favoriteResponse.postValue(cafeDetailRepository.postFavorite(cafeId))
            } else {
                _favoriteResponse.postValue(cafeDetailRepository.deleteFavorite(cafeId))
            }
        }

    fun requestCafeComments(page: Int) = viewModelScope.launch(Dispatchers.IO) {
        mCommentsResponseResponse.postValue(ApiState.Loading())
        mCommentsResponseResponse.postValue(cafeDetailRepository.getComments(cafeId, page))
    }

    fun postMyComment(content: String) = viewModelScope.launch(Dispatchers.IO) {
        mCommentPostResponse.postValue(ApiState.Loading())
        mCommentPostResponse.postValue(cafeDetailRepository.postComment(cafeId, content))
    }

    fun postMyReview(review: ReviewRequest) = viewModelScope.launch(Dispatchers.IO) {
        mPostReviewResponse.postValue(ApiState.Loading())
        mPostReviewResponse.postValue(cafeDetailRepository.postReview(cafeId, review))
    }

    fun putMyReview(review: ReviewRequest) = viewModelScope.launch(Dispatchers.IO) {
        mPutReviewResponse.postValue(ApiState.Loading())
        mPutReviewResponse.postValue(cafeDetailRepository.putReview(cafeId, review))
    }

    fun getMyReview() = viewModelScope.launch(Dispatchers.IO) {
        mMyReviewResponse.postValue(ApiState.Loading())
        mMyReviewResponse.postValue(cafeDetailRepository.getMyReview(cafeId))
    }
}