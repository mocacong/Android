package com.konkuk.mocacong.presentation.main.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.konkuk.mocacong.databinding.FragmentCafePreviewBinding
import com.konkuk.mocacong.objects.RetrofitClient
import com.konkuk.mocacong.objects.Utils.bundleSerializable
import com.konkuk.mocacong.remote.apis.CafeDetailAPI
import com.konkuk.mocacong.remote.apis.MapApi
import com.konkuk.mocacong.remote.models.response.CafePreviewResponse
import com.konkuk.mocacong.remote.models.response.Place
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CafePreviewFragment : BottomSheetDialogFragment() {
    private lateinit var cafe: Place
    private var info: CafePreviewResponse? = null
    private var _binding: FragmentCafePreviewBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            cafe = it.bundleSerializable("cafe", Place::class.java)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCafePreviewBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            withContext(Dispatchers.Default) {
                postCafe()
            }

        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setLayout()
    }

    private fun setLayout() {
        lifecycleScope.launch {
            info = withContext(Dispatchers.IO) {
                getPreviewInfo(cafe.id)
            }

        }

    }


    override fun onDestroy() {
        (parentFragment as HomeFragment).revertMarker()
        _binding = null
        super.onDestroy()
    }

    private suspend fun getPreviewInfo(id: String): CafePreviewResponse? {
        val filteringApi = RetrofitClient.create(MapApi::class.java)
        Log.d("preview", "id: $id")
        val response = filteringApi.getPreview(cafeId = id)
        if (response.isSuccessful) return response.body()!!
        else {
            return null
        }
    }

    private suspend fun postCafe() {
        val api = RetrofitClient.create(CafeDetailAPI::class.java)
        val postResponse = api.postCafe(
            com.konkuk.mocacong.remote.models.request.CafeDetailRequest(
                cafe.id,
                cafe.place_name
            )
        )
        if (postResponse.isSuccessful) return
    }

    companion object {
        @JvmStatic
        fun newInstance(cafe: Place) =
            CafePreviewFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("cafe", cafe)
                }
            }
    }
}