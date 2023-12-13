package com.konkuk.mocacong.presentation.main


import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.konkuk.mocacong.R
import com.konkuk.mocacong.databinding.ActivityMainBinding
import com.konkuk.mocacong.presentation.base.BaseActivity
import com.konkuk.mocacong.presentation.detail.CafeDetailViewModel
import com.konkuk.mocacong.presentation.main.map.MapViewModel
import com.konkuk.mocacong.presentation.main.mypage.MypageViewModel
import com.konkuk.mocacong.remote.repositories.CafeDetailRepository
import com.konkuk.mocacong.remote.repositories.MapRepository
import com.konkuk.mocacong.remote.repositories.MypageRepository
import com.konkuk.mocacong.util.ViewModelFactory

class MainActivity : BaseActivity<ActivityMainBinding>() {


    private lateinit var mapViewModel: MapViewModel
    private lateinit var detailViewModel: CafeDetailViewModel
    private lateinit var mypageViewModel: MypageViewModel


    override val TAG: String = "MainActivity"
    override val layoutRes: Int = R.layout.activity_main

    override fun initViewModel() {
        Log.d(TAG, "initViewModel")
        mapViewModel = ViewModelProvider(this, ViewModelFactory(MapRepository()))[MapViewModel::class.java]
        detailViewModel = ViewModelProvider(this, ViewModelFactory(CafeDetailRepository()))[CafeDetailViewModel::class.java]
        mypageViewModel = ViewModelProvider(this, ViewModelFactory(MypageRepository()))[MypageViewModel::class.java]
    }

    override fun afterViewCreated() {
        Log.d(TAG, "afterViewCreated")
    }

}