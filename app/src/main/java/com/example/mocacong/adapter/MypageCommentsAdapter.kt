package com.example.mocacong.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mocacong.data.response.Cafe
import com.example.mocacong.databinding.ItemMyCommentsBinding

class MypageCommentsAdapter(
    private val cafeList: MutableList<Cafe>
) : RecyclerView.Adapter<MypageCommentsAdapter.MyViewHolder>() {
    inner class MyViewHolder(private val binding: ItemMyCommentsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cafe: Cafe) {
            binding.cafe.text = cafe.name
            binding.commentText.text = cafe.comment
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemMyCommentsBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(cafeList[position])
    }

    override fun getItemCount(): Int {
        return cafeList.size
    }


}
