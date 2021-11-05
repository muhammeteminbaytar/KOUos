package com.muhammetbaytar.kouos.view

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.DatePicker
import android.widget.Toast
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.muhammetbaytar.kouos.R
import com.muhammetbaytar.kouos.databinding.ActivityRegisterScreenBinding
import java.util.*
import kotlin.collections.ArrayList

class RegisterScreen : AppCompatActivity() {
    lateinit var binding: ActivityRegisterScreenBinding
    private lateinit var auth: FirebaseAuth
    val db = FirebaseFirestore.getInstance()
    var depArray = ArrayList<String>()
    lateinit var pImgUri:Uri

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
        facClickControl()
        dateClickOpen()
        clickImageBtn()
    }

    fun clickImageBtn(){
        binding.iwProfileImg.setOnClickListener {
            ImagePicker.with(this)
                .cropSquare() //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    1080,
                    1080
                )    //Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // gelen fotoğrafı dönderir
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK){
            binding.iwProfileImg.setImageURI(data?.data)

            var pData= data?.data
            if (pData != null) {
                pImgUri= pData
            }

        }

    }

    fun registerBtnControl() {
        // kayıt ol butonu fonk.
        binding.btnRegister.setOnClickListener {
            if (emptyControl()) {
                var email = binding.txtEmail.text.toString()
                var pass = binding.txtSifre.text.toString()
                auth.createUserWithEmailAndPassword(email, pass).addOnSuccessListener {
                    saveFirestoreUsers()
                    saveProfileImgUpload()
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
        var itemArray = ArrayList<String>()
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
                    val arrayAdapter = ArrayAdapter(this, R.layout.uni_dropdown_item, itemArray)
                    binding.uniAutoComplete.setAdapter(arrayAdapter)

                }
            }
        }
    }

    fun getFaculty(uniId: String) {
        var itemArray = ArrayList<String>()
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
                        val arrayAdapter = ArrayAdapter(this, R.layout.uni_dropdown_item, itemArray)
                        binding.facAutoComplete.setAdapter(arrayAdapter)
                    }
                }
            }
    }

    fun dateClickOpen() {
        binding.textInputLayout9.setStartIconOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val mouth = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view: DatePicker,
                                                                                  mYear: Int, mMonth: Int, mDay: Int ->
                binding.txtDogumtarihi.setText("" + mDay + "/" + mMonth + "/" + mYear)
            }, year, mouth, day)
            dpd.show()

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
                                depArray.add(document.get("DepName").toString())
                                println(document.get("DepName").toString())
                            }
                        }
                    }
                }
        }

    }

    fun facClickControl() {
        binding.uniAutoComplete.setOnItemClickListener { parent, view, position, id ->
            binding.textFacLay.isEnabled = true
            binding.textFacLay.helperText = ""
        }
        binding.facAutoComplete.setOnItemClickListener { parent, view, position, id ->
            binding.textDepLay.isEnabled = true
            binding.textDepLay.helperText = ""

            var editedArray = ArrayList<String>()

            var idVal = id.toString().toInt()

            for (i in (idVal * 5)..((idVal * 5) + 4)) {
                editedArray.add(depArray[i])
            }

            val arrayAdapter = ArrayAdapter(this, R.layout.uni_dropdown_item, editedArray)
            binding.depAutoComplete.setAdapter(arrayAdapter)
        }

        binding.facAutoComplete.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                binding.depAutoComplete.text.clear()
            }

        })
    }

    fun saveFirestoreUsers() {
        val userMap = hashMapOf<String, Any>()
        auth.currentUser?.let { userMap.put("userId", it.uid) }
        userMap.put("userAdSoyad", binding.txtIsim.text.toString())
        userMap.put("userOgrenciNo", binding.txtOgrno.text.toString())
        userMap.put("userKimlikNo", binding.txtKimlikno.text.toString())
        userMap.put("userTel", binding.txtTel.text.toString())
        userMap.put("userAdres", binding.txtAdres.text.toString())
        userMap.put("userSinif", binding.txtSinif.text.toString())
        userMap.put("userDtarih", binding.txtDogumtarihi.text.toString())
        userMap.put("userUni", binding.uniAutoComplete.text.toString())
        userMap.put("userFak", binding.facAutoComplete.text.toString())
        userMap.put("userDep", binding.depAutoComplete.text.toString())

        db.collection("Users").add(userMap).addOnCompleteListener { task ->

        }.addOnFailureListener { exception ->
            Toast.makeText(
                applicationContext,
                exception.localizedMessage.toString(),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun saveProfileImgUpload(){
        val storage=Firebase.storage
        val reference = storage.reference
        val pImageReference=reference.child("Profiles").child(auth.uid+".jpg")
        if (pImgUri!= null){
            pImageReference.putFile(pImgUri).addOnSuccessListener {

            }.addOnFailureListener{
                println(it.localizedMessage.toString())
            }
        }
    }

    fun emptyControl(): Boolean {
        val editTextArray = listOf(
            binding.txtEmail,
            binding.txtSifre,
            binding.txtIsim,
            binding.txtDogumtarihi,
            binding.txtAdres,
            binding.txtKimlikno,
            binding.txtOgrno,
            binding.txtSinif,
            binding.txtTel,
            binding.uniAutoComplete,
            binding.depAutoComplete,
            binding.facAutoComplete
        )
        if (!pImageEmptyControl()){
            Toast.makeText(this, "Lütfen Fotoğraf Seçin.", Toast.LENGTH_LONG).show()

            return false
        }
        else{
        for (text_widget in editTextArray) {
            if (text_widget.text.toString().isEmpty()) {
                Toast.makeText(this, "Tüm Alanlar Eksiksiz Doldurulmalıdır.", Toast.LENGTH_LONG)
                    .show()
                return false
            }
        }}
        return true
    }

    private fun pImageEmptyControl():Boolean{
        return binding.iwProfileImg.drawable.constantState != resources.getDrawable(R.drawable.add_img).constantState
    }
}