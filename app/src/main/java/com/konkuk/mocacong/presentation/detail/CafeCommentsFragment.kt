package com.konkuk.mocacong.presentation.detail

import androidx.fragment.app.activityViewModels
import com.konkuk.mocacong.R
import com.konkuk.mocacong.databinding.FragmentCafeCommentsBinding
import com.konkuk.mocacong.presentation.base.BaseFragment

class CafeCommentsFragment : BaseFragment<FragmentCafeCommentsBinding>(){
    override val TAG = "CafeCommentsFragment"
    override val layoutRes: Int= R.layout.fragment_cafe_comments

    private val cafeViewModel: CafeDetailViewModel by activityViewModels()

    override fun afterViewCreated() {

    }



}