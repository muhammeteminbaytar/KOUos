package com.muhammetbaytar.kouos.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.muhammetbaytar.kouos.R
import com.muhammetbaytar.kouos.adapters.RecylerAdapter
import com.muhammetbaytar.kouos.databinding.ActivityAdminPanelBinding

class AdminPanelAct : AppCompatActivity() {
    lateinit var binding: ActivityAdminPanelBinding
    var filtreKayitTuru = ""
    var filtreKayitDurum = 0
    var sendNameArrayList = arrayListOf<String>()
    var sendNoArrayList = arrayListOf<String>()
    var sendDocumentIdList = arrayListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.title = "Admin Panel"
        binding = ActivityAdminPanelBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        dropdownfuller()
        dropDownSelectControl()
        //getFirebaseData()
        recylerCreater()
        getFirebaseData()
    }

    fun recylerCreater() {
        binding.recycler.layoutManager = LinearLayoutManager(this)
    }

    fun dropDownSelectControl() {
        binding.basvuruturu.addTextChangedListener {
            var data = binding.basvuruturu.text.toString()
            if (data == "Yaz Okulu") filtreKayitTuru = "yazokulu"
            else if (data == "Çap") filtreKayitTuru = "cap"
            else if (data == "Dgs") filtreKayitTuru = "dikey"
            else if (data == "İntibak") filtreKayitTuru = "intibak"
            else filtreKayitTuru = "yatay"

            getFirebaseData()
        }
        binding.basvurudurum.addTextChangedListener {
            var data = binding.basvurudurum.text.toString()
            if (data == "Gelen") {
                filtreKayitDurum = 0
            }
            else if (data == "Kabul"){
                filtreKayitDurum = 1
            }
            else filtreKayitDurum = 2

            getFirebaseData()
        }

    }

    fun dropdownfuller() {
        val basvuruDurumu = arrayOf("Gelen", "Kabul", "Ret")
        val arrayAdapter = ArrayAdapter(this, R.layout.uni_dropdown_item, basvuruDurumu)
        binding.basvurudurum.setAdapter(arrayAdapter)

        val basvuruTuru = arrayOf("Yaz Okulu", "Çap", "Dgs", "İntibak", "Yatay Geçiş")
        val arrayAdapter2 = ArrayAdapter(this, R.layout.uni_dropdown_item, basvuruTuru)
        binding.basvuruturu.setAdapter(arrayAdapter2)
    }

    fun getFirebaseData() {

        sendNoArrayList.clear()
        sendNameArrayList.clear()
        sendDocumentIdList.clear()
        val db = FirebaseFirestore.getInstance()

        if (filtreKayitTuru == "") {

            db.collection("Basvurular").whereEqualTo("basvuruDurumu", filtreKayitDurum)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Toast.makeText(this, "Something went wrong !", Toast.LENGTH_LONG).show()
                    } else {
                        if (value != null) {
                            for (document in value.documents) {

                                sendDocumentIdList.add(document.id)

                                db.collection("Users")
                                    .whereEqualTo("userId", document.get("userId"))
                                    .addSnapshotListener { value, error ->
                                        if (error != null) {
                                        } else {
                                            if (value != null) {
                                                sendNameArrayList.add(value.documents[0].get("userAdSoyad") as String)
                                                sendNoArrayList.add(value.documents[0].get("userOgrenciNo") as String)
                                                binding.recycler.adapter = RecylerAdapter(
                                                    this,
                                                    sendNameArrayList,
                                                    sendNoArrayList,
                                                    sendDocumentIdList
                                                )
                                            }
                                        }
                                    }
                            }

                        }
                    }
                }
        } else {

            println(filtreKayitTuru)
            db.collection("Basvurular").whereEqualTo("basvuruTuru", filtreKayitTuru)
                .addSnapshotListener { value, error ->
                    if (error == null) {
                        if (value != null) {
                            for (document in value.documents) {
                                sendDocumentIdList.add(document.id)
                                db.collection("Users")
                                    .whereEqualTo("userId", document.get("userId"))
                                    .addSnapshotListener { value, error ->
                                        if (error == null) {
                                            if (value != null) {
                                                sendNameArrayList.add(
                                                    value.documents[0].get("userAdSoyad").toString()
                                                )
                                                sendNoArrayList.add(
                                                    value.documents[0].get("userOgrenciNo")
                                                        .toString()
                                                )
                                                binding.recycler.adapter = RecylerAdapter(
                                                    this,
                                                    sendNameArrayList,
                                                    sendNoArrayList,
                                                    sendDocumentIdList
                                                )
                                            }
                                        }
                                    }
                            }
                        }
                    }
                }
        }
    }

    companion object {

        fun editBasvuruDurum(basvuruId: String, editedData: Int) {
            val dbo = FirebaseFirestore.getInstance().collection("Basvurular").document(basvuruId)
            if (editedData == 1) {
                dbo.update("basvuruDurumu", 1)
            } else {
                dbo.update("basvuruDurumu", 2)
            }
        }
    }
}