package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private val accMan = AccountManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun launchBikeMapActivity(view: View) {
        val intent = Intent(this, BikeMap::class.java)
        startActivity(intent)
    }

    fun launchBikeRentActivity(view: View) {
        if (accMan.isLoggedIn()) {
            Toast.makeText(this, "not implemented", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "not logged in", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            accMan.logIn()
        }
    }
}