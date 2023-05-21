package com.example.mocacong.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mocacong.data.response.Cafe
import com.example.mocacong.databinding.ItemMyCafesBinding

class MypageReviewsAdapter(
    private val cafeList: MutableList<Cafe>
) : RecyclerView.Adapter<MypageReviewsAdapter.MyViewHolder>() {
    inner class MyViewHolder(private val binding: ItemMyCafesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cafe: Cafe) {
            binding.cafe.text = cafe.name

            val score = cafe.mergedScore.toFloat()
            if (score == 0f) binding.scoreImgs.visibility = View.INVISIBLE
            else {
                binding.scoreImgs.visibility = View.VISIBLE
                binding.scoreImgs.rating = score
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemMyCafesBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(cafeList[position])
    }

    override fun getItemCount(): Int {
        return cafeList.size
    }


}
