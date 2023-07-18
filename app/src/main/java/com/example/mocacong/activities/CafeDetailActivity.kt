package com.example.mocacong.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.lifecycle.*
import com.bumptech.glide.Glide
import com.example.mocacong.R
import com.example.mocacong.data.objects.Member
import com.example.mocacong.data.objects.Utils
import com.example.mocacong.data.objects.Utils.intentSerializable
import com.example.mocacong.data.response.*
import com.example.mocacong.data.util.ApiState
import com.example.mocacong.data.util.ViewModelFactory
import com.example.mocacong.databinding.ActivityCafeDetailBinding
import com.example.mocacong.fragments.EditReviewFragment
import com.example.mocacong.fragments.WriteCommentFragment
import com.example.mocacong.repositories.CafeDetailRepository
import com.example.mocacong.ui.MessageDialog
import com.example.mocacong.viewmodels.CafeDetailViewModel
import kotlinx.coroutines.launch

class CafeDetailActivity : AppCompatActivity() {
    private val TAG = this.javaClass.simpleName

    private lateinit var binding: ActivityCafeDetailBinding
    private lateinit var cafeId: String
    private lateinit var cafe: Place
    private var isFirst: Boolean = false
    private var isFav = false
    private var isFavLoading = false

    private lateinit var viewModel: CafeDetailViewModel
    private lateinit var viewModelFactory: ViewModelFactory<CafeDetailRepository>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCafeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModelFactory = ViewModelFactory(CafeDetailRepository())
        viewModel = ViewModelProvider(this, viewModelFactory)[CafeDetailViewModel::class.java]

        getCafeInfo()
        setLayout()
    }

    private fun setLayout() {
        binding.editBtn.setOnClickListener {
            makeEditPopUp()
        }

        binding.commentMoreBtn.setOnClickListener {
            //todo:댓글 더보기 기능
        }

        binding.favBtn.setOnClickListener {
            favoriteClicked()
        }

    }

    private fun favoriteClicked() {
        if (isFavLoading)
            return
        lifecycleScope.launch {
            isFavLoading = true
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.apply {
                    requestFavoritePost(cafeId, !isFav)
                    postFavoriteFlow.collect {
                        when (it) {
                            is ApiState.Success -> {
                                val msg = if (isFav) "즐겨찾기에서 해제되었습니다." else "즐겨찾기에 등록되었습니다"
                                Utils.showToast(this@CafeDetailActivity, msg)
                                isFav = !isFav
                                binding.favBtn.isSelected = isFav
                                isFavLoading = false
                                return@collect
                            }
                            is ApiState.Error -> {
                                it.errorResponse?.let { er ->
                                    handleTokenException(er)
                                    Log.e(TAG, er.message)
                                }
                                mPostFavoriteFlow.value = ApiState.Loading()
                                isFavLoading = false
                                return@collect
                            }
                            is ApiState.Loading -> {
                            }
                        }
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

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.apply {
                    requestCafeDetailInfo(cafeId)
                    cafeDatailInfoFlow.collect {
                        when (it) {
                            is ApiState.Success -> {
                                Log.d(TAG, "Cafe Detail get 성공")
                                it.data?.let { cafeData ->
                                    if (cafeData.myScore == 0) isFirst = true
                                    isFav = cafeData.favorite
                                    binding.favBtn.isSelected = isFav
                                    setDetailInfoLayout(cafeData)
                                    setCommentsLayout(cafeData.comments, cafeData.commentsCount)
                                    setCafeImagesView(cafeData.cafeImages)
                                }
                                return@collect
                            }
                            is ApiState.Error -> {
                                it.errorResponse?.let { errorResponse ->
                                    handleTokenException(errorResponse)
                                    Log.e(TAG, errorResponse.message)
                                }
                                mCafeDetailInfosFlow.value = ApiState.Loading()
                            }
                            is ApiState.Loading -> {}
                        }
                    }
                }
            }
        }
    }

    private fun handleTokenException(errorResponse: ErrorResponse) {
        when (errorResponse.code) {
            1013 -> {
                Utils.showConfirmDialog(this@CafeDetailActivity,
                    "로그인이 필요한 서비스입니다. 로그인 페이지로 이동하시겠습니까?",
                    confirmAction = {
                        gotoSignInActivity()
                    },
                    cancelAction = {

                    }
                )
            }
            1014 or 1015 -> {
                Utils.showConfirmDialog(this@CafeDetailActivity,
                    errorResponse.message,
                    confirmAction = {
                        gotoSignInActivity()
                    },
                    cancelAction = {

                    }
                )
            }
        }
    }

    private fun gotoSignInActivity() {
        val intent = Intent(this, SignInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun setCafeImagesView(cafeImages: List<CafeImage>) {
//        val grid = binding.imagesGridLayout
//        if (cafeImages.isEmpty()) return
//        val uri = Uri.parse(cafeImages[0].imageUrl)
//        for(i in cafeImages.indices){
//        }

    }

    private fun setCommentsLayout(comments: List<Comment>, commentsCount: Int) {
        if (commentsCount > 0) {
            binding.noCmtTextView.visibility = View.GONE
        }

        if (commentsCount > 3) {
            binding.commentMoreBtn.visibility = View.VISIBLE
            binding.commentMoreBtn.setOnClickListener {
                val popup = MessageDialog("서비스 준비 중입니다")
                popup.show(supportFragmentManager, popup.tag)
            }
        }

        for (i in comments.indices) {
            val cmtViews = arrayOf(binding.comment1, binding.comment2, binding.comment3)
            cmtViews[i].visibility = View.VISIBLE
            cmtViews[i].setComment(comments[i].content)
            cmtViews[i].setMyComment(comments[i].isMe)
            comments[i].nickname?.let { cmtViews[i].setNickname(it) }
            comments[i].imgUrl?.let {
                cmtViews[i].setProfileImage(Uri.parse(it))
            }
        }
    }

    fun commentsAdded(cmts: List<Comment>?) {
        if (cmts == null)
            return
        val cmtCount = binding.commentCountText.text.toString().toInt() + 1
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
            if (cafe.phone != "") callText.text = cafe.phone
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

    fun refreshDetailInfo(data: ReviewResponse?) {
        binding.apply {
            data?.let {
                scoreImgs.rating = data.score
                val tmp = "${String.format("%.1f", data.score)} / 5.0"
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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


}


