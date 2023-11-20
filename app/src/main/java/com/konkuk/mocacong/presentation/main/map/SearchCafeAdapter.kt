package com.konkuk.mocacong.presentation.main.map

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.konkuk.mocacong.databinding.ItemCafeListsBinding
import com.konkuk.mocacong.remote.models.response.Place

class SearchCafeAdapter(
    val itemClickedListener: OnSearchItemClickedListener
) : RecyclerView.Adapter<SearchCafeAdapter.MyViewHolder>() {

    var cafeList = listOf<Place>()

    interface OnSearchItemClickedListener {
        fun onItemClicked(place: Place)
    }

    inner class MyViewHolder(private val binding: ItemCafeListsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(place: Place) {
            binding.cafe.text = place.place_name
            binding.addrText.text = place.road_address_name

            binding.root.setOnClickListener {
                itemClickedListener.onItemClicked(place)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemCafeListsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = cafeList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(cafeList[position])
    }

}