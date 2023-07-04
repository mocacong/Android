package com.example.mocacong.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mocacong.data.response.CafeResponse
import com.example.mocacong.data.response.ErrorResponse
import com.example.mocacong.data.util.ApiState
import com.example.mocacong.repositories.CafeDetailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class CafeDetailViewModel(private val cafeDetailRepository: CafeDetailRepository) : ViewModel() {


    var mCafeDetailInfosFlow: MutableStateFlow<ApiState<CafeResponse>> =
        MutableStateFlow(ApiState.Loading())
    var cafeDatailInfoFlow: StateFlow<ApiState<CafeResponse>> = mCafeDetailInfosFlow


    var mPostFavoriteFlow: MutableStateFlow<ApiState<Void>> = MutableStateFlow(ApiState.Loading())
    var postFavoriteFlow: StateFlow<ApiState<Void>> = mPostFavoriteFlow


    fun requestCafeDetailInfo(cafeId: String) = viewModelScope.launch {
        mCafeDetailInfosFlow.value = ApiState.Loading()
        cafeDetailRepository.getCafeDetailInfo(cafeId)
            .catch { error ->
                mCafeDetailInfosFlow.value =
                    ApiState.Error(ErrorResponse(code = 0, "${error.message}"))
            }
            .collect { values ->
                mCafeDetailInfosFlow.value = values
            }
    }

    fun requestFavoritePost(cafeId: String, isPost: Boolean) = viewModelScope.launch {
        mPostFavoriteFlow.value = ApiState.Loading()
        if (isPost) {
            cafeDetailRepository.postFavorite(cafeId)
                .catch {
                    mPostFavoriteFlow.value =
                        ApiState.Error(ErrorResponse(code = 0, "${it.message}"))
                }
                .collect { values ->
                    mPostFavoriteFlow.value = values
                }
        } else {
            cafeDetailRepository.deleteFavorite(cafeId)
                .catch {
                    mPostFavoriteFlow.value =
                        ApiState.Error(ErrorResponse(code = 0, "${it.message}"))
                }
                .collect { values ->
                    mPostFavoriteFlow.value = values
                }
        }
    }
}