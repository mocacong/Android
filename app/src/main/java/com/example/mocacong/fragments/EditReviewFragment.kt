package com.example.mocacong.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mocacong.activities.CafeDetailActivity
import com.example.mocacong.adapter.EditListAdapter
import com.example.mocacong.data.objects.RetrofitClient
import com.example.mocacong.data.objects.Utils
import com.example.mocacong.data.request.ReviewRequest
import com.example.mocacong.data.response.MyReviewResponse
import com.example.mocacong.data.response.ReviewResponse
import com.example.mocacong.databinding.FragmentEditReviewBinding
import com.example.mocacong.network.CafeDetailAPI
import com.example.mocacong.ui.MessageDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditReviewFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentEditReviewBinding? = null
    private val binding get() = _binding!!
    lateinit var adapter: EditListAdapter

    private val api = RetrofitClient.create(CafeDetailAPI::class.java)

    private lateinit var cafeId: String
    private var isFirst = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditReviewBinding.inflate(inflater, container, false)

        cafeId = arguments?.getString("cafeId")!!

        setLayout()
        setListeners()

        return binding.root
    }

    private fun setLayout() {
        lifecycleScope.launch {
            val myReview =
                withContext(Dispatchers.Default) { getMyReview() }
            if (myReview != null) {
                Log.d("Cafe", "리뷰 정보 가져오기 성공 : ${myReview}")
                setRecyclerLayout(myReview)
            } else {
                Log.d("Cafe", "등록된 리뷰 없음")
                setRecyclerLayout()
            }
        }
    }


    private suspend fun getMyReview(): MyReviewResponse? {
        val response = api.getMyReview(cafeId = cafeId)
        if (response.isSuccessful) {
            Log.d("Cafe", "리뷰 겟 성공 : ${response.body()}")
            return response.body()
        } else {
            return null
        }
    }


    private fun setRecyclerLayout(myReview: MyReviewResponse?) {
        if (myReview == null) {
            Utils.showToast(requireContext(), "리뷰 정보 가져오기 실패")
            setRecyclerLayout()
            return
        }

        //별점
        isFirst = myReview.myScore.toInt() == 0
        binding.ratingBar.rating = myReview.myScore

        //혼자같이
        when (myReview.myStudyType) {
            "solo" -> {
                binding.soloBtn.performClick()
            }
            "group" -> {
                binding.withBtn.performClick()
            }
            "both" -> {
                binding.soloBtn.performClick()
                binding.withBtn.performClick()
            }
        }

        //세부리뷰
        val myRV = HashMap<String, String?>()
        myRV["wifi"] = myReview.myWifi
        myRV["parking"] = myReview.myParking
        myRV["toilet"] = myReview.myToilet
        myRV["power"] = myReview.myPower
        myRV["sound"] = myReview.mySound
        myRV["desk"] = myReview.myDesk

        adapter = EditListAdapter(myRV)
        binding.editRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.editRecyclerView.adapter = adapter
        Log.d("Cafe", "어댑터 부착 완료")

    }

    private fun setRecyclerLayout() {
        adapter = EditListAdapter(HashMap())
        binding.editRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.editRecyclerView.adapter = adapter
    }

    private fun setListeners() {
        binding.completeBtn.setOnClickListener {
            //post
            lifecycleScope.launch {
                if (binding.ratingBar.rating == 0f) {
                    MessageDialog("평점은 1~5점으로 남겨주세요!").show(childFragmentManager, "MessageDialog")
                    return@launch
                }
                if (getStudyType() == "") {
                    MessageDialog("코딩 타입을 남겨주세요!").show(childFragmentManager, "MessageDialog")
                    return@launch
                }
                val refreshData = if (isFirst) postReview() else putReview()
                (activity as CafeDetailActivity).refreshDetailInfo()
                dismiss()
            }
        }

        binding.cancelBtn.setOnClickListener {
            dismiss()
        }


    }

    private suspend fun putReview(): ReviewResponse? {
        val myReview = ReviewRequest(
            binding.ratingBar.rating.toInt(),
            getStudyType(),
            adapter.selectedRVs["wifi"],
            adapter.selectedRVs["parking"],
            adapter.selectedRVs["toilet"],
            adapter.selectedRVs["power"],
            adapter.selectedRVs["sound"],
            adapter.selectedRVs["desk"]
        )

        val response = api.putReview(cafeId = cafeId, myReview = myReview)
        return if (response.isSuccessful) {
            Log.d("EditReview", "리뷰수정성공 : ${response.body()}")
            response.body()
        } else {
            null
        }
    }

    private fun getStudyType(): String {
        return if (binding.soloBtn.isButtonClicked() && binding.withBtn.isButtonClicked()) {
            "both"
        } else if (binding.soloBtn.isButtonClicked()) {
            "solo"
        } else if (binding.withBtn.isButtonClicked()) {
            "group"
        } else
            ""
    }


    private suspend fun postReview(): ReviewResponse? {
        val myReview = ReviewRequest(
            binding.ratingBar.rating.toInt(),
            getStudyType(),
            adapter.selectedRVs["wifi"],
            adapter.selectedRVs["parking"],
            adapter.selectedRVs["toilet"],
            adapter.selectedRVs["power"],
            adapter.selectedRVs["sound"],
            adapter.selectedRVs["desk"]
        )

        val response = api.postReview(cafeId = cafeId, myReview = myReview)
        if (response.isSuccessful) {
            Log.d("EditReview", "review POST Success : ${response.body()}")
            return response.body()
        } else {
            return null
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}