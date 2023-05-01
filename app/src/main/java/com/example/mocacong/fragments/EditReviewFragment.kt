package com.example.mocacong.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mocacong.R
import com.example.mocacong.adapter.EditListAdapter
import com.example.mocacong.data.objects.RetrofitClient
import com.example.mocacong.databinding.FragmentEditReviewBinding
import com.example.mocacong.network.ReviewAPI
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class EditReviewFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentEditReviewBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditReviewBinding.inflate(inflater, container, false)

        setRecyclerLayout()
        completeOnclicked()

        return binding.root
    }

    private fun completeOnclicked() {
        binding.completeBtn.setOnClickListener {
            //post
            lifecycleScope.launch {
                postReview()
            }

        }
    }

    private suspend fun postReview() {
        val api = RetrofitClient.create(ReviewAPI::class.java)
        //api.postReview(cafeId = )
    }

    private fun setRecyclerLayout() {
        val lvArrs = arrayListOf<Array<String>>()
        lvArrs.add(resources.getStringArray(R.array.wifiLevels))
        lvArrs.add(resources.getStringArray(R.array.parkingLevels))
        lvArrs.add(resources.getStringArray(R.array.toiletLevels))
        lvArrs.add(resources.getStringArray(R.array.deskLevels))
        lvArrs.add(resources.getStringArray(R.array.powerLevels))
        lvArrs.add(resources.getStringArray(R.array.soundLevels))

        val adapter = EditListAdapter(resources.getStringArray(R.array.reviewLabels), lvArrs)
        binding.editRecyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL, false)
        binding.editRecyclerView.adapter = adapter


    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}