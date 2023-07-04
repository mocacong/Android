package com.example.mocacong.activities


import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mocacong.R
import com.example.mocacong.data.objects.Utils
import com.example.mocacong.data.objects.Utils.intentSerializable
import com.example.mocacong.data.response.Place
import com.example.mocacong.databinding.ActivityMainBinding
import com.example.mocacong.fragments.HomeFragment
import com.example.mocacong.fragments.MypageFragment
import com.example.mocacong.fragments.SettingsFragment
import com.example.mocacong.network.ServerNetworkException
import com.example.mocacong.ui.MessageDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var homeFragment: HomeFragment
    private var backButtonPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            setBackBtn()
            setBottomNav()
        } catch (e: ServerNetworkException) {
            MessageDialog(e.responseMessage)
        }
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

    private fun setBottomNav() {
        val btnv = binding.bottomMenu

        btnv.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_map -> {
                    homeFragment = HomeFragment()

                    val place = intent.intentSerializable("place", Place::class.java)
                    place?.let {
                        val args = Bundle()
                        args.putSerializable("searchedPlace", place)
                        homeFragment.arguments = args
                    }

                    showFragment(homeFragment)
                    true
                }
                R.id.menu_mypage -> {
                    showFragment(MypageFragment())
                    true
                }
                R.id.menu_settings -> {
                    //해야됨
                    showFragment(SettingsFragment())
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

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.frameLayout.id, fragment)
            .commit()
    }


}