package com.konkuk.mocacong.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.konkuk.mocacong.adapter.MypageCommentsAdapter
import com.konkuk.mocacong.data.objects.RetrofitClient
import com.konkuk.mocacong.data.response.Cafe
import com.konkuk.mocacong.data.response.MypageCafesResponse
import com.konkuk.mocacong.databinding.FragmentMyCommentsBinding
import com.konkuk.mocacong.network.MyPageAPI
import kotlinx.coroutines.launch

class MyCommentsFragment : Fragment() {

    private var _binding: FragmentMyCommentsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyCommentsBinding.inflate(inflater, container, false)

        setLayout()
        return binding.root
    }

    private lateinit var adapter: MypageCommentsAdapter
    private val cafes = mutableListOf<Cafe>()
    private var currentPage = 0
    private var isEnd = false


    private fun setLayout() {
        lifecycleScope.launch {
            val response = getMyComments(page = currentPage++)
            if (response != null) {
                cafes.addAll(response.cafes)
                isEnd = response.isEnd
                adapter = MypageCommentsAdapter(cafes)
                binding.recyclerView.adapter = adapter
                binding.recyclerView.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
        }

        binding.recyclerView.setOnScrollChangeListener { view, _, _, _, _ ->
            if (!view.canScrollVertically(1) && !isEnd)
                loadNextPage()
        }
    }

    private suspend fun getMyComments(page: Int): MypageCafesResponse? {
        val api = RetrofitClient.create(MyPageAPI::class.java)
        val response = api.getMyComments(page = page)
        Log.d("Mypage", "작성댓글 get 호출함  : page $page")
        return if (response.isSuccessful) {
            Log.d("Mypage", "작성댓글 get : ${response.body()}")
            response.body()
        } else {
            null
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadNextPage() {
        binding.progressBar.visibility = View.VISIBLE

        // 다음 페이지 가져옴
        lifecycleScope.launch {
            val response = getMyComments(page = currentPage++)
            if (response != null) {
                cafes.addAll(response.cafes)
                adapter.notifyItemRangeInserted(
                    cafes.size - response.cafes.size,
                    response.cafes.size
                )
                isEnd = response.isEnd
                binding.progressBar.visibility = View.GONE
            }
        }
    }


}