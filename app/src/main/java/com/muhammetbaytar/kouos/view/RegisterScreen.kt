package com.muhammetbaytar.kouos.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.muhammetbaytar.kouos.R

class RegisterScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_screen)
        supportActionBar?.hide()


    }
}