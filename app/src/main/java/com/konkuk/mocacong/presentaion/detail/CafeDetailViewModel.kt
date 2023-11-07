package com.konkuk.mocacong.presentaion.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.mocacong.remote.models.response.CafeResponse
import com.konkuk.mocacong.remote.models.response.CommentsResponse
import com.konkuk.mocacong.remote.models.response.MyReviewResponse
import com.konkuk.mocacong.remote.repositories.CafeDetailRepository
import com.konkuk.mocacong.util.ApiState
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

    var mPostReviewFlow: MutableStateFlow<ApiState<Void>> = MutableStateFlow(ApiState.Loading())
    var postReviewFlow: StateFlow<ApiState<Void>> = mPostReviewFlow

    var mPutReviewFlow: MutableStateFlow<ApiState<Void>> = MutableStateFlow(ApiState.Loading())
    var putReviewFlow: StateFlow<ApiState<Void>> = mPutReviewFlow

    var mMyReviewFlow: MutableStateFlow<ApiState<MyReviewResponse>> =
        MutableStateFlow(ApiState.Loading())
    var myReviewFlow: StateFlow<ApiState<MyReviewResponse>> = mMyReviewFlow

    var isCommentEditing: Boolean = false

    //isLoading, errorState livedata로 한 번에 관리

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

    fun requestDeleteComment(cafeId: String, commentId: String) {

        viewModelScope.launch(Dispatchers.IO) {

        }
    }

    fun postMyReview(
        cafeId: String,
        review: com.konkuk.mocacong.remote.models.request.ReviewRequest
    ) =
        viewModelScope.launch(Dispatchers.IO) {
            mPostReviewFlow.value = ApiState.Loading()
            mPostReviewFlow.value = cafeDetailRepository.postReview(cafeId, review)
        }

    fun putMyReview(
        cafeId: String,
        review: com.konkuk.mocacong.remote.models.request.ReviewRequest
    ) = viewModelScope.launch(Dispatchers.IO) {
        mPutReviewFlow.value = ApiState.Loading()
        mPutReviewFlow.value = cafeDetailRepository.putReview(cafeId, review)
    }

    fun getMyReview(cafeId: String) = viewModelScope.launch(Dispatchers.IO) {
        mMyReviewFlow.value = ApiState.Loading()
        mMyReviewFlow.value = cafeDetailRepository.getMyReview(cafeId)
    }
}