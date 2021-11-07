package com.muhammetbaytar.kouos.catacories

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.muhammetbaytar.kouos.databinding.ActivityProfilBinding
import com.muhammetbaytar.kouos.view.LoginScreen

class ProfilAct : AppCompatActivity() {
    lateinit var binding: ActivityProfilBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()
        getProfilData()
        getBasvuruData()
        clickControl()
    }

    fun getProfilData() {
        val db = FirebaseFirestore.getInstance()

        db.collection("Users").whereEqualTo("userId", FirebaseAuth.getInstance().currentUser?.uid)
            .addSnapshotListener { value, error ->
                if (error == null) {
                    if (value != null) {
                        var name = value.documents[0].get("userAdSoyad")
                        var no = value.documents[0].get("userOgrenciNo")
                        var bolum = value.documents[0].get("userDep")
                        var fak = value.documents[0].get("userFak")
                        binding.txtName.text = name.toString()
                        binding.txtNo.setText(no.toString())
                        binding.txtBolumu.setText(bolum.toString())
                        binding.txtFakulte.setText(fak.toString())
                    }
                }
            }
    }

    fun getBasvuruData() {
        val db = FirebaseFirestore.getInstance()

        db.collection("Basvurular")
            .whereEqualTo("userId", FirebaseAuth.getInstance().currentUser?.uid)
            .addSnapshotListener { value, error ->
                if (value != null) {
                    for (document in value.documents) {
                        val basvuru = "- "+valueConverter(document.get("basvuruTuru").toString())
                        val durum = document.get("basvuruDurumu").toString()
                        if (durum == "0") {
                            binding.txtBasvuru.setText(binding.txtBasvuru.text.toString() + basvuru+"\n")
                        } else {
                            if (durum == "1") {
                                binding.txtBassonuc.setText(binding.txtBassonuc.text.toString() + basvuru + " : Olumlu\n")
                            } else {
                                binding.txtBassonuc.setText(binding.txtBassonuc.text.toString() + basvuru + " : Olumsuz\n")
                            }
                        }
                    }
                }
            }
    }
    fun valueConverter(value:String):String{
        if (value=="dikey"){
            return "DGS Başvurusu"
        }
        else if (value=="yatay"){
            return "Yatay Geçiş Başvurusu"
        }
        else if (value=="intibak"){
            return "İntibak Başvurusu"
        }
        else if (value=="cap"){
            return "Çap Başvurusu"
        }else{
            return "Yaz Okulu Başvurusu"
        }
    }
    fun clickControl(){
        binding.btnCikis.setOnClickListener {
            var auth: FirebaseAuth = Firebase.auth
            auth.signOut()
            var intent= Intent(this, LoginScreen::class.java)
            startActivity(intent)
            finish()
        }

    }
}