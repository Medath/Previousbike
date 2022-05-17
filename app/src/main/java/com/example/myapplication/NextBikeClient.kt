package com.example.myapplication

import android.content.Context
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class NextBikeClient(ctx: Context) {

    private val listCitiesUrl = "https://maps.nextbike.net/maps/nextbike-official.json?list_cities=1"
    private val liveUrl = "https://maps.nextbike.net/maps/nextbike-live.json?city="
    //private val listCitiesUrl = "https://3deep5.me/exk/list_cities.json"
    //private val liveUrl = "https://3deep5.me/exk/nextbike-live.json"
    private val context: Context = ctx
    private val queue: RequestQueue = Volley.newRequestQueue(ctx)
    private lateinit var listCities: JSONObject

    fun getCountries(callback: (countries: List<Country>) -> Unit) {
        val countries: MutableList<Country> = mutableListOf()

        makeJSONRequest(listCitiesUrl) { response ->
            val countriesJSON = response.getJSONArray("countries")

            for (i in 0 until countriesJSON.length()) {
                countries.add(Country.createCountryFromJSON(countriesJSON.getJSONObject(i)))
            }

            callback.invoke(countries)
        }
    }

    fun getPlacesOfCity(uid: Number, callback: (places: List<Place>) -> Unit) {
        val places: MutableList<Place> = mutableListOf()

        makeJSONRequest(liveUrl + uid.toString()) {
            response ->
            val cities = response.getJSONArray("countries").getJSONObject(0).getJSONArray("cities")

            for (i in 0 until cities.length()) {
                val jsonPlaces = cities.getJSONObject(i).getJSONArray("places")
                for (j in 0 until jsonPlaces.length()) {
                    places.add(Place.createPlaceFromJSON(jsonPlaces.getJSONObject(j)))
                }
            }
            callback.invoke(places)
        }
    }

    private fun makeJSONRequest(url: String, callback: (response: JSONObject) -> Unit) {
        Toast.makeText(context, url, Toast.LENGTH_SHORT).show()
        val rq = JsonObjectRequest(url,
            {
                response -> callback.invoke(response)
            },
            {
                Toast.makeText(context, "request to $url failed", Toast.LENGTH_LONG).show()
            })
        this.queue.add(rq)
    }
}