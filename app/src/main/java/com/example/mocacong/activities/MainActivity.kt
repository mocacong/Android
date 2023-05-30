package com.example.mocacong.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mocacong.R
import com.example.mocacong.data.response.Place
import com.example.mocacong.databinding.ActivityMainBinding
import com.example.mocacong.fragments.HomeFragment
import com.example.mocacong.fragments.MypageFragment
import com.example.mocacong.fragments.SettingsFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.Serializable

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var homeFragment: HomeFragment
    private var backButtonPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setBackBtn()
        setBottomNav()
    }

    private fun setBackBtn() {
        val onBackPressedCallback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if(backButtonPressedOnce) finish()
                else{
                    backButtonPressedOnce = true
                    Toast.makeText(this@MainActivity, "한 번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show()
                    lifecycleScope.launch {
                        delay(2000)
                        backButtonPressedOnce = false
                    }
                }
            }
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onResume() {
        super.onResume()
        val place = intent.intentSerializable("place", Place::class.java)
        if (place != null) {
            binding.bottomMenu.selectedItemId = R.id.menu_map

            val args = Bundle()
            args.putSerializable("searchedPlace", place)
            homeFragment.arguments = args
        }
    }

    private fun setBottomNav() {
        val btnv = binding.bottomMenu

        homeFragment = HomeFragment()


        btnv.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_map -> {
                    homeFragment = HomeFragment()
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



    private fun <T : Serializable> Intent.intentSerializable(key: String, clazz: Class<T>): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.getSerializableExtra(key, clazz)
        } else {
            this.getSerializableExtra(key) as T?
        }
    }

}