package com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.armada.storeapp.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.lv_item_color.view.*

class ProductColorSwatchAdapter(var context: Context, var arrListColors: ArrayList<String>) :
    RecyclerView.Adapter<ProductColorSwatchAdapter.ColorHolder>() {

    override fun onBindViewHolder(holder: ColorHolder, position: Int) {
        val bm = arrListColors[position]
        Glide.with(context).asBitmap().load(bm).into(holder.itemView.imgColor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.lv_item_color, parent, false)
        return ColorHolder(view)
    }

    override fun getItemCount(): Int {
        return if (arrListColors.size > 3) 4 else arrListColors.size
    }


    class ColorHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}
