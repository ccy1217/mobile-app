package com.example.coursework

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class AdapterClass(private val dataList:ArrayList<DataClass>):
    RecyclerView.Adapter<AdapterClass.ViewHolderClass>(){

    class ViewHolderClass(itemView: View):RecyclerView.ViewHolder(itemView){
        val rvImage: ImageView = itemView.findViewById(R.id.animals)
        val rvTitle: TextView = itemView.findViewById(R.id.animalsString)
        val rvScore: TextView = itemView.findViewById(R.id.animalScore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent,
            false)
        return ViewHolderClass(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        holder.rvImage.setImageResource(currentItem.dataImage)
        holder.rvTitle.text = currentItem.dataTitle
        holder.rvScore.text = "Score: ${currentItem.dataScore}"

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

}

//hi