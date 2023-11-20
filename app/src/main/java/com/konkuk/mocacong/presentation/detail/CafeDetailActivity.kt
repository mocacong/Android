package com.konkuk.mocacong.presentation.detail

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.konkuk.mocacong.databinding.ActivityCafeDetailBinding
import com.konkuk.mocacong.objects.Utils
import com.konkuk.mocacong.objects.Utils.intentSerializable
import com.konkuk.mocacong.remote.models.response.CafeImage
import com.konkuk.mocacong.remote.models.response.CafeResponse
import com.konkuk.mocacong.remote.models.response.Comment
import com.konkuk.mocacong.remote.models.response.Place
import com.konkuk.mocacong.remote.repositories.CafeDetailRepository
import com.konkuk.mocacong.util.ApiState
import com.konkuk.mocacong.util.TokenExceptionHandler
import com.konkuk.mocacong.util.ViewModelFactory
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
            binding.commentMoreBtn.visibility = View.VISIBLE
            binding.commentMoreBtn.setOnClickListener {
                makeCommentPopup(false)
            }
        }

        val cmtViews = arrayOf(binding.comment1, binding.comment2, binding.comment3)

        for (i in comments.indices) {
            cmtViews[i].visibility = View.VISIBLE
            cmtViews[i].setCommentView(comments[i])
            cmtViews[i].setPreview()
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
        binding.placeInfo = cafe
    }

    private fun setDetailInfoLayout(data: CafeResponse) {
        binding.detailInfo = data
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

    override fun onBackPressed() {
        setResult(RESULT_OK)
        finish()
    }

}
