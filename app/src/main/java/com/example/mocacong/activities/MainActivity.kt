package com.example.mocacong.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.example.mocacong.R
import com.example.mocacong.databinding.ActivityMainBinding
import com.example.mocacong.fragments.HomeFragment
import com.naver.maps.map.MapFragment

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        createFragment()


    }

    fun createFragment(){
        val transaction = supportFragmentManager.beginTransaction()
        val homeFragment = HomeFragment()
        transaction.replace(R.id.home, homeFragment).commit()

    }
}