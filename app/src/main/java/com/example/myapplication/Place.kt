package com.example.myapplication

import org.json.JSONObject
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

class Place(private val location: GeoPoint, private val cityName: String,
            private val name: String, private val isBike: Boolean,
            private val bikes: Int, private val availableBikes: Int
) : Markable {
    companion object {
        @JvmStatic
        fun createPlaceFromJSON(obj: JSONObject, cityName: String): Place {
            return Place(
                GeoPoint(obj.getDouble("lat"), obj.getDouble(("lng"))),
                cityName,
                obj.getString("name"),
                obj.getBoolean("bike"),
                obj.getInt("bikes"),
                obj.getInt("bikes_available_to_rent")
            )
        }
    }

    private lateinit var marker: Marker

    override fun getPoint(): GeoPoint { return location }
    override fun getName(): String { return name }
    override fun getDescription(): String {
        var str = "$cityName\n"
        str += name + "\n"

        //TODO: hardcoded strings
        if (isBike()) {
            str += "Bike\n"
            str += if (getAvailableBikeCount() > 0) {
                "Available\n"
            } else {
                "Not available\n"
            }
        } else {
            str += "Station\n"
            str += if (getAvailableBikeCount() > 0) {
                getAvailableBikeCount().toString() + " bikes available\n"
            } else {
                "No bikes available\n"
            }
        }
        return str.trim()
    }
    override fun getMarkerIconInt(): Int {
        return if (isAvailable()) {
            R.drawable.ic_location_available_bike
        } else {
            R.drawable.ic_location_unavailable_bike
        }
    }
    override fun setMarker(marker: Marker) { this.marker = marker }
    override fun getMarker(): Marker { return marker }
    override fun intersects(checkBB: BoundingBox): Boolean { return checkBB.contains(getPoint()) }

    fun isAvailable(): Boolean { return getAvailableBikeCount() != 0 }
    fun isBike(): Boolean { return isBike }
    fun getBikeCount(): Int { return bikes }
    fun getAvailableBikeCount(): Int { return availableBikes }
}