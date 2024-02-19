package com.armada.storeapp.ui.home.riva.riva_look_book.search.adapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.armada.storeapp.R
import com.armada.storeapp.data.model.response.CollectionListItemModel
import com.armada.storeapp.data.model.response.HomeDataModel
import com.armada.storeapp.data.model.response.PatternsProduct
import com.armada.storeapp.data.model.response.TimeLineModel
import com.armada.storeapp.databinding.ItemPopularSearchBinding
import com.armada.storeapp.ui.home.riva.riva_look_book.RivaLookBookActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.OmniProductDetailsActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.ProductDetailsActivity

class PopularSearchAdapter(val searchList: ArrayList<HomeDataModel.PopularSearches>, private val rivaLookBookActivity: RivaLookBookActivity) : androidx.recyclerview.widget.RecyclerView.Adapter<PopularSearchAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemPopularSearchBinding.inflate(LayoutInflater.from(rivaLookBookActivity),parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var currentItem = searchList.get(position)

        holder.binding.txtName.text = currentItem.name
        holder.binding.imgNext!!.visibility = View.VISIBLE
        // holder.itemView.txtCount!!.visibility=View.GONE

        holder.binding.imgProduct.visibility = View.GONE

        if (position == searchList.size - 1) {
            holder.binding.divider.visibility = View.GONE
        } else {
            holder.binding.divider.visibility = View.VISIBLE
        }

        holder.binding.relMain.setOnClickListener {

            if (currentItem.type.equals("C")) {

                val arrList = ArrayList<String>()
                val arrListCollection = java.util.ArrayList<CollectionListItemModel>() //Dummy
                val arListPattrn = ArrayList<PatternsProduct>()  /// Dummy
                val arrListTimeline = java.util.ArrayList<TimeLineModel>() //Dummy

                val model = CollectionListItemModel("", "", "", currentItem.type_id, currentItem.name, "", currentItem.type_id.toString(), currentItem.path, currentItem.lvl, "0",
                    "", "", "", "", "", "", currentItem.name, currentItem.type_id, currentItem.path, currentItem.lvl, 0,
                    0, "", arListPattrn as ArrayList<PatternsProduct?>, 0, "", arrListCollection as ArrayList<CollectionListItemModel?>, 0, arrList, 0, 0)
                val bundle = Bundle()
                bundle.putInt("banner_height", 0)
                bundle.putSerializable("model", model)
                bundle.putString("strHead", currentItem.name)
                rivaLookBookActivity.navController?.navigate(R.id.navigation_product_listing,bundle)

            } else if (currentItem.type.equals("P")) {

                val intent = Intent(rivaLookBookActivity, OmniProductDetailsActivity::class.java)
                intent.putExtra("id", currentItem.type_id.toString())
                intent.putExtra("cat_id", "-1")
                intent.putExtra("name", currentItem.name)
                rivaLookBookActivity!!.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    class MyViewHolder(val binding:ItemPopularSearchBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root)
}