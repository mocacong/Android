package com.konkuk.mocacong.presentation.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.konkuk.mocacong.databinding.ItemCafeCommentBinding
import com.konkuk.mocacong.remote.models.response.Comment

class CafeCommentsAdapter(
    private val commentList: MutableList<Comment>
) : RecyclerView.Adapter<CafeCommentsAdapter.MyViewHolder>() {


    inner class MyViewHolder(private val binding: ItemCafeCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) {
            binding.commentView.apply {
                setCommentView(comment)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemCafeCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(commentList[position])
    }

    override fun getItemCount(): Int {
        return commentList.size
    }


}
