package com.konkuk.mocacong.presentation.detail.comment

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.konkuk.mocacong.R
import com.konkuk.mocacong.data.entities.Comment
import com.konkuk.mocacong.databinding.LayoutCafeCommentBinding

class CafeCommentView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: LayoutCafeCommentBinding

    init {
        binding = LayoutCafeCommentBinding.bind(
            LayoutInflater.from(context).inflate(R.layout.layout_cafe_comment, this, false)
        )
        addView(binding.root)
    }

    fun setComment(comment: Comment?) {
        if (comment == null) binding.root.visibility = View.GONE
        else {
            binding.root.visibility = View.VISIBLE
            binding.profileImg.clipToOutline = true
            binding.commentMenuBtn.visibility = View.GONE
            if (comment.imgUrl.isNullOrBlank()) binding.profileImg.setImageResource(R.drawable.img_no_profile)
            else Glide.with(context).load(comment.imgUrl).into(binding.profileImg)

            binding.nicknameText.text = comment.nicknameText
            binding.contentText.text = comment.content

            invalidate()
            requestLayout()
        }
    }

}
