package com.muhammetbaytar.kouos.catacories

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.FontFactory
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfWriter
import com.muhammetbaytar.kouos.databinding.ActivityBasvuruIntibakBinding
import com.muhammetbaytar.kouos.view.FileUploadAct
import java.io.FileOutputStream
import java.util.*

class BasvuruIntibak : AppCompatActivity() {
    lateinit var binding: ActivityBasvuruIntibakBinding

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
        //setContentView(R.layout.activity_basvuru_intibak)
        binding= ActivityBasvuruIntibakBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        this.title="Ders İntibakı Başvurusu"
        clickControl()
        getFireStoreData()
    }
    fun clickControl(){
        binding.buttonCrtPdf.setOnClickListener {
            createAlertDialog()
        }
    }

    fun createAlertDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Bilgilendirme")
        builder.setCancelable(false)
        builder.setMessage("Başvuru tamamlanması için indirilen belgeyi imzalayarak sistemem geri yükleyin.")
        builder.setPositiveButton("Tamam", DialogInterface.OnClickListener { dialog, which ->
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
            val intent= Intent(this, FileUploadAct::class.java)
            intent.putExtra("typeData","intibak")
            startActivity(intent)
        })
        builder.show()

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
                    "$fakulte Dekanligina\n"

            val ikinciGiris="\n\nAdi ve Soyadi : $ogrenciAd \n" +
                    "Ögrenci No : $ogrenciNo \n" +
                    "Bölümü : $bolum \n"  +
                    "Cep Telefon No : $ogrenciTelNo \n"+
                    "E-posta Adresi : ${FirebaseAuth.getInstance().currentUser?.email.toString()} \n" +
                    "Adresi : $ogrenciAdres \n" +
                    "\n" +
                    "Daha önce ${(binding.txtUni.text.toString())}  ${(binding.txtFak.text.toString())} Fakultesi ${(binding.txtBolum.text.toString())} Bolumunde aldigim ve \n" +
                    "asagida belirttigim ders / derslerden muaf olmak istiyorum.Gereginin yapilmasini arz ederim.\n\n" +
                    "Daha Once Aldigim Ders: ${(binding.txtDers3.text.toString())}     ${(binding.txtAkts3.text.toString())} AKTS\n" +
                    "Muaf Olmak Istedigim Ders: ${(binding.txtDers4.text.toString())}     ${(binding.txtAkts.text.toString())} AKTS\n\n" +
                    "Eklenecek Belge/Belgeler:\n" +
                    "1- Transkript Belgesi (Onayli)\n" +
                    "2- Onayli Ders Içerikleri"








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