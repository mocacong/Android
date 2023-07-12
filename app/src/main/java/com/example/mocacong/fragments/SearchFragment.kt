package com.example.mocacong.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mocacong.activities.SignInActivity
import com.example.mocacong.adapter.SearchCafeAdapter
import com.example.mocacong.data.objects.Utils
import com.example.mocacong.data.response.ErrorResponse
import com.example.mocacong.data.util.ApiState
import com.example.mocacong.databinding.FragmentSearchBinding
import com.example.mocacong.viewmodels.MapViewModel
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

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
        adapter = SearchCafeAdapter()
        binding.searchRecyclerView.adapter = adapter
        binding.searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val inputMethodManager = getSystemService(requireContext(), InputMethodManager::class.java)

        binding.searchText.addTextChangedListener {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    mapViewModel.apply {
                        requestKeyCafeLists(binding.searchText.text.toString())
                        placeByKeyword.collect { state ->
                            when (state) {
                                is ApiState.Success -> {
                                    state.data?.let { response ->
                                        adapter.cafeList = response.documents
                                        adapter.notifyDataSetChanged()
                                    }
                                    return@collect
                                }
                                is ApiState.Error -> {
                                    state.errorResponse?.let { er ->
                                        handleTokenException(er)
                                        Log.e(TAG, er.message)
                                    }
                                    mPlaceByLocation.value = ApiState.Loading()
                                    return@collect
                                }
                                is ApiState.Loading -> {}
                            }
                        }
                    }
                }

            }

            binding.searchText.setOnKeyListener { _, code, keyEvent ->
                if ((keyEvent.action == KeyEvent.ACTION_DOWN) && (code == KeyEvent.KEYCODE_ENTER)) {
                    (inputMethodManager)?.hideSoftInputFromWindow(
                        binding.searchText.windowToken,
                        0
                    )
                    true
                }
                false
            }
        }
    }

    private fun handleTokenException(errorResponse: ErrorResponse) {
        when (errorResponse.code) {
            1013 -> {
                Utils.showConfirmDialog(requireContext(),
                    "로그인이 필요한 서비스입니다. 로그인 페이지로 이동하시겠습니까?",
                    confirmAction = {
                        gotoSignInActivity()
                    },
                    cancelAction = {})
            }
            1014 or 1015 -> {
                Utils.showConfirmDialog(requireContext(), errorResponse.message, confirmAction = {
                    gotoSignInActivity()
                }, cancelAction = {

                })
            }
        }
    }

    private fun gotoSignInActivity() {
        val intent = Intent(requireContext(), SignInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

}