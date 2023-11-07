package com.konkuk.mocacong.presentaion.detail

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.konkuk.mocacong.R
import com.konkuk.mocacong.data.response.Comment
import com.konkuk.mocacong.databinding.CommentViewBinding
import com.konkuk.mocacong.remote.models.response.Comment

class CommentView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: CommentViewBinding
    lateinit var comment: Comment

    init {
        val inflater = LayoutInflater.from(context)
        binding = CommentViewBinding.inflate(inflater, this, true)
    }

    fun setPreview(){
        binding.menuBtn.visibility = GONE
    }

    fun setCommentView(comment: Comment) {
        this.comment = comment
        setProfileImage(comment.imgUrl)
        comment.nickname?.let { setNickname(it) }
        setComment(comment.content)
        setIsMe(comment.isMe)
    }

    private fun setProfileImage(imgUrl: String?) {
        if (imgUrl == null) binding.profileImage.setImageResource(R.drawable.profile_no_image)
        else Glide.with(context).load(imgUrl).into(binding.profileImage)
    }

    private fun setNickname(nickname: String) {
        binding.nicknameTextView.text = nickname
    }

    private fun setComment(comment: String) {
        binding.commentTextView.text = comment
        setViewMore()
    }

    private fun setIsMe(isMe: Boolean) {
        if (isMe) {
            binding.myReview.visibility = VISIBLE
            return
        }

        binding.myReview.visibility = GONE
    }

    private fun setViewMore() {
        val cmt = binding.commentTextView
        cmt.post {
            val lineCount = cmt.lineCount
            if (lineCount > 0) {
                if (cmt.layout.getEllipsisCount(lineCount - 1) > 0) {
                    binding.moreButton.apply {
                        visibility = VISIBLE
                        setOnClickListener {
                            cmt.maxLines = Int.MAX_VALUE
                            visibility = GONE
                        }
                    }
                }
            }
        }
    }
}
