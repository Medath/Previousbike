package com.example.myapplication

class AccountManager {
    private var loggedIn = false

    fun isLoggedIn(): Boolean {
        return loggedIn
    }

    fun logIn() {
        loggedIn = true
    }
}