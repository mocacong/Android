package com.konkuk.mocacong.presentation.main


import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.konkuk.mocacong.R
import com.konkuk.mocacong.databinding.ActivityMainBinding
import com.konkuk.mocacong.presentation.base.BaseActivity
import com.konkuk.mocacong.presentation.detail.CafeCommentsFragment
import com.konkuk.mocacong.presentation.detail.CafeDetailFragment
import com.konkuk.mocacong.presentation.detail.CafeDetailViewModel
import com.konkuk.mocacong.presentation.detail.CafeImagesFragment
import com.konkuk.mocacong.presentation.main.map.HomeFragment
import com.konkuk.mocacong.presentation.main.map.MapViewModel
import com.konkuk.mocacong.presentation.main.map.SearchFragment
import com.konkuk.mocacong.presentation.main.mypage.MyCommentsFragment
import com.konkuk.mocacong.presentation.main.mypage.MyFavsFragment
import com.konkuk.mocacong.presentation.main.mypage.MyReviewsFragment
import com.konkuk.mocacong.presentation.main.mypage.MypageViewModel
import com.konkuk.mocacong.remote.repositories.CafeDetailRepository
import com.konkuk.mocacong.remote.repositories.MapRepository
import com.konkuk.mocacong.remote.repositories.MypageRepository
import com.konkuk.mocacong.util.ViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : BaseActivity<ActivityMainBinding>() {


    private lateinit var mapViewModel: MapViewModel
    private lateinit var detailViewModel: CafeDetailViewModel
    private lateinit var mypageViewModel: MypageViewModel
    private lateinit var mainViewModel: MainViewModel


    override val TAG: String = "MainActivity"
    override val layoutRes: Int = R.layout.activity_main

    private val homeFragment by lazy {
        supportFragmentManager.findFragmentByTag(HomeFragment::class.java.name) ?: HomeFragment()
    }
    private val cafeDetailFragment by lazy {
        supportFragmentManager.findFragmentByTag(CafeDetailFragment::class.java.name)
            ?: CafeDetailFragment()
    }
    private val searchFragment by lazy {
        supportFragmentManager.findFragmentByTag(SearchFragment::class.java.name)
            ?: SearchFragment()
    }
    private val commentsFragment by lazy {
        supportFragmentManager.findFragmentByTag(CafeCommentsFragment::class.java.name)
            ?: CafeCommentsFragment()
    }
    private val imagesFragment by lazy {
        supportFragmentManager.findFragmentByTag(CafeImagesFragment::class.java.name)
            ?: CafeImagesFragment()
    }

    private val myFavsFragment by lazy {
        supportFragmentManager.findFragmentByTag(MyFavsFragment::class.java.name)
            ?: MyFavsFragment()
    }

    private val myCommentsFragment by lazy {
        supportFragmentManager.findFragmentByTag(MyCommentsFragment::class.java.name)
            ?: MyCommentsFragment()
    }

    private val myReviewsFragment by lazy {
        supportFragmentManager.findFragmentByTag(MyReviewsFragment::class.java.name)
            ?: MyReviewsFragment()
    }


    private var backButtonPressedOnce = false

    val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            when (supportFragmentManager.fragments.first { it.isVisible }) {
                is HomeFragment -> {
                    if (backButtonPressedOnce) finish()
                    else {
                        backButtonPressedOnce = true
                        showToast("한 번 더 누르면 종료됩니다")
                        lifecycleScope.launch {
                            delay(2000)
                            backButtonPressedOnce = false
                        }
                    }
                }
                is CafeCommentsFragment, is CafeImagesFragment -> {
                    mainViewModel.goto(MainPage.DETAIL)
                }
                else -> {
                    mainViewModel.goto(MainPage.HOME)
                }
            }
        }
    }

    override fun initViewModel() {
        Log.d(TAG, "initViewModel")
        mapViewModel =
            ViewModelProvider(this, ViewModelFactory(MapRepository()))[MapViewModel::class.java]
        detailViewModel = ViewModelProvider(
            this,
            ViewModelFactory(CafeDetailRepository())
        )[CafeDetailViewModel::class.java]
        mypageViewModel = ViewModelProvider(
            this,
            ViewModelFactory(MypageRepository())
        )[MypageViewModel::class.java]
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun afterViewCreated() {
        onBackPressedDispatcher.addCallback(this, callback)
        collectPage()
        observeMyPlace()
    }

    private fun getFragment(page: MainPage): Fragment {
        return when (page) {
            MainPage.HOME -> homeFragment
            MainPage.DETAIL -> cafeDetailFragment
            MainPage.SEARCH -> searchFragment
            MainPage.COMMENTS -> commentsFragment
            MainPage.IMAGES-> imagesFragment
            MainPage.MyFav -> myFavsFragment
            MainPage.MyComment -> myCommentsFragment
            MainPage.MyReview -> myReviewsFragment
        }
    }

    private fun collectPage() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                var prevPage = mainViewModel.pageFlow.value
                mainViewModel.pageFlow.collect { page ->
                    val preFragment = getFragment(prevPage)
                    val fragment = getFragment(page)
                    supportFragmentManager.commit {
                        if (preFragment != fragment) hide(preFragment)
                        if (fragment.isAdded) {
                            if(fragment is CafeDetailFragment) detailViewModel.requestCafeDetailInfo()
                            show(fragment)
                        }
                        else add(R.id.fragmentContainer, fragment, fragment.javaClass.name)
                    }
                    prevPage = page
                }
            }
        }
    }

    private fun observeMyPlace(){
        mypageViewModel.selectedPlaces.observe(this){
            mainViewModel.gotoMap(it)
        }
    }


}