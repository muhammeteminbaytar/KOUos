package com.muhammetbaytar.kouos.view


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.muhammetbaytar.kouos.databinding.ActivityLoginScreenBinding
import com.muhammetbaytar.kouos.widget.CustomLoadDialog

class LoginScreen : AppCompatActivity() {
    val loadDialog = CustomLoadDialog()
    lateinit var binding: ActivityLoginScreenBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_login_screen)
        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()
        auth = Firebase.auth
        binding.txtRegister.setOnClickListener {
            val intent = Intent(this, RegisterScreen::class.java)
            startActivity(intent)
        }

        binding.btnNewSignIn.setOnClickListener { signBtnClick() }
        currentUserControl()

        binding.txtForgotPass.setOnClickListener {
            val intent = Intent(this, ForgotPassScreen::class.java)
            startActivity(intent)
        }
    }

    fun currentUserControl() {
        // aktif kullanıcı varsa doğrudan içeri alır.
        val rememberSharedPreferences = this.getSharedPreferences(
            "com.muhammetbaytar.kouos",
            MODE_PRIVATE
        )
        var rememberVar = rememberSharedPreferences.getBoolean("remember", false)
        if (rememberVar) {
            if (auth.currentUser != null) {
                var intent = Intent(this, CatacoriesScreen::class.java)
                startActivity(intent)
                finish()
            }
        }

    }

    fun signBtnClick() {
        loadDialog.createLoadDialog(this)
        // giriş yap butonu tıklandığı zaman çalışacak fonk.
        var mail = binding.txtEmail.text.toString()
        var pass = binding.txtPass.text.toString()

        if (mail == "Admin" && pass == "Admin") {
            loadDialog.cancelLoadDialog()

            var intent = Intent(this, AdminPanelAct::class.java)
            startActivity(intent)
        } else {

            if (mail.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Email ve Şifre eksiksiz girilmelidir !", Toast.LENGTH_LONG)
                    .show()
                loadDialog.cancelLoadDialog()
            } else {
                auth.signInWithEmailAndPassword(mail, pass).addOnSuccessListener {
                    loadDialog.cancelLoadDialog()
                    saverSharedPreferences()
                    var intent = Intent(this, CatacoriesScreen::class.java)
                    startActivity(intent)
                }.addOnFailureListener {
                    loadDialog.cancelLoadDialog()
                    //giriş hatası olunca çalışır.
                    Toast.makeText(this, it.localizedMessage.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    fun saverSharedPreferences() {
        val rememberSharedPreferences = this.getSharedPreferences(
            "com.muhammetbaytar.kouos",
            MODE_PRIVATE
        )
        if (binding.checkRemember.isChecked) {
            rememberSharedPreferences.edit().putBoolean("remember", true).apply()
        } else {
            rememberSharedPreferences.edit().putBoolean("remember", false).apply()
        }
    }
}