package com.konkuk.mocacong.presentation.main.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.konkuk.mocacong.databinding.ItemMyCommentsBinding


class MyCommentsAdapter(comments: List<String>) :
    RecyclerView.Adapter<MyCommentsAdapter.MyViewHolder>() {

    lateinit var comments: MutableList<String>

    inner class MyViewHolder(private val binding: ItemMyCommentsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: String) {
            binding.commentText.text = comment
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCommentsAdapter.MyViewHolder {
        val binding =
            ItemMyCommentsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyCommentsAdapter.MyViewHolder, position: Int) {
        holder.bind(comments[position])
    }


    override fun getItemCount(): Int = comments.size


}