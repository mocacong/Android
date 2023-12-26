package com.konkuk.mocacong.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheet<T : ViewDataBinding> : BottomSheetDialogFragment() {
    private var _binding: T? = null
    protected val binding get() = _binding!!

    abstract val TAG: String // fragment 태그
    abstract val layoutRes: Int // 바인딩에 필요한 layout
    private var toast: Toast? = null //토스트 보관 변수

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        afterViewCreated()
    }

    protected abstract fun afterViewCreated()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun showToast(msg: String) {
        toast?.cancel()
        toast = Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT)
        toast?.show()
    }
}