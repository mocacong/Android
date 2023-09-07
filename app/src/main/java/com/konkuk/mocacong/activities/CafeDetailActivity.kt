package com.konkuk.mocacong.activities

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.konkuk.mocacong.R
import com.konkuk.mocacong.data.objects.Utils
import com.konkuk.mocacong.data.objects.Utils.intentSerializable
import com.konkuk.mocacong.data.response.CafeImage
import com.konkuk.mocacong.data.response.CafeResponse
import com.konkuk.mocacong.data.response.Comment
import com.konkuk.mocacong.data.response.Place
import com.konkuk.mocacong.data.util.ApiState
import com.konkuk.mocacong.data.util.TokenExceptionHandler
import com.konkuk.mocacong.data.util.ViewModelFactory
import com.konkuk.mocacong.databinding.ActivityCafeDetailBinding
import com.konkuk.mocacong.fragments.CafeCommentsFragment
import com.konkuk.mocacong.fragments.CafeImagesFragment
import com.konkuk.mocacong.fragments.EditReviewFragment
import com.konkuk.mocacong.repositories.CafeDetailRepository
import com.konkuk.mocacong.viewmodels.CafeDetailViewModel
import kotlinx.coroutines.launch

class CafeDetailActivity : AppCompatActivity() {
    private val TAG = this.javaClass.simpleName

    private lateinit var binding: ActivityCafeDetailBinding
    private lateinit var cafeId: String
    private lateinit var cafe: Place
    private var isFirst: Boolean = false
    private var isFav = false
    private var isFavLoading = false

    private lateinit var cafeViewModel: CafeDetailViewModel
    private lateinit var cafeViewModelFactory: ViewModelFactory<CafeDetailRepository>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCafeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cafeViewModelFactory = ViewModelFactory(CafeDetailRepository())
        cafeViewModel =
            ViewModelProvider(this, cafeViewModelFactory)[CafeDetailViewModel::class.java]

