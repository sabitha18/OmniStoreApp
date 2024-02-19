package com.armada.storeapp.ui.home.riva.riva_look_book.search.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.armada.storeapp.R
import com.armada.storeapp.SearchQuery
import com.armada.storeapp.data.model.response.CollectionListItemModel
import com.armada.storeapp.data.model.response.PatternsProduct
import com.armada.storeapp.databinding.ItemCategoryBinding
import com.armada.storeapp.ui.home.riva.riva_look_book.RivaLookBookActivity
import kotlinx.android.synthetic.main.item_category.view.*

class CategoryAdapter(val catList: ArrayList<SearchQuery.Item?>?, private val rivaLookBookActivity: RivaLookBookActivity, val strQuery:String) : RecyclerView.Adapter<CategoryAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemCategoryBinding.inflate(LayoutInflater.from(rivaLookBookActivity),parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val bm = catList?.get(position)

        holder.itemView.txtCategoryName.text = bm?.name
        val count = "${bm?.product_count} ${rivaLookBookActivity!!.resources.getString(R.string.count_items)}"
        holder.itemView.txtCategoryCount!!.text = count

        if (position == (catList?.size ?: 0) - 1) {
            holder.itemView.lnrDivider.visibility = View.GONE
        } else {
            holder.itemView.lnrDivider.visibility = View.VISIBLE
        }

        holder.itemView.relMainCategory.setOnClickListener {

            val arrList = ArrayList<String>()
            val arrListCollection = ArrayList<CollectionListItemModel>() //Dummy
            val arListPattrn = ArrayList<PatternsProduct>()  /// Dummy


            val model = CollectionListItemModel("", "", "", 0, bm?.name, "", "", bm?.name, bm?.name, "0",
                "", "", "", "", "", "", bm?.name, 0, "", "", 0, 0, "", arListPattrn as ArrayList<PatternsProduct?>,
                0, "", arrListCollection as ArrayList<CollectionListItemModel?>, 0, arrList, 0, 0)
            val bundle = Bundle()
            bundle.putInt("banner_height", 0)
            bundle.putSerializable("model", model)
            bundle.putBoolean("from_search_cat", true)
            bundle.putString("strHead", bm?.name)
            rivaLookBookActivity?.navController?.navigate(R.id.navigation_product_listing,bundle)

        }
    }

    override fun getItemCount(): Int {
        return catList?.size ?: 0
    }

    inner class MyViewHolder(val binding:ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)
}