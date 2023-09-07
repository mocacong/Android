package com.konkuk.mocacong.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.konkuk.mocacong.R
import com.konkuk.mocacong.data.response.Comment
import com.konkuk.mocacong.databinding.ItemCommentViewBinding

class CafeCommentsAdapter(
    private val commentList: MutableList<Comment>
) : RecyclerView.Adapter<CafeCommentsAdapter.MyViewHolder>() {
    inner class MyViewHolder(private val binding: ItemCommentViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) {
            comment.imgUrl?.let {imgUrl->
                Log.d("qaTest","프사 URL: $imgUrl")
                if(imgUrl == null) binding.profileImage.setImageResource(R.drawable.comment_profile)
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
                    }else binding.moreButton.visibility = View.GONE
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
