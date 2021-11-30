package com.example.myapplication

import org.json.JSONObject
import org.osmdroid.util.GeoPoint

class Place(private val location: GeoPoint,
            private val name: String, private val isBike: Boolean,
            private val bikes: Int, private val availableBikes: Int
) {
    companion object {
        @JvmStatic
        fun createPlaceFromJSON(obj: JSONObject): Place {
            return Place(
                GeoPoint(obj.getDouble("lat"), obj.getDouble(("lng"))),
                obj.getString("name"),
                obj.getBoolean("bike"),
                obj.getInt("bikes"),
                obj.getInt("bikes_available_to_rent")
            )
        }
    }

    fun getPoint(): GeoPoint { return location }
    fun getName(): String { return name }

    fun getDescription(): String {
        var str = name + "\n"

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

    fun isAvailable(): Boolean { return getAvailableBikeCount() != 0 }
    fun isBike(): Boolean { return isBike }
    fun getBikeCount(): Int { return bikes }
    fun getAvailableBikeCount(): Int { return availableBikes }
}