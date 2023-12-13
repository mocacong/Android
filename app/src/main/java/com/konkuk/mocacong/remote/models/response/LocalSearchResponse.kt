package com.konkuk.mocacong.remote.models.response

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
    val y: String,
    var isFavorite: Boolean = false
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Place
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

data class Meta(
    val is_end: Boolean,
    val pageable_count: Int,
    val total_count: Int
)