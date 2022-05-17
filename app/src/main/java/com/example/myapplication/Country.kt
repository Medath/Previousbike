package com.example.myapplication

import org.json.JSONArray
import org.json.JSONObject
import org.osmdroid.util.GeoPoint

class Country(private val location: GeoPoint,
              private val name: String, private val registrationNeeded: Boolean,
              private val zoomLevel: Int,
              private val availableBikes: Int,
              private val citiesJSON: JSONArray
) {
    companion object {
        @JvmStatic
        fun createCountryFromJSON(obj: JSONObject): Country {
            return Country(
                GeoPoint(obj.getDouble("lat"), obj.getDouble("lng")),
                obj.getString("name"),
                !obj.getBoolean("no_registration"),
                obj.getInt("zoom"),
                obj.getInt("available_bikes"),
                obj.getJSONArray("cities")
            )
        }
    }

    fun getPoint(): GeoPoint { return location }
    fun getName(): String { return name }
    fun getZoomLevel(): Int { return zoomLevel }
    fun getCities(): MutableList<City> {
        val cities: MutableList<City> = mutableListOf()

        for (i in 0 until citiesJSON.length()) {
            val city = citiesJSON.getJSONObject(i)
            cities.add(City(
                    GeoPoint(city.getDouble("lat"), city.getDouble("lng")),
                    city.getString("name"),
                    city.getInt("zoom").toDouble(),
                    city.getInt("available_bikes"),
                    city.getInt("uid")
                )
            )
        }

        return cities
    }

    fun getDescription(): String {
        var str = name + "\n"

        //TODO: still hardcoded strings
        str += if (!registrationNeeded) { "No registration needed\n" } else { "" }
        str += if (getAvailableBikeCount() > 0) {
            getAvailableBikeCount().toString() + " bikes available\n"
        } else {
            "no bikes available\n"
        }
        str += "zoom: $zoomLevel\n"
        str += "${citiesJSON.length()} \n"

        return str.trim()
    }

    fun registrationNeeded(): Boolean { return registrationNeeded }
    private fun getAvailableBikeCount(): Int { return availableBikes }
}

