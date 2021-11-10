package com.muhammetbaytar.kouos.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.muhammetbaytar.kouos.R
import com.muhammetbaytar.kouos.view.AdminPanelAct

class RecylerAdapter(val c:Context,val basvuruNameList: MutableList<String>,val basvuruNoList : MutableList<String>,val basvuruIdList:MutableList<String>,val idlist:MutableList<String>):RecyclerView.Adapter<ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v=LayoutInflater.from(parent.context).inflate(R.layout.card_layout,parent,false)
        return ViewHolder(v,c,basvuruIdList,basvuruIdList)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.itemView.findViewById<TextView>(R.id.txt_kuladı).text=basvuruNameList[position]
            holder.itemView.findViewById<TextView>(R.id.txt_kulno).text=basvuruNoList[position]
            holder.itemView.findViewById<ImageView>(R.id.img_profile)
            gelAllImage(idlist[position],holder)
            //holder.itemView.findViewById<TextView>(R.id.txt_basıd).text=basvuruIdList[position]

    }
    fun gelAllImage(id:String,holder: ViewHolder){
        var imageRef= Firebase.storage.reference.child("Profiles/$id.jpg")
        imageRef.downloadUrl.addOnSuccessListener {
            val imageUri=it.toString()
            var imageTest=holder.itemView.findViewById<ImageView>(R.id.img_profile)
            Glide.with(c).load(imageUri).into(imageTest)
        }

    }
    override fun getItemCount(): Int {
            return basvuruNameList.size
    }

}
class ViewHolder(itemView: View,c:Context,basvuruIdList: MutableList<String>,docIdList: MutableList<String>):RecyclerView.ViewHolder(itemView){
    init {
        itemView.setOnClickListener {

        }
        itemView.findViewById<ImageView>(R.id.mMenu).setOnClickListener {
            popMenus(it,c,basvuruIdList)
        }

    }

    private fun popMenus(v:View,c: Context,basvuruIdList: MutableList<String>) {
        val position=basvuruIdList[adapterPosition]
        val popMenus=PopupMenu(c,v)
        popMenus.inflate(R.menu.show_menu)
        popMenus.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.txtonay -> {
                    AdminPanelAct.editBasvuruDurum(position,1)
                    true
                }
                R.id.txtret->{
                    AdminPanelAct.editBasvuruDurum(position,2)
                    true
                }
                else->true
            }
        }
        popMenus.show()
        val popup=PopupMenu::class.java.getDeclaredField("mPopup")
        popup.isAccessible=true
        val menu=popup.get(popMenus)
        menu.javaClass.getDeclaredMethod("setForceShowIcon",Boolean::class.java).invoke(menu,true)
    }

}