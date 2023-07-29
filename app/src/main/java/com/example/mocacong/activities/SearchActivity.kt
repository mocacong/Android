package com.example.mocacong.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mocacong.adapter.SearchCafeAdapter
import com.example.mocacong.data.objects.KakaoLocalClient
import com.example.mocacong.data.objects.NetworkUtil
import com.example.mocacong.data.objects.Utils.handleEnterKey
import com.example.mocacong.data.response.LocalSearchResponse
import com.example.mocacong.data.response.Place
import com.example.mocacong.data.util.ApiState
import com.example.mocacong.data.util.TokenExceptionHandler
import com.example.mocacong.databinding.ActivitySearchBinding
import com.example.mocacong.network.KakaoSearchAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchActivity : AppCompatActivity() , SearchCafeAdapter.OnSearchItemClickedListener {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: SearchCafeAdapter

    private val kakaoApi = KakaoLocalClient.create(KakaoSearchAPI::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        layoutInit()
        setContentView(binding.root)
    }


    private fun layoutInit() {
        adapter = SearchCafeAdapter(this)
        binding.searchRecyclerView.adapter = adapter
        binding.searchRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.searchText.addTextChangedListener {
            lifecycleScope.launch {
                val state = withContext(Dispatchers.IO) {
                    getKeywordBasedCafes(binding.searchText.text.toString())
                }
                 when(state) {
                    is ApiState.Success -> {
                        state.data?.let { response ->
                            adapter.cafeList = response.documents
                            adapter.notifyDataSetChanged()
                        }
                    }
                    is ApiState.Error -> {
                        state.errorResponse?.let { er ->
                            TokenExceptionHandler.handleTokenException(this@SearchActivity, er)
                        }
                    }
                    is ApiState.Loading -> {}
                }
            }

        }
        binding.searchText.handleEnterKey()
        binding.searchText.performClick()
    }


    suspend fun getKeywordBasedCafes(query: String): ApiState<LocalSearchResponse> {
        val response = kakaoApi.getKeywordSearchResponse(query = query)
        return if (response.isSuccessful) {
            ApiState.Success(response.body())
        } else {
            val errorResponse = NetworkUtil.getErrorResponse(response.errorBody()!!)
            ApiState.Error(errorResponse = errorResponse!!)
        }
    }

    override fun onItemClicked(place: Place) {
        intent.putExtra("searchedPlace", place)
        setResult(RESULT_OK, intent)
        finish()
    }


}