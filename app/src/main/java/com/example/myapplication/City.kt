package com.example.myapplication

import android.content.Context
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

class City (private val location: GeoPoint,
            private val BB: BoundingBox,
            private val name: String,
            private val zoomLevel: Double,
            private val availableBikes: Int,
            private val uid: Int) : Markable {

    private lateinit var marker: Marker

    override fun getPoint(): GeoPoint { return location }
    override fun intersects(checkBB: BoundingBox): Boolean {
        return (
                BB.actualNorth >= checkBB.actualSouth
            &&  BB.lonWest <= checkBB.lonEast
            &&  BB.lonEast >= checkBB.lonWest
            &&  BB.actualSouth <= checkBB.actualNorth
        )
    }

    override fun getName(): String { return name }
    override fun getDescription(c: Context): String {
        var str = name + "\n"

        str += if (getAvailableBikeCount() > 0) {
            getAvailableBikeCount().toString() + " " + c.getString(R.string.bikes_available) + "\n"
        } else {
            c.getString(R.string.no_bikes_available) + "\n"
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