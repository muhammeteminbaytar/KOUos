package com.muhammetbaytar.kouos.catacories

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.muhammetbaytar.kouos.R
import com.muhammetbaytar.kouos.databinding.ActivityProfilBinding
import com.muhammetbaytar.kouos.view.LoginScreen
import com.muhammetbaytar.kouos.widget.CustomLoadDialog

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

    fun dialogCreater(){
        val view = View.inflate(this, R.layout.custom_logout, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        view.findViewById<Button>(R.id.signout_dialog_ok_btn).setOnClickListener {
            var auth: FirebaseAuth = Firebase.auth
            auth.signOut()
            var intent= Intent(this, LoginScreen::class.java)
            startActivity(intent)
            finish()
        }

        view.findViewById<Button>(R.id.signout_dialog_cancel_btn).setOnClickListener {
            dialog.cancel()
        }

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
                            if(binding.txtBasvuru.text.toString()!="")binding.txtBasvuru.setText(binding.txtBasvuru.text.toString()+"\n")
                            binding.txtBasvuru.setText(binding.txtBasvuru.text.toString() + basvuru)
                        } else {
                            if (binding.txtBassonuc.text.toString()!="")basvuru +binding.txtBassonuc.setText(binding.txtBassonuc.text.toString() + "\n")
                            if (durum == "1") {
                                binding.txtBassonuc.setText(binding.txtBassonuc.text.toString() + basvuru + " : Olumlu")
                            } else {
                                binding.txtBassonuc.setText(binding.txtBassonuc.text.toString() + basvuru + " : Olumsuz")
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
            dialogCreater()
        }

    }
}