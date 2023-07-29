package com.example.mocacong.activities


import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.mocacong.R
import com.example.mocacong.data.objects.Utils
import com.example.mocacong.data.util.ViewModelFactory
import com.example.mocacong.databinding.ActivityMainBinding
import com.example.mocacong.fragments.HomeFragment
import com.example.mocacong.fragments.MypageFragment
import com.example.mocacong.fragments.SettingsFragment
import com.example.mocacong.repositories.MapRepository
import com.example.mocacong.viewmodels.MapViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private var backButtonPressedOnce = false

    private lateinit var mapViewModel: MapViewModel
    private lateinit var mapViewModelFactory: ViewModelFactory<MapRepository>

    private lateinit var homeFragment: HomeFragment
    private lateinit var settingsFragment: SettingsFragment
    private lateinit var mypageFragment: MypageFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapViewModelFactory = ViewModelFactory(MapRepository())
        mapViewModel = ViewModelProvider(this, mapViewModelFactory)[MapViewModel::class.java]

        setBackBtn()
        setInitialFragments()
        setBottomNav()
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

    private fun setInitialFragments() {
        homeFragment = HomeFragment()
        mypageFragment = MypageFragment()
        settingsFragment = SettingsFragment()

        supportFragmentManager.beginTransaction()
            .add(binding.frameLayout.id, mypageFragment)
            .add(binding.frameLayout.id, settingsFragment)
            .add(binding.frameLayout.id, homeFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }


    private fun setBottomNav() {
        val btnv = binding.bottomMenu

        btnv.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_map -> {
                    showFragment(homeFragment)
                    true
                }
                R.id.menu_mypage -> {
                    showFragment(mypageFragment)
                    true
                }
                R.id.menu_settings -> {
                    //해야됨
                    showFragment(settingsFragment)
                    true
                }
                else -> false
            }
        }

        val id =
            when (intent.getIntExtra("tabNumber", 0)) {
                0 -> R.id.menu_map
                1 -> R.id.menu_mypage
                2 -> R.id.menu_settings
                else -> R.id.menu_map
            }

        btnv.selectedItemId = id
    }

    fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .hideAll()
            .show(fragment)
            .commit()
    }

    fun addFragment(fragment: Fragment){
        binding.bottomMenu.visibility = View.GONE
        supportFragmentManager.beginTransaction()
            .add(binding.frameLayout.id, fragment)
            .addToBackStack(null)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    private fun FragmentTransaction.hideAll() : FragmentTransaction{
        supportFragmentManager.fragments.forEach {
            if(it!=null){
                hide(it)
            }
        }
        return this
    }

}