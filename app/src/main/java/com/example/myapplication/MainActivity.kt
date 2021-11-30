package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.core.content.ContextCompat

import org.osmdroid.config.Configuration.*
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MainActivity : AppCompatActivity() {
    private lateinit var map : MapView
    private lateinit var nbc: NextBikeClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        setContentView(R.layout.activity_main)
        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.isTilesScaledToDpi = true
        val mapController = map.controller
        mapController.setZoom(10.0)
        val startPoint = GeoPoint(49.0, 12.0)
        mapController.setCenter(startPoint)

        map.invalidate()

        nbc = NextBikeClient(this)

        addCityToMap(21)
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