package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.DisplayMetrics
import android.view.Display
import androidx.core.content.ContextCompat

import org.osmdroid.config.Configuration.*
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class BikeMap : AppCompatActivity() {
    private lateinit var map : MapView
    private lateinit var nbc: NextBikeClient

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
        //val startPoint = GeoPoint(49.0, 12.0)
        //mapController.setCenter(startPoint)

        map.invalidate()

        nbc = NextBikeClient(this)

        addCountriesToMap()
        //addCityToMap(21)
    }

    private fun addCountriesToMap() {
        nbc.getCountries() { countries ->
            for (country in countries) {
                addCountryToMap(country)
            }
        }
        map.invalidate()
    }

    private fun addCountryToMap(country: Country) {
        val newMarker = Marker(map)
        newMarker.position = country.getPoint()
        newMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        newMarker.icon = if (country.registrationNeeded()) {
            ContextCompat.getDrawable(this, R.drawable.ic_location_unavailable_bike)
        } else {
            ContextCompat.getDrawable(this, R.drawable.ic_location_available_bike)
        }

        newMarker.title = country.getDescription()
        map.overlays.add(newMarker)
    }

    private fun addCityToMap(id: Number) {
        nbc.getPlacesOfCity(id) {
                places ->
            for (place in places) {
                addPlaceToMap(place)
            }
            map.invalidate()
        }
    }

    private fun addPlaceToMap(place: Place) {
        val newMarker = Marker(map)
        newMarker.position = place.getPoint()
        newMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        newMarker.icon = if (place.isAvailable()) {
            ContextCompat.getDrawable(this, R.drawable.ic_location_available_bike)
        } else {
            ContextCompat.getDrawable(this, R.drawable.ic_location_unavailable_bike)
        }

        newMarker.title = place.getDescription()
        map.overlays.add(newMarker)
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