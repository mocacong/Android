package com.konkuk.mocacong.util

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.konkuk.mocacong.presentation.detail.CafeCommentView
import com.konkuk.mocacong.remote.models.response.Comment
import com.willy.ratingbar.ScaleRatingBar

object BindingAdapters {

    @BindingAdapter("clipImage")
    @JvmStatic
    fun setClipToOutline(imageView: ImageView, clip: Boolean?) {
        if (clip != null) {
            imageView.clipToOutline = clip
        }
    }

    @BindingAdapter("isSelected")
    @JvmStatic
    fun setIsSelected(view: View, isSelected: Boolean?) {
        if (isSelected != null)
            view.isSelected = isSelected
    }

//    @BindingAdapter("foods")
//    @JvmStatic
//    fun setFoodList(recyclerView: RecyclerView, foods: List<Food>?) {
//        with(recyclerView.adapter as FoodsAdapter) {
//            if (foods != null) {
//                this.foods = foods
//                this.deselect()
//                notifyDataSetChanged()
//            }
//        }
//    }


    @BindingAdapter("comment")
    @JvmStatic
    fun setComment(commentView: CafeCommentView, comment: Comment?) {
        commentView.setComment(comment)
    }

    @BindingAdapter("android:rating")
    @JvmStatic
    fun setRating(ratingView: ScaleRatingBar, rating: Float?) {
        if (rating != null) {
            ratingView.rating = rating
        }
    }
}