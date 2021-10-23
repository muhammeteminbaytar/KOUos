package com.muhammetbaytar.kouos.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.muhammetbaytar.kouos.R
import com.muhammetbaytar.kouos.databinding.ActivityRegisterScreenBinding

class RegisterScreen : AppCompatActivity() {
    lateinit var binding: ActivityRegisterScreenBinding
    private lateinit var auth: FirebaseAuth
    val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_register_screen)
        binding = ActivityRegisterScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()

        auth = Firebase.auth
        getFireBaseDataOnce()
        registerBtnControl()
    }

    fun registerBtnControl() {
        // kayıt ol butonu fonk.
        binding.btnRegister.setOnClickListener {
            var email = binding.txtEmail.text.toString()
            var pass = binding.txtSifre.text.toString()
            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Zorunlu alanlar boş bırakılamaz !", Toast.LENGTH_LONG).show()
            } else {
                auth.createUserWithEmailAndPassword(email, pass).addOnSuccessListener {
                    Toast.makeText(this, "Kayıt Başarılı", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, LoginScreen::class.java)
                    startActivity(intent)
                    finish()

                }.addOnFailureListener {
                    //kayıt hatası olunca çalışır.
                    Toast.makeText(this, it.localizedMessage.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun getFireBaseDataOnce() {
        var itemArray=ArrayList<String>()
        db.collection("University").addSnapshotListener { value, error ->
            if (error != null) {
                println(error.toString())
            } else {
                if (value != null) {
                    for (document in value) {
                        println(document.get("UniversityName").toString() + "\n")
                        getFaculty(document.id)
                        itemArray.add(document.get("UniversityName").toString())

                    }
                    val arrayAdapter=ArrayAdapter(this, R.layout.uni_dropdown_item,itemArray)
                    binding.uniAutoComplete.setAdapter(arrayAdapter)

                }
            }
        }
    }

    fun getFaculty(uniId: String) {
        var itemArray=ArrayList<String>()
        db.collection("University/${uniId}/Faculty")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    println(error.toString())
                } else {
                    val facultyIdArray = ArrayList<String>()
                    if (value != null) {
                        for (document in value) {
                            println(document.get("FacultyName").toString() + "\n")
                            facultyIdArray.add(document.id)
                            itemArray.add(document.get("FacultyName").toString())
                        }
                        getDepartment(uniId, facultyIdArray)
                        val arrayAdapter=ArrayAdapter(this, R.layout.uni_dropdown_item,itemArray)
                        binding.facAutoComplete.setAdapter(arrayAdapter)
                    }
                }
            }
    }

    fun getDepartment(uniId: String, facIds: ArrayList<String>) {
        for (facId in facIds) {
            db.collection("University/${uniId}/Faculty/${facId}/Department")
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        println(error.toString())
                    } else {
                        if (value != null) {
                            println("-------------------------------------")

                            for (document in value) {
                                println(document.get("DepName").toString())
                            }
                        }
                    }
                }
        }
    }

}