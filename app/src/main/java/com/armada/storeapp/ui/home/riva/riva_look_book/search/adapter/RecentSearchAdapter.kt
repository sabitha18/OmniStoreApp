package com.armada.storeapp.ui.home.riva.riva_look_book.search.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.armada.storeapp.R
import com.armada.storeapp.data.local.model.RecentSearch
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.OmniProductDetailsActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.ProductDetailsActivity
import kotlinx.android.synthetic.main.item_popular_search.view.*

class RecentSearchAdapter(val recentList: ArrayList<RecentSearch>, private val context: Context?) : androidx.recyclerview.widget.RecyclerView.Adapter<RecentSearchAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_popular_search, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val bm = recentList[position]

        holder.itemView.txtName.text = bm.name
        holder.itemView.imgNext!!.visibility = View.VISIBLE

        holder.itemView.imgProduct.visibility = View.GONE

        holder.itemView.relMain.setOnClickListener {
            val intent = Intent(context, OmniProductDetailsActivity::class.java)
            intent.putExtra("id", bm.entity_id)
            intent.putExtra("cat_id", "-1")
            intent.putExtra("name", bm.name)
            intent.putExtra("image", bm.image_url)
            context!!.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return recentList.size
    }

    inner class MyViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView)
}