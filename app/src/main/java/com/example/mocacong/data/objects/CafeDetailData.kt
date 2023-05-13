package com.example.mocacong.data.objects

object CafeDetailData {
    val keys: Array<String> =
        arrayOf("wifi", "parking", "toilet", "desk", "power", "sound")
    val labels : HashMap<String, String> = HashMap()
    val levels : HashMap<String, Array<String>> = HashMap()

    init {
        labels["wifi"] = "와이파이는"
        labels["parking"] = "주차장은"
        labels["toilet"] = "화장실은"
        labels["desk"] = "책상은"
        labels["power"] = "콘센트는"
        labels["sound"] = "분위기는"

        levels["wifi"] = arrayOf("빵빵해요", "적당해요", "느려요")
        levels["parking"] = arrayOf("여유로워요", "협소해요", "없어요")
        levels["toilet"] = arrayOf("깨끗해요", "평범해요", "불편해요")
        levels["desk"] = arrayOf("편해요", "보통이에요", "불편해요")
        levels["power"] = arrayOf("충분해요", "적당해요", "없어요")
        levels["sound"] = arrayOf("북적북적해요", "적당해요", "조용해요")
    }


}