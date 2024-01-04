package com.konkuk.mocacong.presentation.detail.image

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.konkuk.mocacong.R
import com.konkuk.mocacong.databinding.ItemImageBinding
import com.konkuk.mocacong.databinding.LayoutMoreFooterBinding
import com.konkuk.mocacong.remote.models.response.CafeImage

class ImageAdapter(
    val btnClickListener: ButtonClickListener
) :
    RecyclerView.Adapter<ViewHolder>() {

    companion object {
        const val VIEW_TYPE_ITEM = 0
        const val VIEW_TYPE_MORE = 1
    }

    interface ButtonClickListener {
        fun onMoreClicked()
        fun onImageClicked(cafeImage: CafeImage)
    }

    var images: List<CafeImage> = emptyList()
    var isEnd = false

    inner class ItemViewHolder(private val binding: ItemImageBinding) :
        ViewHolder(binding.root) {
        fun bind(image: CafeImage) {
            binding.imageView.apply {
                clipToOutline = true
                setOnClickListener {
                    btnClickListener.onImageClicked(image)
                }
            }
            if (image.imageUrl == null) binding.imageView.setImageResource(R.drawable.img_nothing)
            else Glide.with(binding.imageView.context).load(image.imageUrl).into(binding.imageView)

        }
    }

    inner class FooterViewHolder(private val binding: LayoutMoreFooterBinding) :
        ViewHolder(binding.root) {

        fun bind() {
            binding.root.visibility = if (isEnd) View.GONE else View.VISIBLE
            binding.root.setOnClickListener {
                btnClickListener.onMoreClicked()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == images.size) VIEW_TYPE_MORE else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == VIEW_TYPE_MORE) FooterViewHolder(
            LayoutMoreFooterBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
        else
            ItemViewHolder(
                ItemImageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
    }

    override fun getItemCount(): Int = images.size + 1

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is FooterViewHolder) {
            holder.bind()
        } else if (holder is ItemViewHolder) {
            holder.bind(images[position])
        }
    }
}