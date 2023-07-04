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
import com.example.mocacong.network.ServerNetworkException
import com.example.mocacong.ui.MessageDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class WriteCommentFragment : BottomSheetDialogFragment() {


    private var _binding: FragmentWriteCommentBinding? = null
    private val binding get() = _binding!!

    private lateinit var api: CafeDetailAPI
    private lateinit var cafeId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentWriteCommentBinding.inflate(inflater, container, false)
        try {
            setLayout()
        } catch (e: ServerNetworkException) {
            MessageDialog(e.responseMessage)
        }

        return binding.root
    }

    private fun setLayout() {
        binding.cancelBtn.setOnClickListener {
            dismiss()
        }
        binding.completeBtn.setOnClickListener {
            lifecycleScope.launch {
                cafeId = arguments?.getString("cafeId")!!
                val isPostSucceed = withContext(Dispatchers.IO) { postComment() }
                if (isPostSucceed) {
                    val cmts = withContext(Dispatchers.IO) { getComment() }
                    (activity as CafeDetailActivity).commentsAdded(cmts)
                    dismiss()
                }
            }
        }
        binding.content.addTextChangedListener {
            val length = "${it?.length.toString()} / 200"
            binding.commentCountText.text = length
        }
    }

    private suspend fun getComment(): List<Comment>? {
        val response = api.getCafeComment(cafeId = cafeId)
        if (response.isSuccessful) return response.body()?.comments
        else {
            return null
        }
    }

    private suspend fun postComment(): Boolean {
        val comment = binding.content.text.toString()
        api = RetrofitClient.create(CafeDetailAPI::class.java)
        if (comment.isBlank()) {
            MessageDialog("공백일 수 없습니다").show(childFragmentManager, "MessageDialog")
            return false
        }
        if (comment.length > 200) {
            MessageDialog("댓글은 최대 200자까지 가능합니다").show(childFragmentManager, "MessageDialog")
            return false
        }
        val response =
            api.postCafeComment(cafeId = cafeId, content = binding.content.text.toString())
        if (response.isSuccessful) {
            Log.d("Cafe", "코멘트 POST 성공 ${response.body()}")
            return true
        } else {
            return false
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}