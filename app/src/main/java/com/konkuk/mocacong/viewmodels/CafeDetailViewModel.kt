package com.konkuk.mocacong.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.mocacong.data.request.ReviewRequest
import com.konkuk.mocacong.data.response.CafeResponse
import com.konkuk.mocacong.data.response.CommentsResponse
import com.konkuk.mocacong.data.response.MyReviewResponse
import com.konkuk.mocacong.data.util.ApiState
import com.konkuk.mocacong.repositories.CafeDetailRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CafeDetailViewModel(private val cafeDetailRepository: CafeDetailRepository) : ViewModel() {


    var mCafeDetailInfosFlow: MutableStateFlow<ApiState<CafeResponse>> =
        MutableStateFlow(ApiState.Loading())
    var cafeDatailInfoFlow: StateFlow<ApiState<CafeResponse>> = mCafeDetailInfosFlow

    var mPostFavoriteFlow: MutableStateFlow<ApiState<Void>> = MutableStateFlow(ApiState.Loading())
    var postFavoriteFlow: StateFlow<ApiState<Void>> = mPostFavoriteFlow

    var mCommentsResponseFlow: MutableStateFlow<ApiState<CommentsResponse>> =
        MutableStateFlow(ApiState.Loading())
    var commentsResponseFlow: StateFlow<ApiState<CommentsResponse>> = mCommentsResponseFlow

    var mCommentPostFlow: MutableStateFlow<ApiState<Void>> = MutableStateFlow(ApiState.Loading())
    var commentPostFlow: StateFlow<ApiState<Void>> = mCommentPostFlow

    var mPostReviewFlow : MutableStateFlow<ApiState<Void>> = MutableStateFlow(ApiState.Loading())
    var postReviewFlow: StateFlow<ApiState<Void>> = mPostReviewFlow

    var mPutReviewFlow : MutableStateFlow<ApiState<Void>> = MutableStateFlow(ApiState.Loading())
    var putReviewFlow: StateFlow<ApiState<Void>> = mPutReviewFlow

    var mMyReviewFlow : MutableStateFlow<ApiState<MyReviewResponse>> = MutableStateFlow(ApiState.Loading())
    var myReviewFlow: StateFlow<ApiState<MyReviewResponse>> = mMyReviewFlow


    fun requestCafeDetailInfo(cafeId: String) = viewModelScope.launch(Dispatchers.IO) {
        mCafeDetailInfosFlow.value = ApiState.Loading()
        mCafeDetailInfosFlow.value = cafeDetailRepository.getCafeDetailInfo(cafeId)
    }

    fun requestFavoritePost(cafeId: String, isPost: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            mPostFavoriteFlow.value = ApiState.Loading()
            if (isPost) {
                mPostFavoriteFlow.value = cafeDetailRepository.postFavorite(cafeId)
            } else {
                mPostFavoriteFlow.value = cafeDetailRepository.deleteFavorite(cafeId)
            }
        }

    fun requestCafeComments(cafeId: String, page: Int) = viewModelScope.launch(Dispatchers.IO) {
        mCommentsResponseFlow.value = ApiState.Loading()
        mCommentsResponseFlow.value = cafeDetailRepository.getComments(cafeId, page)
    }

    fun postMyComment(cafeId: String, content: String) = viewModelScope.launch(Dispatchers.IO) {
        mCommentPostFlow.value = ApiState.Loading()
        mCommentPostFlow.value = cafeDetailRepository.postComment(cafeId, content)
    }

    fun postMyReview(cafeId: String, review: ReviewRequest)  = viewModelScope.launch(Dispatchers.IO) {
        mPostReviewFlow.value = ApiState.Loading()
        mPostReviewFlow.value = cafeDetailRepository.postReview(cafeId, review)
    }

    fun putMyReview(cafeId: String, review: ReviewRequest)  = viewModelScope.launch(Dispatchers.IO) {
        mPutReviewFlow.value = ApiState.Loading()
        mPutReviewFlow.value = cafeDetailRepository.putReview(cafeId, review)
    }

    fun getMyReview(cafeId: String)  = viewModelScope.launch(Dispatchers.IO) {
        mMyReviewFlow.value = ApiState.Loading()
        mMyReviewFlow.value = cafeDetailRepository.getMyReview(cafeId)
    }
}