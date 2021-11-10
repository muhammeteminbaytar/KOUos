package com.muhammetbaytar.kouos.widget

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.airbnb.lottie.LottieAnimationView
import com.muhammetbaytar.kouos.R
import java.util.*

class CustomLoadDialog {

    var animArray= arrayListOf(R.raw.load,R.raw.load2,R.raw.load3,R.raw.load4)
    var dialog: Dialog? =null

    fun createLoadDialog(activity: Activity){
        dialog= Dialog(activity)
        dialog?.setContentView(R.layout.laod_dialog)
        dialog?.setCancelable(false)
        Objects.requireNonNull(dialog!!.window)?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val lotti= dialog!!.findViewById<LottieAnimationView>(R.id.animationView)
        lotti.setAnimation(animArray[(0..3).random()])
        dialog?.show()
    }
    fun cancelLoadDialog(){
        dialog?.cancel()
    }
}