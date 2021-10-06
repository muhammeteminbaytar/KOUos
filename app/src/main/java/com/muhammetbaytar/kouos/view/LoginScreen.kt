package com.muhammetbaytar.kouos.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.muhammetbaytar.kouos.R
import com.muhammetbaytar.kouos.databinding.ActivityLoginScreenBinding
import com.muhammetbaytar.kouos.databinding.ActivityMainBinding

class LoginScreen : AppCompatActivity() {
    lateinit var binding:ActivityLoginScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_login_screen)
        binding= ActivityLoginScreenBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        supportActionBar?.hide()

        binding.txtRegister.setOnClickListener {
            val intent=Intent(this,RegisterScreen::class.java)
            startActivity(intent)
        }
    }
}