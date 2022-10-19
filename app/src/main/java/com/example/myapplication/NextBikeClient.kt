package com.example.myapplication

import android.content.Context
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.toolbox.*
import org.json.JSONException
import org.json.JSONObject
import java.io.File

object NextBikeClient {

    private const val listCitiesUrl = "https://api.nextbike.net/maps/nextbike-official.json?list_cities=1"
    private const val liveUrl = "https://api.nextbike.net/maps/nextbike-live.json?city="
    private const val loginUrl = "https://api.nextbike.net/api/login.json"
    private const val apiKeyUrl = "https://webview.nextbike.net/getAPIKey.json"

    private val queue: RequestQueue = RequestQueue(NoCache(), BasicNetwork(HurlStack()))

    private var apiKey = ""
    private lateinit var user: Account

    class Account (private val loginToken: String, private val balance: Int, private val currency: String, private val fullName: String) {

        companion object {
            @JvmStatic
            fun createAccountFromJSON(json: JSONObject): Account {
                val userJson = json.getJSONObject("user")
                return Account(
                    userJson.getString("loginkey"),
                    userJson.getInt("credits"),
                    userJson.getString("currency"),
                    userJson.getString("screen_name")
                )
            }
        }

        fun getLoginToken(): String {
            return loginToken
        }
        fun getBalance(): Int {
            return balance
        }
        fun getCurrency(): String {
            return currency
        }
        fun getFullName(): String {
            return fullName
        }

        override fun toString(): String {
            return "Token: ${getLoginToken()}\n" +
                    "Balance: ${getBalance().toFloat()/100} ${getCurrency()}\n" +
                    "Name: ${getFullName()}"
        }
    }

    init {
        queue.start()
    }

    fun isLoggedIn(context: Context): Boolean {
        if (this::user.isInitialized) {
            return true
        }

        val file = File(context.filesDir, "sessiondata.json")
        if (file.exists()) {
            try {
                user = Account.createAccountFromJSON(JSONObject(file.readText()))
                return true
            } catch(e: JSONException) {
                //File contains invalid JSON, just ignore it because it will be overwritten on a successful login anyway
            } catch(e: Exception) {
                Toast.makeText(context, "Something unexpected happened while reading session data:\n${e.message}", Toast.LENGTH_LONG).show()
            }
        }
        return false
    }

    fun logIn(context: Context, PIN: String, phoneNumber: String, callback: () -> Unit) {
        if (isLoggedIn(context)) {
            Toast.makeText(context, "Already logged in...", Toast.LENGTH_SHORT).show()
            return
        }

        obtainApiKeyIfNecessary(context) {
            apiKey ->
                makePostJSONRequest(context, loginUrl, mutableMapOf("apikey" to apiKey, "mobile" to phoneNumber, "pin" to PIN, "show_errors" to "1")) {
                    response ->
                        user = Account.createAccountFromJSON(response)
                        try {
                            File(context.filesDir, "sessiondata.json")
                                .writeText(response.toString(4))
                        } catch(e: Exception) {
                            Toast.makeText(context, "Storing session data failed: ${e.javaClass.canonicalName}\n${e.message}", Toast.LENGTH_SHORT).show()
                        }
                        callback.invoke()
                }
        }
    }

    fun getUser(): Account {
        return user
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