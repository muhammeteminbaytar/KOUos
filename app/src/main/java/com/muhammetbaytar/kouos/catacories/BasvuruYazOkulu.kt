package com.muhammetbaytar.kouos.catacories

import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfWriter
import com.muhammetbaytar.kouos.R
import com.muhammetbaytar.kouos.databinding.ActivityBasvuruYazOkuluBinding
import java.io.FileOutputStream
import java.util.*

class BasvuruYazOkulu : AppCompatActivity() {
    lateinit var dersEkleDialog:Dialog
    lateinit var binding: ActivityBasvuruYazOkuluBinding
    private var STORAGE_CODE = 1001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_basvuru__yaz_okulu)
        binding = ActivityBasvuruYazOkuluBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        this.title = "Yaz Okulu Başvuru"
        getFireStoreData()
        clickControl()
    }

    private fun createDersEklePopup() {
        dersEkleDialog= Dialog(this)
        dersEkleDialog.setContentView(R.layout.dersekle_popup)
        dersEkleDialog.show()
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
                            println(document.get("userAdSoyad").toString())
                        }
                    }
                }
            }
    }

    fun clickControl() {
        binding.btnCrtPdf.setOnClickListener {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    val permission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    requestPermissions(permission, STORAGE_CODE)
                } else {
                    savePdf()
                }
            }else{
                savePdf()
            }
        }

        binding.textInputLayout5.setStartIconOnClickListener {
            createDersEklePopup()
        }
    }

    fun savePdf(){
        val mDoc=Document()
        val mFileName=SimpleDateFormat("yyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())

        val mFilePath=Environment.getExternalStorageDirectory().toString()+"/"+mFileName+".pdf"

        try {

            PdfWriter.getInstance(mDoc, FileOutputStream(mFilePath))
            mDoc.open()


            var giris="T.C.\n" +
                    "KOCAELİ ÜNİVERSİTESİ\n" +
                    "TEKNOLOJİ FAKÜLTESİ\n" +
                    "BİLİŞİM SİSTEMERİ MÜHENDİSLİĞİ BÖLÜM BAŞKANLIĞINA\n"
            var ikinciGiris="QWERTYUIOPĞÜASDFGHJKLŞİZXCVBNMÖÇ"

            mDoc.addAuthor("MyTeam")
            val paragraph=Paragraph(giris,FontFactory.getFont(FontFactory.TIMES_BOLD,BaseFont.CP1252,BaseFont.EMBEDDED))
            val paragraph2=Paragraph(ikinciGiris,FontFactory.getFont(FontFactory.TIMES_BOLD,BaseFont.CP1252,BaseFont.EMBEDDED))

            paragraph.alignment=Element.ALIGN_CENTER
            paragraph2.alignment=Element.ALIGN_CENTER
            mDoc.add(paragraph)
            mDoc.add(paragraph2)
            mDoc.close()
            Toast.makeText(this, "$mFileName.pdf içine oluşturuldu $mFilePath", Toast.LENGTH_SHORT).show()


        }catch (e: Exception){
            Toast.makeText(this, "" + e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            STORAGE_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    savePdf()
                } else {
                    Toast.makeText(this, "İzin Gerekli", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}