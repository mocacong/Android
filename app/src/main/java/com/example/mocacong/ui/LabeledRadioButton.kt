package com.example.mocacong.ui

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRadioButton

class LabeledRadioButton(context: Context, attrs: AttributeSet) :
    AppCompatRadioButton(context, attrs) {

    private val labelView: TextView

    init {
        labelView = TextView(context)
        labelView.text = ""
    }

    fun setLabelText(labelText: String) {
        labelView.text = labelText
        invalidate()
        requestLayout()
    }


    override fun onDraw(canvas: Canvas?) {
        val btnX = (width - (buttonDrawable?.intrinsicWidth ?: 0)) / 2
        val btnY = (height - (buttonDrawable?.intrinsicHeight ?: 0)) / 2
        canvas?.let {
            buttonDrawable?.setBounds(
                btnX,
                btnY,
                btnX + (buttonDrawable?.intrinsicWidth ?: 0),
                btnY + (buttonDrawable?.intrinsicHeight ?: 0)
            )
            buttonDrawable?.draw(it)
        }

        val x = 0
        val y = btnY + buttonDrawable!!.intrinsicHeight + labelView.height / 2 + dpToPx(15)

        canvas?.drawText(labelView.text.toString(), x.toFloat(), y.toFloat(), labelView.paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        labelView.measure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(labelView.measuredWidth, measuredHeight + labelView.measuredHeight + dpToPx(12))
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}
