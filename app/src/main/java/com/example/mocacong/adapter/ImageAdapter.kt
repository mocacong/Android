package com.example.mocacong.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mocacong.R
import com.example.mocacong.data.response.CafeImage
import com.example.mocacong.databinding.ItemImageBinding

class ImageAdapter(private val imageList: List<CafeImage>) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(private val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(image: CafeImage) {
            if(image.imageUrl == null) binding.imageView.setImageResource(R.drawable.profile_no_image)
            else Glide.with(binding.imageView.context).load(image.imageUrl).into(binding.imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageItem = imageList[position]
        holder.bind(imageItem)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

}
