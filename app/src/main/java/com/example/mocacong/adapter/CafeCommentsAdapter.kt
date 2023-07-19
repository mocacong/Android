package com.example.mocacong.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mocacong.R
import com.example.mocacong.data.response.Comment
import com.example.mocacong.databinding.ItemCommentViewBinding
import com.example.mocacong.ui.CommentView

class CafeCommentsAdapter(
    private val commentList: MutableList<Comment>
) : RecyclerView.Adapter<CafeCommentsAdapter.MyViewHolder>() {
    inner class MyViewHolder(private val binding: ItemCommentViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) {
            comment.imgUrl?.let {imgUrl->
                if(imgUrl == null) binding.profileImage.setImageResource(R.drawable.profile_no_image)
                else Glide.with(binding.profileImage.context).load(imgUrl).into(binding.profileImage)
            }
            comment.nickname?.let {
                binding.nicknameTextView.text = it
            }
            binding.commentTextView.text = comment.content
            val cmt = binding.commentTextView
            cmt.post {
                val lineCount = cmt.lineCount
                if(lineCount>0){
                    if(cmt.layout.getEllipsisCount(lineCount-1)>0){
                        binding.moreButton.apply {
                            visibility = ConstraintLayout.VISIBLE
                            setOnClickListener{
                                cmt.maxLines = Int.MAX_VALUE
                                visibility = ConstraintLayout.GONE
                            }
                        }
                    }
                }
            }

            if(comment.isMe) binding.myReview.visibility = View.VISIBLE
            else binding.myReview.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemCommentViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(commentList[position])
    }


    override fun getItemCount(): Int {
        return commentList.size
    }


}
