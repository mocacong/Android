package com.konkuk.mocacong.presentation.detail

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.konkuk.mocacong.R
import com.konkuk.mocacong.data.entities.Review
import com.konkuk.mocacong.databinding.LayoutReviewBtnBinding


class ReviewButtonGroup @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val category: String
    private val binding: LayoutReviewBtnBinding
    private lateinit var levelStringArr: Array<String>
    private var selectedLevel = 0

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.ReviewButtonGroup, 0, 0)
            .apply {
                try {
                    category = getString(R.styleable.ReviewButtonGroup_category).toString()
                    Log.d("UI", "group [${category}] 생성")
                } finally {
                    recycle()
                }
            }

        binding = LayoutReviewBtnBinding.inflate(LayoutInflater.from(context), this, true)
        setLevelText()
        setClickListeners()

    }

    @SuppressLint("DiscouragedApi")
    private fun setLevelText() {
        val resourceId = resources.getIdentifier(category, "array", context.packageName)

        if (resourceId != 0) {
            levelStringArr = resources.getStringArray(resourceId)
            Log.d("UI", "${category} 배열 가져옴 ${levelStringArr[0]}")
            binding.review = Review(category, levelStringArr.toList())
        } else {
            Log.d("UI", "${category} ID 찾을 수 없음")
        }
    }

    private fun setClickListeners() {
        binding.apply {
            btnLv1.setOnClickListener {
                if (it.isSelected) selectButton(0)
                else selectButton(1)
            }
            btnLv2.setOnClickListener {
                if (it.isSelected) selectButton(0)
                else selectButton(2)
            }
            btnLv3.setOnClickListener {
                if (it.isSelected) selectButton(0)
                else selectButton(3)
            }
        }
    }

    private fun selectButton(level: Int) {
        binding.apply {
            btnLv1.isSelected = false
            btnLv2.isSelected = false
            btnLv3.isSelected = false

            when (level) {
                0 -> {
                    selectedLevel = 0
                }
                1 -> {
                    btnLv1.isSelected = true
                    selectedLevel = 1
                }
                2 -> {
                    btnLv2.isSelected = true
                    selectedLevel = 2
                }
                3 -> {
                    btnLv3.isSelected = true
                    selectedLevel = 3
                }
            }
        }
    }

    fun setInitialButtonSelected(levelString: String?) {
        if (levelStringArr.contains(levelString)) {
            selectButton(levelStringArr.indexOf(levelString))
        } else {
            Log.d("UI", "[$levelString] 선택안됨!!")
        }
    }

    fun getSelectedLabel(): String? {
        return if (selectedLevel == 0) null
        else levelStringArr[selectedLevel]
    }

}
