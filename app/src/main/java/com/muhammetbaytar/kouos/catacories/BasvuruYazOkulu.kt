package com.muhammetbaytar.kouos.catacories

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
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
import com.muhammetbaytar.kouos.view.FileUploadAct
import java.io.FileOutputStream
import java.util.*

class BasvuruYazOkulu : AppCompatActivity() {
    lateinit var ogrenciAd:String
    lateinit var ogrenciNo:String
    lateinit var ogrenciAdres:String
    lateinit var ogrenciTelNo:String
    lateinit var bolum:String
    lateinit var fakulte:String

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
                        }
                    }
                }
            }
    }

    fun clickControl() {
        binding.btnCrtPdf.setOnClickListener {
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
            val intent=Intent(this,FileUploadAct::class.java)
            intent.putExtra("typeData","yazokulu")
            startActivity(intent)
        })
        builder.show()

    }

    fun savePdf(){
        val mDoc=Document()
        val mFileName=SimpleDateFormat("yyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())

        val mFilePath=Environment.getExternalStorageDirectory().toString()+"/"+mFileName+".pdf"

        try {

            PdfWriter.getInstance(mDoc, FileOutputStream(mFilePath))
            mDoc.open()


            var giris="T.C.\n" +
                    "KOCAELI ÜNIVERSITESI\n" +
                    fakulte+"\n"+
                    bolum+" BÖLÜM BASKALIGINA\n"
            var ikinciGiris="\n\nUniversiteniz $fakulte $bolum Bölümü/Programi\n $ogrenciNo Numarali $ogrenciAd isimli ögrencisiyim.\n" +
                    "Kocaeli Üniversitesinde Yaz Okulu açilmayacagindan asagida tabloda belirttigim dersleri, gerekli sartlari" +
                    "\n" +
                    "(kredi, akts ve içerik) saglayan ${binding.txtUniversite.text.toString()}'nden alabilmem hususunda geregini arz ederim. Saygilarimla"

            val ucuncuGiris="\n\nAdi ve Soyadi : $ogrenciAd \n" +
                            "Ögrenci No : $ogrenciNo \n" +
                            "Bölümü : $bolum \n"  +
                            "Cep Telefon No : $ogrenciTelNo \n"+
                            "E-posta Adresi : ${FirebaseAuth.getInstance().currentUser?.email.toString()} \n" +
                            "Adresi : $ogrenciAdres \n" +
                            "Danisman Adi Soyadi : ${(binding.txtDanisman.text.toString())}\n" +
                            "Yaz Okulu Donemi : ${(binding.txtDonem.text.toString())} \n" +
                            "Yaz Okulu Baslama Bitis Tarihi : ${(binding.txtTarih.text.toString())} \n" +
                             "\n" +
                             "${binding.txtDers1.text.toString()}  - ${binding.txtAkts1.text.toString()} \n" +
                             "${binding.txtDers2.text.toString()}  - ${binding.txtAkts2.text.toString()} \n" +
                             "${binding.txtDers3.text.toString()}  - ${binding.txtAkts3.text.toString()} \n"


            val dorduncuGiris="\n IMZA "


            mDoc.addAuthor("MyTeam")

            val paragraph=Paragraph(giris,FontFactory.getFont(FontFactory.TIMES_BOLD,BaseFont.CP1252,BaseFont.EMBEDDED))
            val paragraph2=Paragraph(ikinciGiris,FontFactory.getFont(FontFactory.TIMES,BaseFont.CP1252,BaseFont.EMBEDDED))
            val paragraph3=Paragraph(ucuncuGiris,FontFactory.getFont(FontFactory.TIMES,BaseFont.CP1252,BaseFont.EMBEDDED))
            val paragraph4=Paragraph(dorduncuGiris,FontFactory.getFont(FontFactory.TIMES,BaseFont.CP1252,BaseFont.EMBEDDED))
            paragraph.alignment=Element.ALIGN_CENTER
            paragraph2.alignment=Element.ALIGN_CENTER
            paragraph3.alignment=Element.ALIGN_LEFT
            paragraph4.alignment=Element.ALIGN_RIGHT
            mDoc.add(paragraph)
            mDoc.add(paragraph2)
            mDoc.add(paragraph3)
            mDoc.add(paragraph4)
            mDoc.close()
            Toast.makeText(this, "$mFileName.pdf içine oluşturuldu $mFilePath", Toast.LENGTH_LONG).show()


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