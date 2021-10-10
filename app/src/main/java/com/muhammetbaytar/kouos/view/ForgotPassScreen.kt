package com.muhammetbaytar.kouos.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.muhammetbaytar.kouos.R
import com.muhammetbaytar.kouos.databinding.ActivityForgotPassScreenBinding
import com.muhammetbaytar.kouos.databinding.ActivityLoginScreenBinding

class ForgotPassScreen : AppCompatActivity() {
    lateinit var binding: ActivityForgotPassScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_pass_screen)
        supportActionBar?.hide()
        binding = ActivityForgotPassScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        clickControl()

    }

    fun clickControl() {
        binding.btnForgotSumbit.setOnClickListener {
            val email: String = binding.txtForgotEmail.text.toString().trim { it <= ' ' }
            if (email.isEmpty()) {
                Toast.makeText(this, "Lütfen mail adresinizi girin !", Toast.LENGTH_LONG).show()

            }else{
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener{
                    task->
                    if (task.isSuccessful){
                        Toast.makeText(this, "Şifre sıfırlama maili gönderimi başarılı.", Toast.LENGTH_LONG).show()
                        finish()
                    }else{
                        Toast.makeText(this, task.exception?.message.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}