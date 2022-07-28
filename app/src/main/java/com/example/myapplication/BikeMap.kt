package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.core.content.ContextCompat

import org.osmdroid.config.Configuration.*
import org.osmdroid.events.*
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class BikeMap : AppCompatActivity() {
    private lateinit var map : MapView
    private lateinit var nbc : NextBikeClient
    private lateinit var allCities : MutableList<City>
    private lateinit var detailedCities : HashMap<City, MutableSet<Place>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        setContentView(R.layout.activity_bike_map)
        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setUseDataConnection(true)
        map.isTilesScaledToDpi = true
        map.setMultiTouchControls(true)

        val mapController = map.controller
        mapController.setZoom(2.0)
        map.invalidate()

        nbc = NextBikeClient(this)

        detailedCities = hashMapOf()
        allCities = mutableListOf()
        getAllCities()

        map.addMapListener(DelayedMapListener(object: MapAdapter() {
            override fun onScroll(event: ScrollEvent?): Boolean {
                addDetailedCitiesToMap(allCities, map.boundingBox, map.zoomLevelDouble)
                return super.onScroll(event)
            }
            override fun onZoom(event: ZoomEvent): Boolean {
                addDetailedCitiesToMap(allCities, map.boundingBox, map.zoomLevelDouble)
                return super.onZoom(event)
            }
        }, 200))

    }

    private fun shouldBeDetailed(city: City, bb: BoundingBox, zoom: Double): Boolean {
        return city.intersects(bb) && zoom >= city.getZoomLevel() && city.getAvailableBikeCount() > 0
    }

    private fun addDetailedCitiesToMap(cities: MutableList<City>, bb: BoundingBox, zoom: Double) {
        val foundCities: MutableSet<City> = mutableSetOf()

        for (city in cities) {
            if (detailedCities.containsKey(city)) {
                if (shouldBeDetailed(city, bb, zoom + 3.0)) {
                    continue
                } else {
                    removePlacesOfCityFromMap(city)
                    detailedCities.remove(city)
                }
            }
            if (shouldBeDetailed(city, bb, zoom)) {
                foundCities.add(city)
            }
        }
        foundCities.removeAll(detailedCities.keys)
        for (city in foundCities) {
                //TODO: Handle failing requests
                removeMarkableFromMap(city)
                detailedCities[city] = mutableSetOf()
                addPlacesOfCityToMap(city)
        }
    }
    private fun addCitiesToMap(cities: MutableList<City>) {
        for (city in cities) {
                addMarkableToMap(city)
            }
        map.invalidate()
    }

    private fun getAllCities() {
        nbc.getCountries { countries ->
            for (country in countries) {
                allCities.addAll(country.getCities())
            }
            addCitiesToMap(allCities)
        }
    }

    private fun addPlacesOfCityToMap(city: City) {
        nbc.getPlacesOfCity(city.getUID()) {
                places ->
            for (place in places) {
                addMarkableToMap(place)
                detailedCities[city]?.add(place)
            }
            map.invalidate()
        }
    }

    private fun removePlacesOfCityFromMap(city: City) {
        for (place in detailedCities[city]!!) {
            removeMarkableFromMap(place)
        }
        addMarkableToMap(city)
    }

    private fun addMarkableToMap(mk: Markable) {
        val pt = mk.getPoint()
        if (!areCoordinatesValid(pt)) {
            //TODO: hardcoded string
            Toast.makeText(this, "Adding ${mk.getName()} failed. Invalid coordinates: $pt", Toast.LENGTH_LONG).show()
            return
        }
        val newMarker = Marker(map)
        newMarker.position = pt
        newMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        newMarker.icon = ContextCompat.getDrawable(this, mk.getMarkerIconInt())
        newMarker.title = mk.getDescription()
        mk.setMarker(newMarker)
        map.overlays.add(newMarker)
    }

    private fun removeMarkableFromMap(mk: Markable) {
        map.overlays.remove(mk.getMarker())
    }

    private fun areCoordinatesValid(pt: GeoPoint): Boolean {
        return (pt.longitude <= 180.0 && pt.longitude >= -180.0
                && pt.latitude <= 90.0 && pt.latitude >= -90.0)
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }
}