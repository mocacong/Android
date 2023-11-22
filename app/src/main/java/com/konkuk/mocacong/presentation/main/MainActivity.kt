package com.konkuk.mocacong.presentation.main


import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import com.konkuk.mocacong.R
import com.konkuk.mocacong.databinding.ActivityMainBinding
import com.konkuk.mocacong.objects.Utils
import com.konkuk.mocacong.presentation.base.BaseActivity
import com.konkuk.mocacong.presentation.main.map.MapViewModel
import com.konkuk.mocacong.remote.repositories.MapRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : BaseActivity<ActivityMainBinding>() {

    private var backButtonPressedOnce = false

    private lateinit var mapViewModel: MapViewModel


    override val TAG: String = "MainActivity"
    override val layoutRes: Int = R.layout.activity_main

    override fun initViewModel() {
        mapViewModel = createViewModel(MapRepository())
    }

    override fun afterViewCreated() {
        setBackBtn()
    }


    private fun setBackBtn() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backButtonPressedOnce) finish()
                else {
                    backButtonPressedOnce = true
                    Utils.showToast(this@MainActivity, "한 번 더 누르면 종료됩니다")
                    lifecycleScope.launch {
                        delay(2000)
                        backButtonPressedOnce = false
                    }
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

}