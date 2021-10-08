package com.muhammetbaytar.kouos.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.muhammetbaytar.kouos.R
import com.muhammetbaytar.kouos.databinding.ActivityCatacoriesScreenBinding
import com.muhammetbaytar.kouos.databinding.ActivityRegisterScreenBinding

class CatacoriesScreen : AppCompatActivity() {
    lateinit var binding: ActivityCatacoriesScreenBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_catacories_screen)
        binding = ActivityCatacoriesScreenBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        supportActionBar?.hide()

        binding.button.setOnClickListener {
            auth= Firebase.auth
            auth.signOut()
            var intent= Intent(this,LoginScreen::class.java)
            startActivity(intent)
            finish()
        }
    }
}