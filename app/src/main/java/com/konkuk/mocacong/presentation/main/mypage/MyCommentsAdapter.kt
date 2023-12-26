package com.konkuk.mocacong.presentation.main.mypage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.konkuk.mocacong.databinding.ItemMyCommentsBinding
import com.konkuk.mocacong.databinding.LayoutMoreFooterBinding
import com.konkuk.mocacong.remote.models.response.MyComments

class MyCommentsAdapter(
    val clickListener: OnItemClickListener
) :
    RecyclerView.Adapter<ViewHolder>() {

    companion object {
        const val VIEW_TYPE_ITEM = 0
        const val VIEW_TYPE_MORE = 1
    }

    interface OnItemClickListener {
        fun onMoreClicked()
        fun onItemClicked(myCafe: MyComments)
    }

    var myCafes: List<MyComments> = emptyList()
    var isEnd = false

    inner class ItemViewHolder(private val binding: ItemMyCommentsBinding) :
        ViewHolder(binding.root) {
        fun bind(myCafe: MyComments) {
            binding.myCafe = myCafe
            binding.recyclerView.adapter = MyCafeCommentsAdapter(myCafe.commentContents)
            binding.recyclerView.layoutManager = LinearLayoutManager(binding.root.context)
            binding.root.setOnClickListener {
                clickListener.onItemClicked(myCafe)
            }
        }
    }

    inner class FooterViewHolder(private val binding: LayoutMoreFooterBinding) :
        ViewHolder(binding.root) {

        fun bind() {
            binding.root.visibility = if (isEnd) View.GONE else View.VISIBLE
            binding.root.setOnClickListener {
                clickListener.onMoreClicked()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == myCafes.size) VIEW_TYPE_MORE else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == VIEW_TYPE_MORE) FooterViewHolder(
            LayoutMoreFooterBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
        else
            ItemViewHolder(
                ItemMyCommentsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
    }

    override fun getItemCount(): Int = myCafes.size + 1

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is FooterViewHolder) {
            holder.bind()
        } else if (holder is ItemViewHolder) {
            holder.bind(myCafes[position])
        }
    }
}