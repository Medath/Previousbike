package com.example.myapplication

import android.content.Context
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

interface Markable {
    fun getPoint(): GeoPoint
    fun getName(): String
    fun getDescription(c: Context): String
    fun getMarkerIconInt(): Int
    fun setMarker(marker: Marker)
    fun getMarker(): Marker
    fun intersects(checkBB: BoundingBox): Boolean
}