//package com.konkuk.mocacong.presentation.detail
//
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.activityViewModels
//import androidx.lifecycle.lifecycleScope
//import com.google.android.material.bottomsheet.BottomSheetDialogFragment
//import com.konkuk.mocacong.databinding.FragmentEditReviewBinding
//import com.konkuk.mocacong.objects.Utils
//import com.konkuk.mocacong.remote.models.response.MyReviewResponse
//import com.konkuk.mocacong.util.ApiState
//import com.konkuk.mocacong.util.TokenExceptionHandler
//import kotlinx.coroutines.launch
//
//class EditReviewFragment : BottomSheetDialogFragment() {
//
//    private var _binding: FragmentEditReviewBinding? = null
//    private val binding get() = _binding!!
//    private val viewModel: CafeDetailViewModel by activityViewModels()
//
//    val TAG = "EditReview"
//
//    private lateinit var cafeId: String
//    private var isFirst = true
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentEditReviewBinding.inflate(inflater, container, false)
//
//        cafeId = arguments?.getString("cafeId")!!
//
//        setInitialLayout()
//        setListeners()
//
//        return binding.root
//    }
//
//    private fun setInitialLayout() {
//        binding.apply {
//            powerGroup.setRequiredView()
//            deskGroup.setRequiredView()
//        }
//        lifecycleScope.launch {
//            viewModel.apply {
//                getMyReview(cafeId).join()
//                when (val apiState = myReviewFlow.value) {
//                    is ApiState.Success -> {
//                        apiState.data?.let { myReview ->
//                            if (myReview.myScore == 0) {
//                                isFirst = true
//                            } else {
//                                isFirst = false
//                                setButtonState(myReview)
//                            }
//                        }
//                    }
//                    is ApiState.Error -> {
//                        apiState.errorResponse?.let { er ->
//                            TokenExceptionHandler.handleTokenException(requireContext(), er)
//                            Utils.showToast(requireContext(), er.message)
//                            Log.e(TAG, er.message)
//                        }
//                        mMyReviewFlow.value = ApiState.Loading()
//                    }
//                    is ApiState.Loading -> {}
//                }
//            }
//        }
//    }
//
//    private fun setButtonState(myReview: MyReviewResponse) {
//        binding.apply {
//            ratingBar.rating = myReview.myScore.toFloat()
//            when (myReview.myStudyType) {
//                "solo" -> {
//                    soloBtn.performClick()
//                }
//                "group" -> {
//                    withBtn.performClick()
//                }
//                "both" -> {
//                    soloBtn.performClick()
//                    withBtn.performClick()
//                }
//            }
//
//            wifiGroup.setInitialButtonSelected(myReview.myWifi)
//            parkingGroup.setInitialButtonSelected(myReview.myParking)
//            deskGroup.setInitialButtonSelected(myReview.myDesk)
//            toiletGroup.setInitialButtonSelected(myReview.myToilet)
//            powerGroup.setInitialButtonSelected(myReview.myPower)
//            soundGroup.setInitialButtonSelected(myReview.mySound)
//        }
//    }
//
//
//    private fun setListeners() {
//        binding.completeBtn.setOnClickListener {
//            if (checkRequiredItems()) {
//                if (isFirst) postReview() else putReview()
//            }
//        }
//
//        binding.cancelBtn.setOnClickListener {
//            dismiss()
//        }
//    }
//
//    private fun checkRequiredItems(): Boolean {
//        if (binding.ratingBar.rating == 0f) {
////            MessageDialog("평점은 1~5점으로 남겨주세요!").show(childFragmentManager, "MessageDialog")
//            return false
//        }
//        if (getStudyType() == "") {
////            MessageDialog("코딩 타입을 남겨주세요!").show(childFragmentManager, "MessageDialog")
//            return false
//        }
//        if (binding.powerGroup.getSelectedLabel() == null || binding.deskGroup.getSelectedLabel() == null) {
////            MessageDialog("필수 항목(*)을 체크해주세요!").show(childFragmentManager, "MessageDialog")
//            return false
//        }
//        return true
//    }
//
//    private fun postReview() = lifecycleScope.launch {
//        viewModel.apply {
//            val review = com.konkuk.mocacong.remote.models.request.ReviewRequest(
//                myScore = binding.ratingBar.rating.toInt(),
//                myStudyType = getStudyType(),
//                myWifi = binding.wifiGroup.getSelectedLabel(),
//                myParking = binding.parkingGroup.getSelectedLabel(),
//                myDesk = binding.deskGroup.getSelectedLabel(),
//                myPower = binding.powerGroup.getSelectedLabel(),
//                mySound = binding.soundGroup.getSelectedLabel(),
//                myToilet = binding.toiletGroup.getSelectedLabel()
//            )
//
//            Log.d(TAG, review.toString())
//            postMyReview(cafeId, review).join()
//            when (val apiState = postReviewFlow.value) {
//                is ApiState.Success -> {
//                    Utils.showToast(requireContext(), "리뷰가 등록되었습니다")
//                    (activity as CafeDetailActivity).refreshDetailInfo()
//                    dismiss()
//                }
//                is ApiState.Error -> {
//                    apiState.errorResponse?.let { er ->
//                        TokenExceptionHandler.handleTokenException(requireContext(), er)
//                        Utils.showToast(requireContext(), er.message)
//                        Log.e(TAG, er.message)
//                    }
//                    mPostReviewFlow.value = ApiState.Loading()
//                }
//                is ApiState.Loading -> {}
//            }
//        }
//    }
//
//    private fun putReview() = lifecycleScope.launch {
//        viewModel.apply {
//            val review = com.konkuk.mocacong.remote.models.request.ReviewRequest(
//                myScore = binding.ratingBar.rating.toInt(),
//                myStudyType = getStudyType(),
//                myWifi = binding.wifiGroup.getSelectedLabel(),
//                myParking = binding.parkingGroup.getSelectedLabel(),
//                myDesk = binding.deskGroup.getSelectedLabel(),
//                myPower = binding.powerGroup.getSelectedLabel(),
//                mySound = binding.soundGroup.getSelectedLabel(),
//                myToilet = binding.toiletGroup.getSelectedLabel()
//            )
//            Log.d(TAG, review.toString())
//            putMyReview(cafeId, review).join()
//            when (val apiState = putReviewFlow.value) {
//                is ApiState.Success -> {
//                    Utils.showToast(requireContext(), "리뷰가 수정되었습니다")
//                    (activity as CafeDetailActivity).refreshDetailInfo()
//                    dismiss()
//                }
//                is ApiState.Error -> {
//                    apiState.errorResponse?.let { er ->
//                        TokenExceptionHandler.handleTokenException(requireContext(), er)
//                        Utils.showToast(requireContext(), er.message)
//                        Log.e(TAG, er.message)
//                    }
//                    mPutReviewFlow.value = ApiState.Loading()
//                }
//                is ApiState.Loading -> {}
//            }
//        }
//    }
//
//
//    private fun getStudyType(): String {
//        return if (binding.soloBtn.isButtonClicked() && binding.withBtn.isButtonClicked()) {
//            "both"
//        } else if (binding.soloBtn.isButtonClicked()) {
//            "solo"
//        } else if (binding.withBtn.isButtonClicked()) {
//            "group"
//        } else
//            ""
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//
//}