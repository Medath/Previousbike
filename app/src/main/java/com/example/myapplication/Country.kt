package com.example.myapplication

import org.json.JSONObject
import org.osmdroid.util.GeoPoint

class Country(private val location: GeoPoint,
              private val name: String, private val registrationNeeded: Boolean,
              private val availableBikes: Int
) {
    companion object {
        @JvmStatic
        fun createCountryFromJSON(obj: JSONObject): Country {
            return Country(
                GeoPoint(obj.getDouble("lat"), obj.getDouble("lng")),
                obj.getString("name"),
                !obj.getBoolean("no_registration"),
                obj.getInt("available_bikes")
            )
        }
    }

    fun getPoint(): GeoPoint { return location }
    fun getName(): String { return name }

    fun getDescription(): String {
        var str = name + "\n"

        //TODO: still hardcoded strings
        str += if (!registrationNeeded) { "No registration needed\n" } else { "" }
        str += if (getAvailableBikeCount() > 0) {
            getAvailableBikeCount().toString() + " bikes available\n"
        } else {
            "no bikes available\n"
        }

        return str.trim()
    }

    fun registrationNeeded(): Boolean { return registrationNeeded }
    fun getAvailableBikeCount(): Int { return availableBikes }
}

