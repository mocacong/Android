package com.konkuk.mocacong.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _pageFlow = MutableStateFlow(MainPage.HOME)
    val pageFlow = _pageFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, MainPage.HOME)

    fun goto(page: MainPage){
        viewModelScope.launch {
            _pageFlow.emit(page)
        }
    }
}