package com.muhammetbaytar.kouos.catacories

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.muhammetbaytar.kouos.R

class BasvuruYazOkulu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basvuru__yaz_okulu)

        this.title="Yaz Okulu Başvuru"
    }
}