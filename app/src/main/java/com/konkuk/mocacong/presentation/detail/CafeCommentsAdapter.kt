package com.konkuk.mocacong.presentation.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.konkuk.mocacong.R
import com.konkuk.mocacong.data.entities.Comment
import com.konkuk.mocacong.databinding.LayoutCafeCommentBinding
import com.konkuk.mocacong.databinding.LayoutMoreFooterBinding

class CafeCommentsAdapter(
    val btnClickListener: ButtonClickListener
) :
    RecyclerView.Adapter<ViewHolder>() {

    companion object {
        const val VIEW_TYPE_ITEM = 0
        const val VIEW_TYPE_MORE = 1
    }


    interface ButtonClickListener {
        fun onMenuClicked(comment: Comment)
        fun onMoreClicked(isEnd: Boolean)
    }

    var comments: List<Comment> = emptyList()
    var isEnd = false

    inner class ItemViewHolder(private val binding: LayoutCafeCommentBinding) :
        ViewHolder(binding.root) {
        fun bind(comment: Comment) {
            binding.comment = comment
            binding.commentMenuBtn.setOnClickListener {
                btnClickListener.onMenuClicked(comment)
            }
        }

        fun clear() {
            binding.nicknameText.text = "(알 수 없음)"
            binding.profileImg.setImageResource(R.drawable.img_no_profile)
            binding.contentText.text = ""
        }
    }

    inner class FooterViewHolder(private val binding: LayoutMoreFooterBinding) :
        ViewHolder(binding.root) {

        fun bind() {
            binding.root.visibility = if (isEnd) View.GONE else View.VISIBLE
            binding.root.setOnClickListener {
                btnClickListener.onMoreClicked(isEnd)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == comments.size) VIEW_TYPE_MORE else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == VIEW_TYPE_MORE) FooterViewHolder(
            LayoutMoreFooterBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
        else
            ItemViewHolder(
                LayoutCafeCommentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
    }

    override fun getItemCount(): Int = comments.size + 1

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is FooterViewHolder) {
            holder.bind()
        } else if (holder is ItemViewHolder) {
            holder.bind(comments[position])
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is ItemViewHolder) holder.clear()
    }
}