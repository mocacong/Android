package com.example.mocacong.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mocacong.R
import com.example.mocacong.controllers.DetailController
import com.example.mocacong.data.request.CafeDetailRequest
import com.example.mocacong.data.response.CafeResponse
import com.example.mocacong.data.response.Place
import com.example.mocacong.databinding.ActivityCafeDetailBinding
import kotlinx.coroutines.launch
import java.io.Serializable

class CafeDetailActivity : AppCompatActivity() {

    lateinit var controller: DetailController

    private lateinit var binding: ActivityCafeDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCafeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        controller = DetailController()
        getCafeInfo()
    }

    private fun getCafeInfo() {
        val cafe = intent.intentSerializable("cafe", Place::class.java)!!
        val postRequest = CafeDetailRequest(cafe.id, cafe.place_name)

        lifecycleScope.launch {
            controller.postCafe(postRequest)
            val data = controller.getCafeDetail(cafe.id)

            setBasicInfo(cafe)

            if (data != null) {
                Log.d("Detail",data.toString())
                setDetailInfoLayout(data)
            } else {
                Toast.makeText(applicationContext, "카페 정보 불러오기 실패", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun setBasicInfo(cafe: Place) {
        binding.apply {
            addressText.text = cafe.road_address_name
            cafeName.text = cafe.place_name
            callText.text = cafe.phone
        }
    }

    private fun setDetailInfoLayout(data: CafeResponse) {
        binding.apply {
            scoreImgs.rating = data.score.toFloat()

            //수정필요
            val tmp = "${data.score} / 5.0"
            scoreText.text = tmp

            if(data.favorite)
                favBtn.setImageResource(R.drawable.true_fav)

            reviewCountText.text = data.reviewsCount.toString()

            wifiText.setReviewText(data.wifi, getString(R.string.wifi))
            parkingText.setReviewText(data.parking,getString(R.string.parking))
            toiletText.setReviewText(data.toilet, getString(R.string.toilet))
            deskText.setReviewText(data.desk,getString(R.string.desk))
            powerText.setReviewText(data.power,getString(R.string.power))
            soundText.setReviewText(data.sound, getString(R.string.sound))

        }
    }

    private fun TextView.setReviewText(rev : String?, type: String){
        if(rev!=null){
            val str = "$type $rev"
            this.text = str
            this.setTextColor(getColor(R.color.darkBrown))
        }else{
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

