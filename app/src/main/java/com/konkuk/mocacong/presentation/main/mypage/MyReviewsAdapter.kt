package com.konkuk.mocacong.presentation.main.mypage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.konkuk.mocacong.databinding.ItemMyReviewBinding
import com.konkuk.mocacong.databinding.LayoutMoreFooterBinding
import com.konkuk.mocacong.presentation.models.ReviewsUiModel
import com.konkuk.mocacong.remote.models.response.MyReviews

class MyReviewsAdapter(
    val clickListener: OnItemClickListener
) :
    RecyclerView.Adapter<ViewHolder>() {

    companion object {
        const val VIEW_TYPE_ITEM = 0
        const val VIEW_TYPE_MORE = 1
    }

    interface OnItemClickListener {
        fun onMoreClicked()
        fun onItemClicked(myReviews: MyReviews)
    }

    var myReviews: List<MyReviews> = emptyList()
    var isEnd = false

    inner class ItemViewHolder(private val binding: ItemMyReviewBinding) :
        ViewHolder(binding.root) {
        fun bind(myReview: MyReviews) {
            binding.myReview= myReview
            binding.review = ReviewsUiModel.mypageResponseToUIModel(myReview)
            binding.root.setOnClickListener {
                clickListener.onItemClicked(myReview)
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
        return if (position == myReviews.size) VIEW_TYPE_MORE else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == VIEW_TYPE_MORE) FooterViewHolder(
            LayoutMoreFooterBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
        else
            ItemViewHolder(
                ItemMyReviewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
    }

    override fun getItemCount(): Int = myReviews.size + 1

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is FooterViewHolder) {
            holder.bind()
        } else if (holder is ItemViewHolder) {
            holder.bind(myReviews[position])
        }
    }
}