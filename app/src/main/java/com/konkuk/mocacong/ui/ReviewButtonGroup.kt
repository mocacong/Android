package com.konkuk.mocacong.ui

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.konkuk.mocacong.R
import com.konkuk.mocacong.databinding.ItemReviewButtonGroupBinding


class ReviewButtonGroup @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val category: String
    private val binding: ItemReviewButtonGroupBinding
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

        binding = ItemReviewButtonGroupBinding.inflate(LayoutInflater.from(context), this, true)
        setLevelText()
        setClickListeners()

    }

    private fun setLevelText() {
        val resourceId = resources.getIdentifier("level_$category", "array", context.packageName)

        if (resourceId != 0) {
            levelStringArr = resources.getStringArray(resourceId)

            Log.d("UI", "level_${category} 배열 가져옴 ${levelStringArr[0]}")

            binding.apply {
                labelText.text = levelStringArr[0]
                firstText.text = levelStringArr[1]
                secondText.text = levelStringArr[2]
                thirdText.text = levelStringArr[3]
            }
        } else {
            // Handle the case where the resourceId is not found
            Log.d("UI", "level_${category} ID 찾을 수 없음")

        }
    }

    private fun setClickListeners() {
        binding.apply {
            firstBtn.setOnClickListener {
                if (it.isSelected) selectButton(0)
                else selectButton(1)
            }
            secondBtn.setOnClickListener {
                if (it.isSelected) selectButton(0)
                else selectButton(2)
            }
            thirdBtn.setOnClickListener {
                if (it.isSelected) selectButton(0)
                else selectButton(3)
            }
        }
    }

    private fun selectButton(level: Int) {
        binding.apply {

            Log.d("UI", "[Level_${category}] : \'$level\' 선택됨")
            firstBtn.isSelected = false
            secondBtn.isSelected = false
            thirdBtn.isSelected = false

            when (level) {
                0 -> {
                    selectedLevel = 0
                }
                1 -> {
                    firstBtn.isSelected = true
                    selectedLevel = 1
                }
                2 -> {
                    secondBtn.isSelected = true
                    selectedLevel = 2
                }
                3 -> {
                    thirdBtn.isSelected = true
                    selectedLevel = 3
                }
            }
        }
    }

    fun setInitialButtonSelected(levelString: String?){
        if(levelStringArr.contains(levelString)){
            selectButton(levelStringArr.indexOf(levelString))
        }else{
            Log.d("UI","[$levelString] 선택안됨!!")
        }
    }



    fun getCategory(): String = this.category

    fun getSelectedLabel(): String? {
        return if(selectedLevel==0) null
        else levelStringArr[selectedLevel]
    }

    fun setRequiredView(){
        binding.requiredCheckText.visibility = View.VISIBLE
        setBadge()
    }

    private fun setBadge() {

    }
}
