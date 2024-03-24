package com.example.descompuestos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PlaceDetailsAdapter (private val mImageUrls: List<String?>,
                           private val layoutResId: Int) : RecyclerView.Adapter<PlaceDetailsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mImageUrls[position]?.let { holder.bindImage(it) }
    }

    override fun getItemCount(): Int {
        return mImageUrls.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val img: ImageView = itemView.findViewById(R.id.pd_image)

        fun bindImage(data: String){
            Glide.with(itemView)
                .load(data)
                .into(img)
        }
    }
}