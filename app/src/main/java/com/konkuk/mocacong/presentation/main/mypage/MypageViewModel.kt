package com.konkuk.mocacong.presentation.main.mypage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.mocacong.remote.models.response.*
import com.konkuk.mocacong.remote.repositories.KakaoRepository
import com.konkuk.mocacong.remote.repositories.MypageRepository
import com.konkuk.mocacong.remote.repositories.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import javax.inject.Inject


@HiltViewModel
class MypageViewModel @Inject constructor(
    private val mypageRepository: MypageRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val kakaoRepository: KakaoRepository
) : ViewModel() {

    val favDescription = listOf("즐겨찾는 카페", "즐겨찾기 한 카페를 한 눈에 확인해보세요")
    val reviewsDescription = listOf("나의 리뷰", "카페별로 작성한 리뷰를 확인해볼까요?")
    val commentsDescription = listOf("나의 댓글", "내가 남긴 댓글을 카페 별로 모아보세요")

    val _favResponse = MutableLiveData<MyFavResponse>()
    val favResponse: LiveData<MyFavResponse> = _favResponse
    var favPage: Int = 0

    fun requestMyFavs(page: Int = favPage) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                mypageRepository.getMyFavs(page)
            }
            response.byState(
                onSuccess = {
                    if (page == 0) {
                        _favResponse.value = it
                    } else {
                        val prev = favResponse.value!!.cafes.toMutableList()
                        prev.addAll(it.cafes)
                        _favResponse.value = it.copy(cafes = prev)
                    }
                    favPage++
                }
            )
        }
    }

    val _reviewResponse = MutableLiveData<MyReviewsResponse>()
    val reviewResponse: LiveData<MyReviewsResponse> = _reviewResponse
    var reviewPage: Int = 0

    fun requestMyReviews(page: Int = reviewPage) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                mypageRepository.getMyReviews(page)
            }
            response.byState(
                onSuccess = {
                    if (page == 0) {
                        _reviewResponse.value = it
                    } else {
                        val prev = reviewResponse.value!!.cafes.toMutableList()
                        prev.addAll(it.cafes)
                        _reviewResponse.value = it.copy(cafes = prev)
                    }
                    reviewPage++
                }
            )
        }
    }


    val _commentResponse = MutableLiveData<MyCommentsResponse>()
    val commentResponse: LiveData<MyCommentsResponse> = _commentResponse
    var commentPage: Int = 0

    fun requestMyComments(page: Int = commentPage) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                mypageRepository.getMyComments(page)
            }
            response.byState(
                onSuccess = {
                    if (page == 0) {
                        _commentResponse.value = it
                    } else {
                        val prev = commentResponse.value!!.cafes.toMutableList()
                        prev.addAll(it.cafes)
                        _commentResponse.value = it.copy(cafes = prev)
                    }
                    commentPage++
                }
            )
        }
    }

    private val _selectedPlaces = MutableLiveData<Place?>()
    val selectedPlaces: LiveData<Place?> = _selectedPlaces
    fun requestSearchAddress(keyword: String, id: String) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                kakaoRepository.getMyPageSearchResult(
                    keyword = keyword
                )
            }
            response.byState(
                onSuccess = { lr ->
                    if (lr.documents.isNotEmpty()) {
                        _selectedPlaces.value = lr.documents.filter {
                            it.id == id
                        }[0]
                    } else {
                        _selectedPlaces.value = null
                    }
                }
            )
        }
    }

    private val _myProfile = MutableLiveData<ProfileResponse>()
    val myProfile: LiveData<ProfileResponse> = _myProfile

    suspend fun getMyProfile(): ProfileResponse? {
        val nickname = withContext(Dispatchers.IO) {
            userPreferencesRepository.getMemberNickname()
        }
        val imgUrl = withContext(Dispatchers.IO) {
            userPreferencesRepository.getMemberImage()
        }
        return if (nickname.isNullOrBlank()) {
            requestMyProfile()
            null
        } else {
            val pr = ProfileResponse(nickname, imgUrl, "")
            _myProfile.value = pr
            return pr
        }
    }

    fun requestMyProfile() {
        Log.d("Profile", "requestMyProfile")
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                mypageRepository.getMyProfile()
            }
            response.byState(
                onSuccess = {
                    _myProfile.value = it
                    Log.d("Profile", "requestMyProfile 성공. $it")
                    viewModelScope.launch {
                        userPreferencesRepository.saveMemberNickname(it.nickname)
                        userPreferencesRepository.saveMemberImage(it.imgUrl)
                    }
                }
            )
        }
    }

    fun putMyProfileImg(part: MultipartBody.Part) {
        Log.d("Profile", "putMyProfileImg 들어옴")
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                mypageRepository.putMyProfileImg(part)
            }
            response.byState(onSuccess = {
                Log.d("Profile", "putMyProfileImg 성공")
                requestMyProfile()
            })
        }
    }

}