package com.example.myapplication

import android.content.Context
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.NoCache
import org.json.JSONObject

object NextBikeClient {

    private const val listCitiesUrl = "https://maps.nextbike.net/maps/nextbike-official.json?list_cities=1"
    private const val liveUrl = "https://maps.nextbike.net/maps/nextbike-live.json?city="
    //private val listCitiesUrl = "https://3deep5.me/exk/list_cities.json"
    //private val liveUrl = "https://3deep5.me/exk/nextbike-live.json"
    private val queue: RequestQueue = RequestQueue(NoCache(), BasicNetwork(HurlStack()))

    private var loggedIn = false

    init {
        queue.start()
    }

    fun isLoggedIn(): Boolean {
        return loggedIn
    }

    fun logIn(context: Context, PIN: String, phoneNumber: String) {
        Toast.makeText(context, "Logging in...\npw: $PIN\npn: $phoneNumber", Toast.LENGTH_SHORT).show()
        loggedIn = true
    }

    fun getCountries(context: Context, callback: (countries: List<Country>) -> Unit) {
        val countries: MutableList<Country> = mutableListOf()

        makeJSONRequest(context, listCitiesUrl) { response ->
            val countriesJSON = response.getJSONArray("countries")

            for (i in 0 until countriesJSON.length()) {
                countries.add(Country.createCountryFromJSON(countriesJSON.getJSONObject(i)))
            }

            callback.invoke(countries)
        }
    }

    fun getPlacesOfCity(context: Context, uid: Number, callback: (places: List<Place>) -> Unit) {
        val places: MutableList<Place> = mutableListOf()

        makeJSONRequest(context,liveUrl + uid.toString()) {
            response ->
            val cities = response.getJSONArray("countries").getJSONObject(0).getJSONArray("cities")

            for (i in 0 until cities.length()) {
                val jsonCity = cities.getJSONObject(i)
                val jsonPlaces = jsonCity.getJSONArray("places")
                for (j in 0 until jsonPlaces.length()) {
                    places.add(Place.createPlaceFromJSON(jsonPlaces.getJSONObject(j), jsonCity.getString("name")))
                }
            }
            callback.invoke(places)
        }
    }

    private fun makeJSONRequest(context: Context, url: String, callback: (response: JSONObject) -> Unit) {
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