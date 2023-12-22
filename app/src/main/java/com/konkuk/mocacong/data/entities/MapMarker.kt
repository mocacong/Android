package com.konkuk.mocacong.data.entities

import com.naver.maps.map.overlay.Marker

data class MapMarker(
    var marker: Marker,
    val mapId: String,
    val name: String,
    val roadAddress: String,
    val phoneNumber: String,
    var type: Type = Type.NONE,
    var isFavorite: Boolean = false
) {
    fun getPlaceInfo(): BasicPlaceInfo = BasicPlaceInfo(id = mapId, name = name, roadAddress, phoneNumber)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as MapMarker
        if (mapId != other.mapId) return false
        return true
    }

    override fun hashCode(): Int {
        return mapId.hashCode()
    }

    override fun toString(): String {
        return "[장소: $name, type: $type]"
    }

    enum class Type { SOLO, GROUP, BOTH, NONE}
}