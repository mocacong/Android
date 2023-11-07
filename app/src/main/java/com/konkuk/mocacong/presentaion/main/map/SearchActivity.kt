package com.konkuk.mocacong.presentaion.main.map

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.konkuk.mocacong.objects.KakaoLocalClient
import com.konkuk.mocacong.objects.NetworkUtil
import com.konkuk.mocacong.objects.Utils.handleEnterKey
import com.konkuk.mocacong.objects.Utils.showKeyboard
import com.konkuk.mocacong.data.response.LocalSearchResponse
import com.konkuk.mocacong.data.response.Place
import com.konkuk.mocacong.util.ApiState
import com.konkuk.mocacong.util.TokenExceptionHandler
import com.konkuk.mocacong.databinding.ActivitySearchBinding
import com.konkuk.mocacong.remote.apis.KakaoSearchAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchActivity : AppCompatActivity(), SearchCafeAdapter.OnSearchItemClickedListener {

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

        binding.searchText.apply {
            handleEnterKey()
            showKeyboard()
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    setRecyclerItems()
                }
            })
        }
    }

    private fun setRecyclerItems() = lifecycleScope.launch {
        val state = withContext(Dispatchers.IO) {
            getKeywordBasedCafes(binding.searchText.text.toString())
        }
        when (state) {
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


    private suspend fun getKeywordBasedCafes(query: String): ApiState<LocalSearchResponse> {
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