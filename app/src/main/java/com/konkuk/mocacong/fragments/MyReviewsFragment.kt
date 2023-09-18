package com.konkuk.mocacong.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.konkuk.mocacong.adapter.MypageReviewsAdapter
import com.konkuk.mocacong.data.objects.RetrofitClient
import com.konkuk.mocacong.data.response.Cafe
import com.konkuk.mocacong.data.response.MypageCafesResponse
import com.konkuk.mocacong.databinding.FragmentMyReviewsBinding
import com.konkuk.mocacong.network.MyPageAPI
import kotlinx.coroutines.launch

class MyReviewsFragment : Fragment() {

    private var _binding: FragmentMyReviewsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MypageReviewsAdapter
    private val cafes = mutableListOf<Cafe>()
    private var currentPage = 0
    private var isEnd = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.d("Mypage", "oncreated 나의리뷰 프래그먼트")
        _binding = FragmentMyReviewsBinding.inflate(inflater, container, false)

        setLayout()
        return binding.root
    }

    private fun setLayout() {
        lifecycleScope.launch {
            val response = getMyReviews(page = currentPage++)
            if (response != null) {
                cafes.addAll(response.cafes)
                isEnd = response.isEnd
                adapter = MypageReviewsAdapter(cafes)
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

    private suspend fun getMyReviews(page: Int): MypageCafesResponse? {
        val api = RetrofitClient.create(MyPageAPI::class.java)
        val response = api.getMyReviews(page = page)
        Log.d("Mypage", "나의리뷰get 호출함  : page $page")
        return if (response.isSuccessful) {
            Log.d("Mypage", "나의리뷰 get : ${response.body()}")
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
            val response = getMyReviews(page = currentPage++)
            if (response != null) {
                cafes.addAll(response.cafes)
                adapter.notifyDataSetChanged()
                isEnd = response.isEnd
                binding.progressBar.visibility = View.GONE
            }
        }
    }


}