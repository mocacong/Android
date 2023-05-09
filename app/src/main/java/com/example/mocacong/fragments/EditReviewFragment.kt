package com.example.mocacong.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mocacong.R
import com.example.mocacong.activities.CafeDetailActivity
import com.example.mocacong.adapter.EditListAdapter
import com.example.mocacong.data.objects.RetrofitClient
import com.example.mocacong.data.request.ReviewRequest
import com.example.mocacong.data.response.ReviewResponse
import com.example.mocacong.databinding.FragmentEditReviewBinding
import com.example.mocacong.network.ReviewAPI
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class EditReviewFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentEditReviewBinding? = null
    private val binding get() = _binding!!
    lateinit var adapter: EditListAdapter

    lateinit var cafeId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditReviewBinding.inflate(inflater, container, false)

        setRecyclerLayout()
        setListeners()

        return binding.root
    }

    private fun setListeners() {
        binding.completeBtn.setOnClickListener {
            //post
            lifecycleScope.launch {
                cafeId = arguments?.getString("cafeId")!!
                val refreshData = postReview()
                (activity as CafeDetailActivity).refreshDetailInfo(refreshData)
                dismiss()
            }
        }

        binding.cancelBtn.setOnClickListener {
            dismiss()
        }

    }


    private suspend fun postReview() : ReviewResponse?{
        val api = RetrofitClient.create(ReviewAPI::class.java)
        val myReview = ReviewRequest(
            binding.ratingBar.rating.toInt(),
            adapter.selectedRVs["studyType"],
            adapter.selectedRVs[getString(R.string.wifi)],
            adapter.selectedRVs[getString(R.string.parking)],
            adapter.selectedRVs[getString(R.string.toilet)],
            adapter.selectedRVs[getString(R.string.power)],
            adapter.selectedRVs[getString(R.string.sound)],
            adapter.selectedRVs[getString(R.string.desk)]
        )

        val response = api.postReview(cafeId = cafeId, myReview = myReview)
        if (response.isSuccessful) {
            Log.d("EditReview", "review POST Success : ${response.body()}")
            return response.body()
        } else {
            Log.d("EditReview", "review POST Failed : ${response.errorBody()?.string()}")
            return null
        }
    }

    private fun setRecyclerLayout() {
        val lvArrs = arrayListOf<Array<String>>()
        lvArrs.add(resources.getStringArray(R.array.wifiLevels))
        lvArrs.add(resources.getStringArray(R.array.parkingLevels))
        lvArrs.add(resources.getStringArray(R.array.toiletLevels))
        lvArrs.add(resources.getStringArray(R.array.deskLevels))
        lvArrs.add(resources.getStringArray(R.array.powerLevels))
        lvArrs.add(resources.getStringArray(R.array.soundLevels))

        adapter = EditListAdapter(resources.getStringArray(R.array.reviewLabels), lvArrs)
        binding.editRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.editRecyclerView.adapter = adapter


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}