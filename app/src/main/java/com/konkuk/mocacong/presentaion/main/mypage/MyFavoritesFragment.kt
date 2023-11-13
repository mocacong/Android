package com.konkuk.mocacong.presentaion.main.mypage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.konkuk.mocacong.databinding.FragmentMyFavoritesBinding
import com.konkuk.mocacong.objects.RetrofitClient
import com.konkuk.mocacong.remote.apis.MyPageAPI
import com.konkuk.mocacong.remote.models.response.Cafe
import com.konkuk.mocacong.remote.models.response.MypageCafesResponse
import kotlinx.coroutines.launch

class MyFavoritesFragment : Fragment() {

    private var _binding: FragmentMyFavoritesBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyFavoritesBinding.inflate(inflater, container, false)

        setLayout()
        return binding.root
    }

    private lateinit var adapter: MypageReviewsAdapter
    private val cafes = mutableListOf<Cafe>()
    private var currentPage = 0
    private var isEnd = false


    private fun setLayout() {
        lifecycleScope.launch {
            val response = getMyFavorites(page = currentPage++)
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

    private suspend fun getMyFavorites(page: Int): MypageCafesResponse? {
        val api = RetrofitClient.create(MyPageAPI::class.java)
        val response = api.getMyFavorites(page = page)
        Log.d("Mypage", "즐겨찾기 get 호출함  : page $page")
        return if (response.isSuccessful) {
            Log.d("Mypage", "즐겨찾기 get : ${response.body()}")
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
            val response = getMyFavorites(page = currentPage++)
            if (response != null) {
                cafes.addAll(response.cafes)
                adapter.notifyDataSetChanged()
                isEnd = response.isEnd
                binding.progressBar.visibility = View.GONE
            }
        }
    }


}