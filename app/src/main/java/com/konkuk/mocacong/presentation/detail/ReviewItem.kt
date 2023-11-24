package com.konkuk.mocacong.presentation.detail

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.getResourceIdOrThrow
import com.konkuk.mocacong.R
import com.konkuk.mocacong.databinding.ItemReviewBinding


@SuppressLint("DiscouragedApi")
class ReviewItem @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: ItemReviewBinding
    private val resId: Int
    private val category: String
    var reviewStr: String
    private lateinit var levelStringArr: Array<String>

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.CustomViewAttrs, 0, 0)
            .apply {
                try {
                    resId = getResourceIdOrThrow(R.styleable.CustomViewAttrs_reviewIconImg)
                    category = getString(R.styleable.CustomViewAttrs_reviewCategory).toString()
                    reviewStr = getString(R.styleable.CustomViewAttrs_reviewString).toString()
                } finally {
                    recycle()
                }
            }

        binding = ItemReviewBinding.inflate(LayoutInflater.from(context), this, true)
        setDrawable()

        val resourceId = resources.getIdentifier(category, "array", context.packageName)
        if (resourceId != 0) {
            levelStringArr = resources.getStringArray(resourceId)
        } else {
            Log.d("UI", "${category} ID 찾을 수 없음")
        }
        setTextView()
    }

    fun setReviewString(reviewString: String) {
        this.reviewStr = reviewString
        setTextView()
        this.invalidate()
        this.requestLayout()
        tag = reviewString
    }


    private fun setDrawable() {
        binding.imageView.setImageResource(resId)
    }

    private fun setTextView() {
        when (levelStringArr.indexOf(reviewStr)) {
            1 -> {
                binding.labelText.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.level1))
                binding.labelText.text = levelStringArr[1]
            }
            2 -> {
                binding.labelText.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.level2))
                binding.labelText.text = levelStringArr[2]
            }
            3 -> {
                binding.labelText.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.level3))
                binding.labelText.text = levelStringArr[3]
            }
            else -> {
                binding.labelText.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.stroke))
                binding.labelText.text = "리뷰가 없어요"
            }
        }
    }


}
