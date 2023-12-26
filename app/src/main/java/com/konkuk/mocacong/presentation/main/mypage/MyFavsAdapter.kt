package com.konkuk.mocacong.presentation.main.mypage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.konkuk.mocacong.databinding.ItemMyFavBinding
import com.konkuk.mocacong.databinding.LayoutMoreFooterBinding
import com.konkuk.mocacong.remote.models.response.MyFavorites

class MyFavsAdapter(
    val clickListener: OnItemClickListener
) :
    RecyclerView.Adapter<ViewHolder>() {

    companion object {
        const val VIEW_TYPE_ITEM = 0
        const val VIEW_TYPE_MORE = 1
    }

    interface OnItemClickListener {
        fun onMoreClicked()
        fun onItemClicked(myCafe: MyFavorites)
    }

    var myCafes: List<MyFavorites> = emptyList()
    var isEnd = false

    inner class ItemViewHolder(private val binding: ItemMyFavBinding) :
        ViewHolder(binding.root) {
        fun bind(myCafe: MyFavorites) {
            binding.myCafe = myCafe
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
                ItemMyFavBinding.inflate(
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