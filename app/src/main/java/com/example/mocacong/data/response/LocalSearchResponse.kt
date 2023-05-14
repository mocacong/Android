package com.example.mocacong.data.response

import java.io.Serializable

data class LocalSearchResponse(
    val documents: List<Place>,
    val meta: Meta
)

data class Place(
    val address_name: String,
    val category_group_code: String,
    val category_group_name: String,
    val category_name: String,
    val distance: String,
    val id: String,
    val phone: String?,
    val place_name: String,
    val place_url: String,
    val road_address_name: String?,
    val x: String,
    val y: String
) : Serializable

data class Meta(
    val is_end: Boolean,
    val pageable_count: Int,
    val total_count: Int
)