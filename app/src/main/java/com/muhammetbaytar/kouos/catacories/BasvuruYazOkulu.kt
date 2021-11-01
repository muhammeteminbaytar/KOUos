package com.muhammetbaytar.kouos.catacories

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.muhammetbaytar.kouos.R

class BasvuruYazOkulu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basvuru__yaz_okulu)

        this.title = "Yaz Okulu Başvuru"
        getFireStoreData()
    }

    fun getFireStoreData() {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        db.collection("Users").whereEqualTo("userId",auth.uid).addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(this, "Something went wrong !", Toast.LENGTH_LONG).show()
            } else {
                if (value != null) {
                    for (document in value.documents){
                        println(document.get("userAdSoyad").toString())
                    }
                }
            }
        }
    }
}