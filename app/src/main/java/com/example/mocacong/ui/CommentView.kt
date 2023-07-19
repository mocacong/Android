package com.example.mocacong.ui

import OnSwipeTouchListener
import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mocacong.R
import com.example.mocacong.databinding.ItemCommentViewBinding


class CommentView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var binding: ItemCommentViewBinding

    init {
        val inflater = LayoutInflater.from(context)
        binding = ItemCommentViewBinding.inflate(inflater, this, true)
    }

    fun setProfileImage(imgUrl: Uri?) {
        if(imgUrl == null) binding.profileImage.setImageResource(R.drawable.profile_no_image)
        else Glide.with(context).load(imgUrl).into(binding.profileImage)
    }


    fun setNickname(nickname: String) {
        binding.nicknameTextView.text = nickname
    }

    fun setComment(comment: String) {
        binding.commentTextView.text = comment
        setViewMore()
    }

    fun setMyComment(my : Boolean){
        if(my) binding.myReview.visibility = VISIBLE
        else binding.myReview.visibility = GONE
    }

    private fun setViewMore(){
        val cmt = binding.commentTextView
        cmt.post {
            val lineCount = cmt.lineCount
            if(lineCount>0){
                if(cmt.layout.getEllipsisCount(lineCount-1)>0){
                    binding.moreButton.apply {
                        visibility = VISIBLE
                        setOnClickListener{
                            cmt.maxLines = Int.MAX_VALUE
                            visibility = GONE
                        }
                    }
                }
            }
        }
    }
}
