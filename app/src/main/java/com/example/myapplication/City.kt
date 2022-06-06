package com.example.myapplication

import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

class City (private val location: GeoPoint,
            private val name: String,
            private val zoomLevel: Double,
            private val availableBikes: Int,
            private val uid: Int) {

    private lateinit var marker: Marker

    fun getPoint(): GeoPoint { return location }
    fun getZoomLevel(): Double { return zoomLevel }
    fun getUID(): Int { return uid }

    fun setMarker(marker: Marker) {
        this.marker = marker
    }

    fun getMarker(): Marker { return marker }
    fun getName(): String { return name }
    fun getDescription(): String {
        var str = name + "\n"

        //TODO: still hardcoded strings
        str += if (getAvailableBikeCount() > 0) {
            getAvailableBikeCount().toString() + " bikes available\n"
        } else {
            "no bikes available\n"
        }

        return str.trim()
    }

    fun getAvailableBikeCount(): Int { return availableBikes }
}