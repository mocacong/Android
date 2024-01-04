package com.konkuk.mocacong.presentation.main.map

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.konkuk.mocacong.R
import com.konkuk.mocacong.databinding.FragmentSearchBinding
import com.konkuk.mocacong.presentation.base.BaseFragment
import com.konkuk.mocacong.presentation.main.MainViewModel
import com.konkuk.mocacong.remote.models.response.Place
import com.konkuk.mocacong.util.handleEnterKey
import com.konkuk.mocacong.util.showKeyboard

class SearchFragment : BaseFragment<FragmentSearchBinding>() {
    override val TAG: String
        get() = "Search"
    override val layoutRes: Int = R.layout.fragment_search

    private val mapViewModel: MapViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun afterViewCreated() {
        val adapter = SearchCafeAdapter(object : SearchCafeAdapter.OnSearchItemClickedListener {
            override fun onItemClicked(place: Place) {
                hideKeyboard()
                mainViewModel.gotoMap(place)
            }
        })

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.searchText.apply {
            handleEnterKey()
            showKeyboard()
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    mapViewModel.requestSearchByKeyword(binding.searchText.text.toString())
                }
            })

        }

        mapViewModel.searchedPlaces.observe(this){
            Log.d(TAG,"Searced: $it")
            adapter.cafeList = it
            adapter.notifyDataSetChanged()
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager: InputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireView().windowToken, 0)
    }
}