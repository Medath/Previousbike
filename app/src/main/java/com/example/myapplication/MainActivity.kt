package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun launchBikeMapActivity(view: View) {
        val intent = Intent(this, BikeMap::class.java)
        startActivity(intent)
    }

    fun launchBikeRentActivity(view: View) {
        val intent = if (NextBikeClient.isLoggedIn(this)) {
            Intent(this, BikeRentActivity::class.java)
        } else {
            Intent(this, LoginActivity::class.java)
        }
        startActivity(intent)
    }
}