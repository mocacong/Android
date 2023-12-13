package com.konkuk.mocacong.presentation.detail

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.konkuk.mocacong.R
import com.konkuk.mocacong.databinding.ItemCafeCommentBinding
import com.konkuk.mocacong.remote.models.response.Comment

class CafeCommentView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: ItemCafeCommentBinding
    val nickname:String
    val content: String
    private val profileImg: String?

    init {
        binding = ItemCafeCommentBinding.bind(
            LayoutInflater.from(context).inflate(R.layout.item_cafe_comment, this, false)
        )

        context.theme.obtainStyledAttributes(attrs, R.styleable.CustomViewAttrs, 0, 0)
            .apply {
                try {
                    nickname = getString(R.styleable.CustomViewAttrs_commentNickname).toString()
                    binding.nicknameText.text = nickname

                    content = getString(R.styleable.CustomViewAttrs_commentContent).toString()
                    binding.contentText.text = content

                    profileImg = getString(R.styleable.CustomViewAttrs_commentProfileImg).toString()
                    setProfileImg()
                } finally {
                    recycle()
                }
            }
        addView(binding.root)
    }

    fun setComment(comment: Comment?){
        if(comment==null) binding.root.visibility = View.GONE
        else {
            binding.root.visibility = View.VISIBLE
            if (comment.imgUrl.isNullOrBlank()) binding.profileImg.setImageResource(R.drawable.shape_circle)
            else Glide.with(context).load(comment.imgUrl).into(binding.profileImg)

            binding.nicknameText.text = comment.nickname
            binding.contentText.text = comment.content
        }
    }

    private fun setProfileImg(){
        binding.profileImg.clipToOutline
        if (profileImg.isNullOrBlank()) binding.profileImg.setImageResource(R.drawable.shape_circle)
        else Glide.with(context).load(profileImg).into(binding.profileImg)
    }

}
