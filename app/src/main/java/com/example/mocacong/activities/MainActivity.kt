package com.example.mocacong.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mocacong.R
import com.example.mocacong.databinding.ActivityMainBinding
import com.example.mocacong.fragments.HomeFragment
import com.example.mocacong.fragments.MypageFragment

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setBottomNav()


    }

    private fun setBottomNav() {
        val btnv = binding.bottomMenu
        val frameLayout = binding.frameLayout

        btnv.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_map -> {
                    showFragment(HomeFragment())
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
            .replace(R.id.frameLayout, fragment)
            .commit()

    }


}