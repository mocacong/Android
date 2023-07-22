package com.example.mocacong.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mocacong.activities.MainActivity
import com.example.mocacong.adapter.SearchCafeAdapter
import com.example.mocacong.data.objects.Utils.handleEnterKey
import com.example.mocacong.data.response.Place
import com.example.mocacong.data.util.ApiState
import com.example.mocacong.data.util.TokenExceptionHandler
import com.example.mocacong.databinding.FragmentSearchBinding
import com.example.mocacong.viewmodels.MapViewModel
import kotlinx.coroutines.launch

class SearchFragment : Fragment() , SearchCafeAdapter.OnSearchItemClickedListener {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val mapViewModel: MapViewModel by activityViewModels()
    private val TAG = "MAP"

    private lateinit var adapter: SearchCafeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        layoutInit()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun layoutInit() {
        adapter = SearchCafeAdapter(this)
        binding.searchRecyclerView.adapter = adapter
        binding.searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.searchText.addTextChangedListener {
            lifecycleScope.launch {
                mapViewModel.apply {
                    requestKeyCafeLists(binding.searchText.text.toString()).join()
                    when (val state = placeByKeyword.value) {
                        is ApiState.Success -> {
                            state.data?.let { response ->
                                adapter.cafeList = response.documents
                                adapter.notifyDataSetChanged()
                            }
                        }
                        is ApiState.Error -> {
                            state.errorResponse?.let { er ->
                                TokenExceptionHandler.handleTokenException(requireContext(),er)
                                Log.e(TAG, er.message)
                            }
                            mPlaceByLocation.value = ApiState.Loading()
                        }
                        is ApiState.Loading -> {}
                    }
                }
            }
        }
        binding.searchText.handleEnterKey()
    }

    override fun onItemClicked(place: Place) {
        mapViewModel.searchedPlaceResult = place
        val activity = requireActivity()
        if (activity is MainActivity) {
            activity.showFragment(HomeFragment())
        }
    }

}