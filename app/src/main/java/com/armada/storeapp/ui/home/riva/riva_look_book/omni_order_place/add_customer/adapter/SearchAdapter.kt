package com.armada.storeapp.ui.home.riva.riva_look_book.omni_order_place.add_customer.adapter

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.armada.storeapp.R
import com.armada.storeapp.data.model.response.AddToCartResponse
import com.armada.storeapp.data.model.response.CustomerMasterData
import com.armada.storeapp.data.model.response.SkuMasterTypes
import com.armada.storeapp.databinding.ItemSpinnerBinding
import com.armada.storeapp.databinding.TestLayoutBinding
import com.armada.storeapp.ui.utils.Constants
import com.armada.storeapp.ui.utils.SwipeRevealLayout.ViewBinderHelper
import com.armada.storeapp.ui.utils.Utils
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.layout_bag_item.view.*
import java.util.*

class SearchAdapter(
    private val activity: FragmentActivity,
    var list: ArrayList<CustomerMasterData>?
) : RecyclerView.Adapter<SearchAdapter.ItemHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemHolder {
        return ItemHolder(
            ItemSpinnerBinding.inflate(
                LayoutInflater.from(activity),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(
        holder: ItemHolder,
        position: Int
    ) {

        val searchItem = list?.get(position)
        holder.binding.tvSpinnerText.text=searchItem?.customerName

    }


    override fun getItemCount(): Int {
        return if (list == null) 0 else list!!.size
    }

    class ItemHolder(val binding: ItemSpinnerBinding) :
        RecyclerView.ViewHolder(binding.root)
}