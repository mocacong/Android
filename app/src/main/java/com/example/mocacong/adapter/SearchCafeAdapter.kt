package com.example.mocacong.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mocacong.activities.MainActivity
import com.example.mocacong.data.response.Place
import com.example.mocacong.databinding.ItemCafeListsBinding

class SearchCafeAdapter : RecyclerView.Adapter<SearchCafeAdapter.MyViewHolder>() {

    var cafeList = listOf<Place>()

    inner class MyViewHolder(private val binding : ItemCafeListsBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(place : Place){
            binding.cafe.text = place.place_name
            binding.addrText.text = place.road_address_name

            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, MainActivity::class.java)
                intent.putExtra("place",place)
                binding.root.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemCafeListsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = cafeList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(cafeList[position])
    }

}