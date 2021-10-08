package com.muhammetbaytar.kouos.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.muhammetbaytar.kouos.R
import com.muhammetbaytar.kouos.databinding.ActivityRegisterScreenBinding

class RegisterScreen : AppCompatActivity() {
    lateinit var binding: ActivityRegisterScreenBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_register_screen)
        binding = ActivityRegisterScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()

        auth = Firebase.auth
        registerBtnControl()
    }

    fun registerBtnControl() {
        // kayıt ol butonu fonk.
        binding.btnRegister.setOnClickListener {
            var email = binding.txtEmail.text.toString()
            var pass = binding.txtSifre.text.toString()
            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this,"Zorunlu alanlar boş bırakılamaz !",Toast.LENGTH_LONG).show()
            }else{
                auth.createUserWithEmailAndPassword(email,pass).addOnSuccessListener {
                    Toast.makeText(this,"Kayıt Başarılı",Toast.LENGTH_LONG).show()
                    val intent=Intent(this,LoginScreen::class.java)
                    startActivity(intent)
                    finish()

                }.addOnFailureListener{
                    //kayıt hatası olunca çalışır.
                    Toast.makeText(this,it.localizedMessage.toString(),Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}