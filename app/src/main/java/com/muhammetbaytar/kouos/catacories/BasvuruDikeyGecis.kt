package com.muhammetbaytar.kouos.catacories

import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.FontFactory
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfWriter
import com.muhammetbaytar.kouos.R
import com.muhammetbaytar.kouos.databinding.ActivityBasvuruDikeyGecisBinding
import java.io.FileOutputStream
import java.util.*

class BasvuruDikeyGecis : AppCompatActivity() {
    lateinit var binding:ActivityBasvuruDikeyGecisBinding

    private var STORAGE_CODE = 1001
    lateinit var ogrenciAd:String
    lateinit var ogrenciNo:String
    lateinit var ogrenciAdres:String
    lateinit var ogrenciTelNo:String
    lateinit var bolum:String
    lateinit var fakulte:String
    lateinit var adres:String
    lateinit var kimlikNo:String
    lateinit var dTatihi:String
    lateinit var sinif:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_basvuru_dikey_gecis)
        binding= ActivityBasvuruDikeyGecisBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        this.title="Dikey Geçiş Başvurusu"
        clickControl()
        getFireStoreData()

    }

    fun clickControl(){
        binding.btnCreatePDF.setOnClickListener {
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
    }
    fun savePdf(){
        val mDoc= Document()
        val mFileName= SimpleDateFormat("yyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())

        val mFilePath= Environment.getExternalStorageDirectory().toString()+"/"+mFileName+".pdf"

        try {

            PdfWriter.getInstance(mDoc, FileOutputStream(mFilePath))
            mDoc.open()


            var giris="T.C.\n" +
                    "KOCAELI ÜNIVERSITESI\n" +
                    "DIKEY GECIS BASVURU FORMU\n"

            val ikinciGiris="\n\nAdi ve Soyadi : $ogrenciAd \n" +
                    "Ögrenci No : $ogrenciNo \n" +
                    "Bölümü : $bolum \n"  +
                    "Cep Telefon No : $ogrenciTelNo \n"+
                    "E-posta Adresi : ${FirebaseAuth.getInstance().currentUser?.email.toString()} \n" +
                    "Adresi : $ogrenciAdres \n" +
                    "TC Kimlik No : $kimlikNo \n" +
                    "Dogum Tarihi : $dTatihi\n\n\n" +
                    "Kayitli Üniversite : Kocaeli Üniversitesi \n" +
                    "Kayitli Fakülte : $fakulte\n" +
                    "Kayitli Bölüm : $bolum\n" +
                    "Basvurdugunuz Universite: ${(binding.txtUni.text.toString())}\n" +
                    "Basvurdugunuz Fakulte : ${(binding.txtFak.text.toString())}\n" +
                    "Basvurdugunuz Bölüm : ${(binding.txtBolum.text.toString())}\n" +
                    "Sinif : $sinif\n" +
                    "Ögrenci No : $ogrenciNo\n\n" +

                    "\nBeyan ettigim bilgilerin veya belgelerin gerçege aykiri olmasi veya daha önce dikey geçiş yapmiş olmam\n" +
                    "halinde hakkimda cezai işlemlerin yürütülecegini ve kaydim yapilmiş olsa dahi silinecegini bildigimi kabul ediyorum.\n" +
                    ""



            val dorduncuGiris="\n\n Adayin Adi Soyadi : $ogrenciAd \n " +
                    "IMZASI "


            mDoc.addAuthor("MyTeam")

            val paragraph= Paragraph(giris,
                FontFactory.getFont(FontFactory.TIMES_BOLD, BaseFont.CP1252, BaseFont.EMBEDDED))
            val paragraph2= Paragraph(ikinciGiris,
                FontFactory.getFont(FontFactory.TIMES, BaseFont.CP1252, BaseFont.EMBEDDED))
            val paragraph4= Paragraph(dorduncuGiris,
                FontFactory.getFont(FontFactory.TIMES, BaseFont.CP1252, BaseFont.EMBEDDED))
            paragraph.alignment= Element.ALIGN_CENTER
            paragraph2.alignment= Element.ALIGN_LEFT
            paragraph4.alignment= Element.ALIGN_RIGHT
            mDoc.add(paragraph)
            mDoc.add(paragraph2)
            mDoc.add(paragraph4)
            mDoc.close()
            Toast.makeText(this, "$mFileName.pdf içine oluşturuldu $mFilePath", Toast.LENGTH_SHORT).show()


        }catch (e: Exception){
            Toast.makeText(this, "" + e.toString(), Toast.LENGTH_SHORT).show()
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
                            //println(document.get("userAdSoyad").toString())
                            ogrenciAd=document.get("userAdSoyad").toString()
                            ogrenciNo=document.get("userOgrenciNo").toString()
                            ogrenciAdres=document.get("userAdres").toString()
                            ogrenciTelNo=document.get("userTel").toString()
                            bolum=document.get("userDep").toString()
                            fakulte=document.get("userFak").toString()
                            adres=document.get("userAdres").toString()
                            kimlikNo=document.get("userKimlikNo").toString()
                            dTatihi=document.get("userDtarih").toString()
                            sinif=document.get("userSinif").toString()
                        }
                    }
                }
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