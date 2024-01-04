package com.konkuk.mocacong.presentation.detail.image

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.konkuk.mocacong.databinding.DialogFullImageBinding
import com.konkuk.mocacong.presentation.detail.CafeDetailViewModel

class FullImageDialog : DialogFragment() {
    internal lateinit var listener: ImageDialogListener
    val binding: DialogFullImageBinding get() = _binding!!
    private var _binding: DialogFullImageBinding? = null

    private val detailViewModel : CafeDetailViewModel by activityViewModels()

    interface ImageDialogListener {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFullImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.image = detailViewModel.currentImage
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}