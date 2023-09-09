package com.konkuk.mocacong.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.konkuk.mocacong.data.response.Cafe
import com.konkuk.mocacong.databinding.ItemMyCafesBinding

class MypageReviewsAdapter(
    private val cafeList: MutableList<Cafe>
) : RecyclerView.Adapter<MypageReviewsAdapter.MyViewHolder>() {
    inner class MyViewHolder(private val binding: ItemMyCafesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cafe: Cafe) {
            binding.cafeInfo = cafe
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemMyCafesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(cafeList[position])
    }

    override fun getItemCount(): Int {
        return cafeList.size
    }


}
