package com.konkuk.mocacong.presentation.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.mocacong.remote.models.response.Place
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _pageFlow = MutableStateFlow(MainPage.HOME)
    val pageFlow = _pageFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, MainPage.HOME)

    fun goto(page: MainPage) {
        viewModelScope.launch {
            _pageFlow.emit(page)
        }
    }

    private val _locationFlow = MutableStateFlow<Place?>(Place())
    val locationFlow = _locationFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, Place())

    fun gotoMap(place: Place?) {
        Log.d("Main", "GOTO $place")
        viewModelScope.launch {
            _locationFlow.emit(place)
            goto(MainPage.HOME)
        }
    }


}