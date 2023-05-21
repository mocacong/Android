package com.example.mocacong.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import com.example.mocacong.R
import com.example.mocacong.data.response.Place
import com.example.mocacong.databinding.ActivityMainBinding
import com.example.mocacong.fragments.HomeFragment
import com.example.mocacong.fragments.MypageFragment
import java.io.Serializable

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var homeFragment: HomeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setBottomNav()
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
                    true
                }
                else -> false
            }
        }

        btnv.selectedItemId = R.id.menu_map
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