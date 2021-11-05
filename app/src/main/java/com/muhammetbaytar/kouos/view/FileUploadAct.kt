package com.muhammetbaytar.kouos.view

import android.content.Intent
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.muhammetbaytar.kouos.databinding.ActivityFileUploadBinding
import java.util.*

class FileUploadAct : AppCompatActivity() {

    lateinit var binding: ActivityFileUploadBinding
    lateinit var typeUpload: String
    lateinit var uri: Uri
    lateinit var ogrenciAd: String
    lateinit var ogrenciNo: String
    var belgeTipi = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_file_upload)
        supportActionBar?.hide()
        typeUpload = "default"
        binding = ActivityFileUploadBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        getFireStoreData()
        createIntent()
        clickControl()
    }

    fun clickControl() {
        binding.imgBasvurubelge.setOnClickListener {
            val intent = Intent()
            intent.type = "application/pdf"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Başvuru Belgesi Seçin"), 0)
            belgeTipi = 0
        }
        binding.imgTranskript.setOnClickListener {
            val intent = Intent()
            intent.type = "application/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Transkript Belgesi Seçin"), 0)
            belgeTipi = 1
        }
        binding.btnBasvuruolustur.setOnClickListener {
            uploadFirease("")
            uploadFirease("Transkript_")

            saveBasvuruFirebase()
        }
    }

    fun viewControl() {
        println(typeUpload)
        if (typeUpload == "intibak" || typeUpload == "cap") {
            binding.imgTranskript.visibility = View.VISIBLE
            binding.txtTranskriptbelge.visibility = View.VISIBLE
        }
    }

    fun createIntent() {
        typeUpload = intent.getStringExtra("typeData").toString()
        viewControl()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                uri = data?.data!!
                if (belgeTipi == 0) {
                    binding.txtBasvurubelge.text = "Başvuru Belgesi Seçildi"
                    binding.txtBasvurubelge.setTextColor(Color.rgb(0, 100, 0))
                } else {
                    binding.txtTranskriptbelge.text = "Transkript Belgesi Seçildi"
                    binding.txtTranskriptbelge.setTextColor(Color.rgb(0, 100, 0))
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun uploadFirease(docName: String) {
        val storage = Firebase.storage
        val reference = storage.reference
        var fileName = docName + ogrenciNo + "_" + ogrenciAd + "_" + SimpleDateFormat(
            "yyyMMddHHmm",
            Locale.getDefault()
        ).format(System.currentTimeMillis())
        var pImageReference = reference.child("Basvuru").child("$fileName.pdf")
        if (docName != "") {
            pImageReference = reference.child("Transkript").child("$fileName.pdf")
        }
        if (uri != null) {
            pImageReference.putFile(uri).addOnSuccessListener {
                Toast.makeText(this, "Basarılı", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                println(it.localizedMessage.toString())
            }
        }
    }

    fun getFireStoreData() {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        db.collection("Users").whereEqualTo("userId", auth.uid)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Toast.makeText(this, "Something went wrong !", Toast.LENGTH_LONG).show()
                } else {
                    if (value != null) {
                        for (document in value.documents) {
                            ogrenciAd = document.get("userAdSoyad").toString()
                            ogrenciNo = document.get("userOgrenciNo").toString()
                        }
                    }
                }
            }
    }

    fun saveBasvuruFirebase() {
        val basvuruMap = hashMapOf<String, Any>()
        Firebase.auth.currentUser?.let { basvuruMap.put("userId", it.uid) }
        basvuruMap.put("basvuruTuru", typeUpload)
        basvuruMap.put("basvuruDurumu", 0)
        FirebaseFirestore.getInstance().collection("Basvurular").add(basvuruMap)
            .addOnCompleteListener {
                
            }.addOnFailureListener {
            Toast.makeText(
                applicationContext,
                it.localizedMessage.toString(),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}