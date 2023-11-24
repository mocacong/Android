package com.konkuk.mocacong.data.entities

data class Review(
    val category: String,
    val levelStrings : List<String>
        ){
    var selectedLevelString: String? = null

}