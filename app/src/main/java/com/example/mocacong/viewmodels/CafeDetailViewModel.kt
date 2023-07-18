package com.example.mocacong.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mocacong.data.response.CafeResponse
import com.example.mocacong.data.util.ApiState
import com.example.mocacong.repositories.CafeDetailRepository
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
}