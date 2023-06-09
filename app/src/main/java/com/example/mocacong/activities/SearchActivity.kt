package com.example.mocacong.activities

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mocacong.adapter.SearchCafeAdapter
import com.example.mocacong.controllers.SearchController
import com.example.mocacong.databinding.ActivitySearchBinding
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var controller: SearchController
    private lateinit var adapter: SearchCafeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        controller = SearchController()
        adapter = SearchCafeAdapter()
        layoutInit()

    }

    private fun layoutInit() {
        binding.searchText.addTextChangedListener {
            lifecycleScope.launch {
                val response = controller.searchByKeyword(binding.searchText.text.toString())
                Log.d("Search", response.toString())

                if (response != null) {
                    adapter.cafeList = response.documents
                    binding.searchRecyclerView.adapter = adapter
                    binding.searchRecyclerView.layoutManager =
                        LinearLayoutManager(this@SearchActivity)
                }
            }
        }

        binding.searchText.setOnKeyListener { _, code, keyEvent ->
            if((keyEvent.action == KeyEvent.ACTION_DOWN) && (code == KeyEvent.KEYCODE_ENTER)){
                (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(binding.searchText.windowToken, 0)
                true
            }
            false
        }

        binding.searchText.performClick()
    }


}