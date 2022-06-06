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
    private lateinit var detailedCities : MutableList<City>

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

        detailedCities = mutableListOf()
        allCities = mutableListOf()
        getAllCities()

        map.addMapListener(DelayedMapListener(object: MapAdapter() {
            override fun onScroll(event: ScrollEvent?): Boolean {
                findCitiesInBoundingBoxBelowZoomAndAddToMap(allCities, map.boundingBox, map.zoomLevelDouble)
                return super.onScroll(event)
            }
            override fun onZoom(event: ZoomEvent): Boolean {
                findCitiesInBoundingBoxBelowZoomAndAddToMap(allCities, map.boundingBox, map.zoomLevelDouble)
                return super.onZoom(event)
            }
        }, 200))

    }

    private fun findCitiesInBoundingBoxBelowZoomAndAddToMap(cities: MutableList<City>, bb: BoundingBox, zoom: Double) {
        val foundCities: MutableList<City> = mutableListOf()
        for (city in cities) {
            if (bb.contains(city.getPoint()) && zoom >= city.getZoomLevel() && city.getAvailableBikeCount() > 0) {
                foundCities.add(city)
            }
        }
        foundCities.removeAll(detailedCities)
        for (city in foundCities) {
                //TODO: Handle failing requests
                map.overlays.remove(city.getMarker())
                addPlacesOfCityToMap(city.getUID())
                detailedCities.add(city)
        }
    }
    private fun addCitiesToMap(cities: MutableList<City>) {
        for (city in cities) {
                addCityToMap(city)
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

    private fun addCityToMap(city: City) {
        val pt = city.getPoint()
        if (!areCoordinatesValid(pt)) {
            //TODO: hardcoded string
            Toast.makeText(this, "Adding ${city.getName()} failed. Invalid coordinates: $pt", Toast.LENGTH_LONG).show()
            return
        }
        val newMarker = Marker(map)
        newMarker.position = pt
        newMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        newMarker.icon = ContextCompat.getDrawable(this, R.drawable.ic_location_unavailable_bike)
        newMarker.title = city.getDescription()
        city.setMarker(newMarker)
        map.overlays.add(newMarker)
    }

    private fun addPlacesOfCityToMap(id: Number) {
        nbc.getPlacesOfCity(id) {
                places ->
            for (place in places) {
                addPlaceToMap(place)
            }
            map.invalidate()
        }
    }

    private fun addPlaceToMap(place: Place) {
        val pt = place.getPoint()
        if (!areCoordinatesValid(pt)) {
            //TODO: hardcoded string
            Toast.makeText(this, "Adding ${place.getName()} failed. Invalid coordinates: $pt", Toast.LENGTH_LONG).show()
            return
        }
        val newMarker = Marker(map)
        newMarker.position = pt
        newMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        newMarker.icon = if (place.isAvailable()) {
            ContextCompat.getDrawable(this, R.drawable.ic_location_available_bike)
        } else {
            ContextCompat.getDrawable(this, R.drawable.ic_location_unavailable_bike)
        }

        newMarker.title = place.getDescription()
        map.overlays.add(newMarker)
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