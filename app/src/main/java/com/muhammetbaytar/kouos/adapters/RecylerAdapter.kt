package com.muhammetbaytar.kouos.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.muhammetbaytar.kouos.R
import com.muhammetbaytar.kouos.view.AdminPanelAct

class RecylerAdapter(val c:Context,val basvuruNameList: MutableList<String>,val basvuruNoList : MutableList<String>,val basvuruIdList:MutableList<String>):RecyclerView.Adapter<ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v=LayoutInflater.from(parent.context).inflate(R.layout.card_layout,parent,false)
        return ViewHolder(v,c,basvuruIdList)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.itemView.findViewById<TextView>(R.id.txt_kuladı).text=basvuruNameList[position]
            holder.itemView.findViewById<TextView>(R.id.txt_kulno).text=basvuruNoList[position]
            holder.itemView.findViewById<TextView>(R.id.txt_basıd).text=basvuruIdList[position]



    }

    override fun getItemCount(): Int {
            return basvuruNameList.size
    }

}
class ViewHolder(itemView: View,c:Context,basvuruNameList: MutableList<String>):RecyclerView.ViewHolder(itemView){
    init {
        itemView.setOnClickListener {

        }
        itemView.findViewById<ImageView>(R.id.mMenu).setOnClickListener {
            popMenus(it,c,basvuruNameList)
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