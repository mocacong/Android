package com.example.mocacong.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mocacong.R
import com.example.mocacong.controllers.DetailController
import com.example.mocacong.data.request.CafeDetailRequest
import com.example.mocacong.data.response.CafeResponse
import com.example.mocacong.data.response.Comment
import com.example.mocacong.data.response.Place
import com.example.mocacong.data.response.ReviewResponse
import com.example.mocacong.databinding.ActivityCafeDetailBinding
import com.example.mocacong.fragments.EditReviewFragment
import com.example.mocacong.fragments.WriteCommentFragment
import kotlinx.coroutines.launch
import java.io.Serializable

class CafeDetailActivity : AppCompatActivity() {

    private val controller: DetailController = DetailController()

    private lateinit var binding: ActivityCafeDetailBinding
    private lateinit var cafeId: String
    private lateinit var cafe : Place
    private var isFirst : Boolean = false
    private var isFav = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCafeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getCafeInfo()
        setlayout()
    }



    private fun setlayout() {
        binding.editBtn.setOnClickListener {
            makeEditPopUp()
        }

        binding.writeCommentBtn.setOnClickListener {
            makeCommentPopup()
        }

        binding.commentMoreBtn.setOnClickListener {
            //todo:댓글 더보기 기능
        }

        binding.favBtn.setOnClickListener {
            favoriteClicked()
        }
    }

    private fun favoriteClicked() {
        lifecycleScope.launch {
            val str = if(isFav) controller.deleteFavorite(cafeId) else controller.postFavorite(cafeId)
            //통신 실패했을 때 핸들링 필요
            isFav = !isFav
            Toast.makeText(this@CafeDetailActivity,str, Toast.LENGTH_SHORT).show()
        }
    }


    private fun makeEditPopUp() {
        val bundle = Bundle()
        bundle.putString("cafeId", cafeId)
        if(isFirst)
            bundle.putBoolean("isFirst",true)
        else
            bundle.putBoolean("isFirst",false)

        val editReviewFragment = EditReviewFragment()
        editReviewFragment.arguments = bundle
        editReviewFragment.show(supportFragmentManager, editReviewFragment.tag)
    }

    private fun getCafeInfo() {

        cafe = intent.intentSerializable("cafe", Place::class.java)!!
        cafeId = cafe.id

        Log.d("cafe","cafeID = $cafeId")

        val postRequest = CafeDetailRequest(cafeId, cafe.place_name)
        lifecycleScope.launch {
            controller.postCafe(postRequest)
            val cafeData = controller.getCafeDetail(cafe.id)

            setBasicInfo(cafe)

            if (cafeData != null) {
                Log.d("Detail", cafeData.toString())
                if(cafeData.myScore==0) isFirst = true
                isFav = cafeData.favorite
                setDetailInfoLayout(cafeData)
                setCommentsLayout(cafeData.comments, cafeData.commentsCount)
                //Todo:코멘트, 즐찾
            } else {
                Toast.makeText(applicationContext, "카페 정보 불러오기 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setCommentsLayout(comments: List<Comment>, commentsCount: Int) {
        if (commentsCount > 0) {
            binding.noCmtTextView.visibility = View.GONE
        }


        if (commentsCount > 3) {
            binding.commentMoreBtn.visibility = View.VISIBLE
            binding.commentMoreBtn.setOnClickListener {
                //todo:댓글 더보기 기능
            }
        }

        for (i in comments.indices) {
            val cmtViews = arrayOf(binding.comment1, binding.comment2, binding.comment3)
            cmtViews[i].visibility = View.VISIBLE
            cmtViews[i].setComment(comments[i].content)
            cmtViews[i].setMyComment(comments[i].isMe)
            cmtViews[i].setNickname(comments[i].nickname)
        }
    }

    fun commentsAdded(cmts: List<Comment>?){
        if(cmts==null)
            return

        val cmtCount = binding.commentCountText.text.toString().toInt()+1
        binding.commentCountText.text = (cmtCount).toString()
        setCommentsLayout(cmts, cmtCount)
    }

    private fun makeCommentPopup() {
        val bundle = Bundle()
        bundle.putString("cafeId", cafeId)

        val writeCommentFragment = WriteCommentFragment()
        writeCommentFragment.arguments = bundle
        writeCommentFragment.show(supportFragmentManager, writeCommentFragment.tag)
    }

    private fun setBasicInfo(cafe: Place) {
        binding.apply {
            addressText.text = cafe.road_address_name
            cafeName.text = cafe.place_name
            if(cafe.phone!="")callText.text = cafe.phone
        }
    }

    private fun setDetailInfoLayout(data: CafeResponse) {
        binding.apply {
            scoreImgs.rating = data.score.toFloat()

            val tmp = "${ String.format("%.1f", data.score) } / 5.0"

            scoreText.text = tmp

            reviewCountText.text = data.reviewsCount.toString()
            commentCountText.text = data.commentsCount.toString()

            wifiText.setReviewText(data.wifi, getString(R.string.wifi))
            parkingText.setReviewText(data.parking, getString(R.string.parking))
            toiletText.setReviewText(data.toilet, getString(R.string.toilet))
            deskText.setReviewText(data.desk, getString(R.string.desk))
            powerText.setReviewText(data.power, getString(R.string.power))
            soundText.setReviewText(data.sound, getString(R.string.sound))
        }
    }

    fun refreshDetailInfo(data: ReviewResponse?) {

        binding.apply {
            data?.let {
                scoreImgs.rating = data.score.toFloat()

                val tmp = "${ String.format("%.1f", data.score) } / 5.0"
                scoreText.text = tmp
                reviewCountText.text = data.reviewsCount.toString()

                wifiText.setReviewText(data.wifi, getString(R.string.wifi))
                parkingText.setReviewText(data.parking, getString(R.string.parking))
                toiletText.setReviewText(data.toilet, getString(R.string.toilet))
                deskText.setReviewText(data.desk, getString(R.string.desk))
                powerText.setReviewText(data.power, getString(R.string.power))
                soundText.setReviewText(data.sound, getString(R.string.sound))
            }
        }

    }

    private fun TextView.setReviewText(rev: String?, type: String) {
        if (rev != null) {
            val str = "$type $rev"
            this.text = str
            this.setTextColor(getColor(R.color.darkBrown))
        } else {
            this.text = getString(R.string.plzRV)
            this.setTextColor(getColor(R.color.stroke))
        }
    }

    private fun <T : Serializable> Intent.intentSerializable(key: String, clazz: Class<T>): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.getSerializableExtra(key, clazz)
        } else {
            this.getSerializableExtra(key) as T?
        }
    }
}

