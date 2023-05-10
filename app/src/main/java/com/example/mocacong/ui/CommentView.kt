package com.example.mocacong.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
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


    fun setProfileImage(@DrawableRes resId: Int) {
        binding.profileImage.setImageResource(resId)
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
