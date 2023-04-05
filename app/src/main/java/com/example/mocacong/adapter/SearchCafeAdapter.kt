package com.example.mocacong.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mocacong.data.response.Place
import com.example.mocacong.databinding.SearchCafeItemBinding

class SearchCafeAdapter : RecyclerView.Adapter<SearchCafeAdapter.MyViewHolder>() {

    var cafeList = listOf<Place>()

    inner class MyViewHolder(private val binding : SearchCafeItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(place : Place){
            binding.cafe.text = place.place_name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = SearchCafeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = cafeList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(cafeList[position])
    }

}