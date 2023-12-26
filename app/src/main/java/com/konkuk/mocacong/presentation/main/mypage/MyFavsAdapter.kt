package com.konkuk.mocacong.presentation.main.mypage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.konkuk.mocacong.databinding.ItemMyCafesBinding
import com.konkuk.mocacong.remote.models.response.MyCafe


class MyFavsAdapter(
    val itemClickedListener: OnSearchItemClickedListener,
    val context: Context
) : RecyclerView.Adapter<MyFavsAdapter.MyViewHolder>() {

    lateinit var cafeList : MutableList<MyCafe>

    interface OnSearchItemClickedListener {
        fun onItemClicked(myCafe: MyCafe)
    }

    inner class MyViewHolder(private val binding: ItemMyCafesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(myCafe: MyCafe) {
            binding.myCafe = myCafe
            binding.root.setOnClickListener {
                itemClickedListener.onItemClicked(myCafe)
            }
            when(myCafe){
                is MyCafe.MyFavorites->{
                    binding.recyclerView.visibility = View.GONE
                }
                is MyCafe.MyComments->{
                    binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    binding.recyclerView.adapter = MyCommentsAdapter(myCafe.comment)
                }
                is MyCafe.MyReviews->{

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemMyCafesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = cafeList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(cafeList[position])
    }

}