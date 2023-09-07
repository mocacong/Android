package com.konkuk.mocacong.ui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.konkuk.mocacong.R

class CustomButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {

    private var isClicked = false
    private val background : GradientDrawable

    init {
        //초기 모양
        setTextColor(ContextCompat.getColor(context, R.color.darkBrown))
        background = GradientDrawable()
        background.cornerRadius = 20f // 둥근 사각형 반지름 값
        background.setColor(ContextCompat.getColor(context, R.color.stroke))
        setBackground(background)

        setOnClickListener {
            isClicked = !isClicked
            updateButtonState()
        }
    }

    private fun updateButtonState() {
        if (isClicked) {
            background.setColor(ContextCompat.getColor(context, R.color.darkBrown))
            setBackground(background)
            setTextColor(Color.WHITE)
        } else {
            background.setColor(ContextCompat.getColor(context, R.color.stroke))
            setBackground(background)
            setTextColor(ContextCompat.getColor(context, R.color.darkBrown))
        }
    }

    fun isButtonClicked(): Boolean {
        return isClicked
    }
}
