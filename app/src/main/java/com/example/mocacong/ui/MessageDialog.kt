package com.example.mocacong.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.mocacong.databinding.DialogMessageBinding

class MessageDialog(message: String) : DialogFragment() {

    private var _binding: DialogMessageBinding? = null
    private val binding get() = _binding!!
    private val message : String

    init {
        this.message = message
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogMessageBinding.inflate(inflater, container, false)

        binding.message.text = message
        binding.okBtn.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    override fun onDetach() {
        super.onDetach()
        _binding = null
    }



}