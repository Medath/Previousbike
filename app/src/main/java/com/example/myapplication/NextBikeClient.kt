package com.example.myapplication

import android.content.Context
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.toolbox.*
import org.json.JSONObject

object NextBikeClient {

    private const val listCitiesUrl = "https://api.nextbike.net/maps/nextbike-official.json?list_cities=1"
    private const val liveUrl = "https://api.nextbike.net/maps/nextbike-live.json?city="
    private const val loginUrl = "https://api.nextbike.net/api/login.json"
    private const val apiKeyUrl = "https://webview.nextbike.net/getAPIKey.json"

    private val queue: RequestQueue = RequestQueue(NoCache(), BasicNetwork(HurlStack()))

    private var loggedIn = false
    private var apiKey = ""
    private lateinit var user: Account

    class Account (private val loginToken: String) {

        fun getLoginToken(): String {
            return loginToken
        }
        fun getBalance(): Int {
            return -1
        }
    }

    init {
        queue.start()

    }

    fun isLoggedIn(): Boolean {
        return loggedIn
    }

    fun logIn(context: Context, PIN: String, phoneNumber: String) {
        if (isLoggedIn()) {
            Toast.makeText(context, "Already logged in...", Toast.LENGTH_SHORT).show()
            return
        }

        obtainApiKeyIfNecessary(context) {
            apiKey ->
            makePostJSONRequest(context, loginUrl, mutableMapOf("apikey" to apiKey, "mobile" to phoneNumber, "pin" to PIN, "show_errors" to "1")) {
                    response ->
                Toast.makeText(context, "Logged in probably successfully...\n${response.toString(1)}", Toast.LENGTH_LONG).show()
            }
        }

        //loggedIn = true
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

    private fun obtainApiKeyIfNecessary(context: Context, callback: (key: String) -> Unit) {
        if (apiKey.isEmpty()) {
            makeJSONRequest(context, apiKeyUrl) {
                response ->
                apiKey = response.getString("apiKey")
                callback.invoke(apiKey)
            }
        } else {
            callback.invoke(apiKey)
        }
    }

    private fun makePostJSONRequest(context: Context, url: String, params: MutableMap<String, String>, callback: (response: JSONObject) -> Unit) {
        val rq = object : StringRequest(Method.POST, url,
            {
                response -> callback.invoke(JSONObject(response))
            },
            {
                msg ->
                Toast.makeText(context, "post failed:\n$msg", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getBodyContentType(): String {
                return "application/x-www-form-urlencoded; charset=UTF-8"
            }
            override fun getParams(): MutableMap<String, String> {
                return params
            }
            override fun getHeaders(): MutableMap<String, String> {
                return mutableMapOf("Accept" to "application/json")
            }
        }
        this.queue.add(rq)
    }

    private fun makeJSONRequest(context: Context, url: String, callback: (response: JSONObject) -> Unit) {
        val rq = JsonObjectRequest(url,
            {
                response -> callback.invoke(response)
            },
            {
                msg ->
                Toast.makeText(context, "request to $url failed:\n$msg", Toast.LENGTH_LONG).show()
            }
        )
        this.queue.add(rq)
    }
}