package com.example.mocacong.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mocacong.R
import com.example.mocacong.activities.CafeDetailActivity
import com.example.mocacong.adapter.EditListAdapter
import com.example.mocacong.data.objects.RetrofitClient
import com.example.mocacong.data.request.ReviewRequest
import com.example.mocacong.data.response.MyReviewResponse
import com.example.mocacong.data.response.ReviewResponse
import com.example.mocacong.databinding.FragmentEditReviewBinding
import com.example.mocacong.network.CafeDetailAPI
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class EditReviewFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentEditReviewBinding? = null
    private val binding get() = _binding!!
    lateinit var adapter: EditListAdapter

    private val api = RetrofitClient.create(CafeDetailAPI::class.java)

    lateinit var cafeId: String
    var isFirst = true
    var soloClicked = false
    var groupClicked = false



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
            val myReview = getMyReview()
            if (myReview != null) {
                Log.d("Cafe","리뷰 정보 가져오기 성공 : ${myReview}")
                setRecyclerLayout(myReview)
            } else {
                Log.d("Cafe","등록된 리뷰 없음")
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
            Log.d("Cafe", "나의리뷰 GET ERROR : ${response.errorBody()?.string()}")
            return null
        }
    }


    private fun setRecyclerLayout(myReview: MyReviewResponse?) {
        if (myReview == null) {
            Toast.makeText(requireActivity(), "리뷰 정보 가져오기 실패", Toast.LENGTH_SHORT).show()
            setRecyclerLayout()
            return
        }

        //별점
        isFirst = myReview.myScore.toInt() == 0
        binding.ratingBar.rating = myReview.myScore

        //혼자같이
        when(myReview.myStudyType){
            "solo"->{
                binding.soloBtn.performClick()
            }
            "group"->{
                binding.withBtn.performClick()
            }
            "both"->{
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
        Log.d("Cafe","어댑터 부착 완료")

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
                val refreshData = if (isFirst) postReview() else putReview()

                (activity as CafeDetailActivity).refreshDetailInfo(refreshData)
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
            Log.d("EditReview", "리뷰수정실패 : ${response.errorBody()?.string()}")
            null
        }
    }

    private fun getStudyType() : String {
        if(binding.soloBtn.isButtonClicked() && binding.withBtn.isButtonClicked()){
            return "both"
        }else if(binding.soloBtn.isButtonClicked()){
            return "solo"
        }else if(binding.withBtn.isButtonClicked()){
            return "group"
        }else
            return ""
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
            Log.d("EditReview", "review POST Failed : ${response.errorBody()?.string()}")
            return null
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}