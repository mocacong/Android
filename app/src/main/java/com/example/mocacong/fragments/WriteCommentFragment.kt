package com.example.mocacong.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.example.mocacong.activities.CafeDetailActivity
import com.example.mocacong.data.objects.RetrofitClient
import com.example.mocacong.data.response.Comment
import com.example.mocacong.databinding.FragmentWriteCommentBinding
import com.example.mocacong.network.CafeDetailAPI
import com.example.mocacong.ui.MessageDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import org.json.JSONObject

class WriteCommentFragment : BottomSheetDialogFragment() {


    private var _binding: FragmentWriteCommentBinding? = null
    private val binding get() = _binding!!

    private lateinit var api : CafeDetailAPI
    private lateinit var cafeId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentWriteCommentBinding.inflate(inflater, container, false)
        setLayout()

        return binding.root
    }

    private fun setLayout() {
        binding.cancelBtn.setOnClickListener {
            dismiss()
        }
        binding.completeBtn.setOnClickListener {
            lifecycleScope.launch {
                cafeId = arguments?.getString("cafeId")!!
                postComment()
                val cmts = getComment()
                (activity as CafeDetailActivity).commentsAdded(cmts)
                dismiss()
            }
        }
    }

    private suspend fun getComment() : List<Comment>?{
        val response = api.getCafeComment(cafeId=cafeId)
        if(response.isSuccessful) return response.body()?.comments
        else {
            Log.d("Cafe", "코멘트 refresh GET 실패 : ${response.errorBody()?.string()}")
            return null
        }
    }

    private suspend fun postComment() {
        if(binding.content.text.toString() == "") {
            MessageDialog("공백일 수 없습니다").show(childFragmentManager, "MessageDialog")
        }
        api = RetrofitClient.create(CafeDetailAPI::class.java)
        val response = api.postCafeComment(cafeId=cafeId, content = binding.content.text.toString())
        if(response.isSuccessful){
            Log.d("Cafe","코멘트 POST 성공 ${response.body()}")
        }else {
            Log.d("Cafe", "코멘트 POST 실패 ${response.errorBody()?.string()}")
            val json = JSONObject(response.errorBody()?.string())
            val code = json.getInt("code")
            val msg = json.getString("message")
            Log.d("Cafe", "Error: $msg Code: $code")
            MessageDialog(msg).show(childFragmentManager, "MessageDialog")
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}