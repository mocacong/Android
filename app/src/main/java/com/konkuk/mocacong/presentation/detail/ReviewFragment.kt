package com.konkuk.mocacong.presentation.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.konkuk.mocacong.databinding.FragmentReviewBinding

class ReviewFragment: BottomSheetDialogFragment() {

    private val viewModel : CafeDetailViewModel by activityViewModels()
    private var _binding: FragmentReviewBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewBinding.inflate(inflater, container, false)
        return binding.root
    }
}