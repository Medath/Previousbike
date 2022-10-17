package com.example.myapplication


import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var phoneTextField: EditText
    private lateinit var pinTextField: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen)

        phoneTextField = findViewById(R.id.editTextPhone)
        pinTextField = findViewById(R.id.editTextPIN)
        val buttonSignIn = findViewById<Button>(R.id.SignInButton)

        val tw = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                maybeEnableButton(buttonSignIn)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        }

        phoneTextField.addTextChangedListener(tw)
        pinTextField.addTextChangedListener(tw)
        buttonSignIn.setOnClickListener {
            NextBikeClient.logIn(this, pinTextField.text.toString(), phoneTextField.text.toString()) {
                val intent = Intent(this, BikeRentActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun maybeEnableButton(btn: Button) {
        btn.isEnabled = pinTextField.length() == 6
                && PhoneNumberUtils.isGlobalPhoneNumber(phoneTextField.text.toString().replace(" ", ""))
    }
}