package com.example.myapplication

import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

class City (private val location: GeoPoint,
            private val name: String,
            private val zoomLevel: Double,
            private val availableBikes: Int,
            private val uid: Int) : Markable {

    private lateinit var marker: Marker

    override fun getPoint(): GeoPoint { return location }
    override fun getName(): String { return name }
    override fun getDescription(): String {
        var str = name + "\n"

        //TODO: still hardcoded strings
        str += if (getAvailableBikeCount() > 0) {
            getAvailableBikeCount().toString() + " bikes available\n"
        } else {
            "no bikes available\n"
        }

        return str.trim()
    }
    override fun setMarker(marker: Marker) { this.marker = marker }
    override fun getMarker(): Marker { return marker }
    override fun getMarkerIconInt(): Int {
        return R.drawable.ic_location_unavailable_bike
    }

    fun getZoomLevel(): Double { return zoomLevel }
    fun getUID(): Int { return uid }
    fun getAvailableBikeCount(): Int { return availableBikes }
}