        getCafeInfo()
        setLayout()
    }

    private fun setLayout() {
        binding.editBtn.setOnClickListener {
            makeEditPopUp()
        }

        binding.favBtn.setOnClickListener {
            favoriteClicked()
        }

        binding.commentPlusBar.setOnClickListener {
            makeCommentPopup(true)
        }

        binding.commentBtn.setOnClickListener {
            makeCommentPopup(false)
        }

        binding.cafeImagePlusBtn.setOnClickListener {
            makeImagesPopup()
        }
    }

    private fun favoriteClicked() {
        if (isFavLoading)
            return
        lifecycleScope.launch {
            isFavLoading = true
            cafeViewModel.apply {
                requestFavoritePost(cafeId, !isFav).join()

                when (val apiState = postFavoriteFlow.value) {
                    is ApiState.Success -> {
                        val msg = if (isFav) "즐겨찾기에서 해제되었습니다." else "즐겨찾기에 등록되었습니다"
                        Utils.showToast(this@CafeDetailActivity, msg)
                        isFav = !isFav
                        binding.favBtn.isSelected = isFav
                        isFavLoading = false
                    }
                    is ApiState.Error -> {
                        apiState.errorResponse?.let { er ->
                            TokenExceptionHandler.handleTokenException(this@CafeDetailActivity, er)
                            Log.e(TAG, er.message)
                        }
                        mPostFavoriteFlow.value = ApiState.Loading()
                        isFavLoading = false
                    }
                    is ApiState.Loading -> {
                    }
                }
            }
        }
    }

    private fun makeEditPopUp() {
        val bundle = Bundle()
        bundle.putString("cafeId", cafeId)
        if (isFirst)
            bundle.putBoolean("isFirst", true)
        else
            bundle.putBoolean("isFirst", false)

        val editReviewFragment = EditReviewFragment()
        editReviewFragment.arguments = bundle
        editReviewFragment.show(supportFragmentManager, editReviewFragment.tag)
    }

    private fun getCafeInfo() {
        cafe = intent.intentSerializable("cafe", Place::class.java)!!
        cafeId = cafe.id
        setBasicInfo(cafe)
        refreshDetailInfo()
    }

    private fun setCafeImagesView(cafeImages: List<CafeImage>) {
        val imageViews = listOf(
            binding.cafeImage1,
            binding.cafeImage2,
            binding.cafeImage3,
            binding.cafeImage4,
            binding.cafeImage5
        )

        cafeImages.forEachIndexed { index, cafeImage ->
            imageViews[index]
            Glide.with(this).load(cafeImage.imageUrl).into(imageViews[index])
        }
    }

    private fun setCommentsLayout(comments: List<Comment>, commentsCount: Int) {
        if (commentsCount > 0) {
            binding.noCmtTextView.visibility = View.GONE
        }

        if (commentsCount > 3) {
            binding.commentMoreBtn.visibility = View.VISIBLE
            binding.commentMoreBtn.setOnClickListener {
                makeCommentPopup(false)
            }
        }
        val cmtViews = arrayOf(binding.comment1, binding.comment2, binding.comment3)

        for (i in comments.indices) {
            cmtViews[i].visibility = View.VISIBLE
            cmtViews[i].setComment(comments[i].content)
            cmtViews[i].setMyComment(comments[i].isMe)
            comments[i].nickname?.let { cmtViews[i].setNickname(it) }
            comments[i].imgUrl?.let {
                cmtViews[i].setProfileImage(Uri.parse(it))
            }
        }
    }

    private fun makeCommentPopup(isFocusToTextField: Boolean) {
        val bundle = Bundle()
        bundle.putString("cafeId", cafeId)
        bundle.putBoolean("isFocusToTextField", isFocusToTextField)
        val cafeCommentsFragment = CafeCommentsFragment()
        cafeCommentsFragment.arguments = bundle
        cafeCommentsFragment.show(supportFragmentManager, cafeCommentsFragment.tag)
    }

    private fun makeImagesPopup() {
        val bundle = Bundle()
        bundle.putString("cafeId", cafeId)
        val cafeImagesFragment = CafeImagesFragment()
        cafeImagesFragment.arguments = bundle
        cafeImagesFragment.show(supportFragmentManager, cafeImagesFragment.tag)
    }


    private fun setBasicInfo(cafe: Place) {
        binding.apply {
            cafeName.text = cafe.place_name
            if (cafe.phone != "") callText.text = cafe.phone
            if (cafe.road_address_name != "")
                addressText.text = cafe.road_address_name
        }
    }

    private fun setDetailInfoLayout(data: CafeResponse) {
        binding.apply {
            scoreImgs.rating = data.score.toFloat()
            val tmp = "${String.format("%.1f", data.score)} / 5.0"
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

    fun refreshDetailInfo() = lifecycleScope.launch {
        cafeViewModel.apply {
            requestCafeDetailInfo(cafeId).join()
            when (val apiState = cafeDatailInfoFlow.value) {
                is ApiState.Success -> {
                    Log.d(TAG, "Cafe Detail get 성공")
                    apiState.data?.let { cafeData ->
                        if (cafeData.myScore == 0) isFirst = true
                        isFav = cafeData.favorite
                        binding.favBtn.isSelected = isFav
                        setDetailInfoLayout(cafeData)
                        setCommentsLayout(cafeData.comments, cafeData.commentsCount)
                        setCafeImagesView(cafeData.cafeImages)
                    }
                }
                is ApiState.Error -> {
                    apiState.errorResponse?.let { errorResponse ->
                        TokenExceptionHandler.handleTokenException(
                            this@CafeDetailActivity,
                            errorResponse
                        )
                        Log.e(TAG, errorResponse.message)
                    }
                    mCafeDetailInfosFlow.value = ApiState.Loading()
                }
                is ApiState.Loading -> {}
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

    override fun onBackPressed() {
        setResult(RESULT_OK)
        finish()
    }

}
