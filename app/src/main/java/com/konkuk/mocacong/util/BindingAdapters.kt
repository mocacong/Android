package com.konkuk.mocacong.util

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButtonToggleGroup
import com.konkuk.mocacong.R
import com.konkuk.mocacong.data.entities.Comment
import com.konkuk.mocacong.presentation.detail.CafeCommentView
import com.konkuk.mocacong.presentation.detail.CafeCommentsAdapter
import com.konkuk.mocacong.presentation.detail.ReviewButtonGroup
import com.konkuk.mocacong.presentation.models.CafeCommentsUiModel
import com.willy.ratingbar.ScaleRatingBar

object BindingAdapters {

    @BindingAdapter("clipImage")
    @JvmStatic
    fun setClipToOutline(imageView: ImageView, clip: Boolean?) {
        if (clip != null) {
            imageView.clipToOutline = clip
        }
    }

    @BindingAdapter("profileImgUrl")
    @JvmStatic
    fun setProfileImageUrl(imageView: ImageView, url: String?) {
        if (url.isNullOrBlank()) imageView.setImageResource(R.drawable.img_no_profile)
        else Glide.with(imageView.context).load(url).into(imageView)
    }

    @BindingAdapter("isSelected")
    @JvmStatic
    fun setIsSelected(view: View, isSelected: Boolean?) {
        if (isSelected != null)
            view.isSelected = isSelected
    }

    @BindingAdapter("myLabel")
    @JvmStatic
    fun setMyReview(reviewButtonGroup: ReviewButtonGroup, label: String?) {
        if (label != null) {
            reviewButtonGroup.setInitialButtonSelected(label)
        }
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


    @BindingAdapter("comments")
    @JvmStatic
    fun setCafeComments(recyclerView: RecyclerView, model: CafeCommentsUiModel?) {
        if (model != null) {
            with(recyclerView.adapter as CafeCommentsAdapter) {
                this.isEnd = model.isEnd
                this.comments = model.comments
                notifyDataSetChanged()
                if(model.page==0) recyclerView.layoutManager?.scrollToPosition(0)
            }
        }
    }

    @BindingAdapter("comment")
    @JvmStatic
    fun setComment(commentView: CafeCommentView, comment: Comment?) {
        commentView.setComment(comment)
    }

    @BindingAdapter("cafe_rating")
    @JvmStatic
    fun setRating(ratingBar: ScaleRatingBar, rating: Float?) {
        if (rating != null) {
            ratingBar.rating = rating
        }
    }

    @BindingAdapter("studyType")
    @JvmStatic
    fun setStudyType(group: MaterialButtonToggleGroup, type: String?) {
        group.uncheck(R.id.grouped_soloBtn)
        group.uncheck(R.id.grouped_groupBtn)
        when (type) {
            "solo" -> {
                group.check(R.id.grouped_soloBtn)
            }
            "group" -> {
                group.check(R.id.grouped_groupBtn)
            }
            "both" -> {
                group.check(R.id.grouped_soloBtn)
                group.check(R.id.grouped_groupBtn)
            }
        }

    }
}