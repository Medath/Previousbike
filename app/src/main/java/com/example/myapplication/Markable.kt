package com.example.myapplication

import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

interface Markable {
    fun getPoint(): GeoPoint
    fun getName(): String
    fun getDescription(): String
    fun getMarkerIconInt(): Int
    fun setMarker(marker: Marker)
    fun getMarker(): Marker
}