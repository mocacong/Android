package com.konkuk.mocacong.presentation.main.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.konkuk.mocacong.databinding.ItemMyCommentBinding

class MyCafeCommentsAdapter(
    var comments: List<String>
) :
    RecyclerView.Adapter<MyCafeCommentsAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(private val binding: ItemMyCommentBinding) :
        ViewHolder(binding.root) {
        fun bind(comment: String) {
            binding.commentText.text = comment
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemMyCommentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = comments.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(comments[position])
    }
}