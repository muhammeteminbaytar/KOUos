package com.muhammetbaytar.kouos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()


        Handler(Looper.getMainLooper()).postDelayed({

            val mainIntent = Intent(this, LoginScreen::class.java)
            startActivity(mainIntent)
            finish()
        }, 4000)

    }
}