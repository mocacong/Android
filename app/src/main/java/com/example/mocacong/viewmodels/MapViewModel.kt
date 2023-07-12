package com.example.mocacong.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mocacong.data.request.FilteringRequest
import com.example.mocacong.data.response.CafePreviewResponse
import com.example.mocacong.data.response.ErrorResponse
import com.example.mocacong.data.response.FilteringResponse
import com.example.mocacong.data.response.LocalSearchResponse
import com.example.mocacong.data.util.ApiState
import com.example.mocacong.repositories.MapRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MapViewModel(private val mapRepository: MapRepository) : ViewModel() {
    val TAG = "Map"

    init {
        Log.d(TAG, "Map 뷰모델 생성됨")
    }

    var mFilteredCafesFlow: MutableStateFlow<ApiState<FilteringResponse>> =
        MutableStateFlow(ApiState.Loading())
    var filteredCafesFlow: StateFlow<ApiState<FilteringResponse>> = mFilteredCafesFlow


    var mfavoriteFlow: MutableStateFlow<ApiState<FilteringResponse>> =
        MutableStateFlow(ApiState.Loading())
    var favoriteFlow: StateFlow<ApiState<FilteringResponse>> = mfavoriteFlow

    var mPlaceByLocation: MutableStateFlow<ApiState<LocalSearchResponse>> =
        MutableStateFlow(ApiState.Loading())
    var placeByLocation: StateFlow<ApiState<LocalSearchResponse>> = mPlaceByLocation

    var mPlaceByKeyword: MutableStateFlow<ApiState<LocalSearchResponse>> =
        MutableStateFlow(ApiState.Loading())
    var placeByKeyword: StateFlow<ApiState<LocalSearchResponse>> = mPlaceByKeyword

    var mPreviewInfo: MutableStateFlow<ApiState<CafePreviewResponse>> =
        MutableStateFlow(ApiState.Loading())
    var previewInfo: StateFlow<ApiState<CafePreviewResponse>> = mPreviewInfo


    fun requestFilterStudyType(type: String, filteringRequest: FilteringRequest) =
        viewModelScope.launch(Dispatchers.IO) {
            mFilteredCafesFlow.value = ApiState.Loading()
            val result = mapRepository.getFilterings(type, filteringRequest)
            mFilteredCafesFlow.value = result
        }


    fun requestFavorites(filteringRequest: FilteringRequest) =
        viewModelScope.launch(Dispatchers.IO) {
            mfavoriteFlow.value = ApiState.Loading()
            val result = mapRepository.getFavorites(filteringRequest)
            mfavoriteFlow.value = result

        }


    fun requestMapCafeLists(mapx: String, mapy: String) = viewModelScope.launch(Dispatchers.IO) {
        Log.d(TAG, "requestMapCafeLists 호출됨")
        mPlaceByLocation.value = ApiState.Loading()
        val result = mapRepository.getLocationBasedCafes(mapx, mapy)
        mPlaceByLocation.value = result
    }

    fun requestKeyCafeLists(query: String) = viewModelScope.launch(Dispatchers.IO) {
        mPlaceByKeyword.value = ApiState.Loading()
        mapRepository.getKeywordBasedCafes(query)
            .catch {
                mPlaceByKeyword.value = ApiState.Error(ErrorResponse(code = 0, "${it.message}"))
            }
            .collect {
                mPlaceByKeyword.value = it
            }
    }

    fun requestPreviewInfo(cafeId: String) = viewModelScope.launch(Dispatchers.IO) {
        mPreviewInfo.value = ApiState.Loading()
        mapRepository.getPreviewInfo(cafeId = cafeId)
            .catch {
                mPreviewInfo.value = ApiState.Error(ErrorResponse(code = 0, "${it.message}"))
            }
            .collect {
                mPreviewInfo.value = it
            }
    }


}