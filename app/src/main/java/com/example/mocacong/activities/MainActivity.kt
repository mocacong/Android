package com.example.mocacong.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mocacong.R
import com.example.mocacong.databinding.ActivityMainBinding
import com.naver.maps.map.MapFragment

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }

    }
}