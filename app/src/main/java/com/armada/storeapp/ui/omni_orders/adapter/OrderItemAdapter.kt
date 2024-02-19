package com.armada.storeapp.ui.omni_orders.adapter

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
import com.armada.storeapp.data.model.response.OmnIOrderDetailX
import com.armada.storeapp.data.model.response.SkuMasterTypes
import com.armada.storeapp.databinding.LayoutOmniItemBinding
import com.armada.storeapp.databinding.TestLayoutBinding
import com.armada.storeapp.ui.utils.Constants
import com.armada.storeapp.ui.utils.SwipeRevealLayout.ViewBinderHelper
import com.armada.storeapp.ui.utils.Utils
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.layout_bag_item.view.*
import java.util.*

class OrderItemAdapter(
    private val activity: FragmentActivity,
    private val selectedCurrency: String,
    var itemList: ArrayList<OmnIOrderDetailX>?
) : RecyclerView.Adapter<OrderItemAdapter.ItemHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemHolder {
        return ItemHolder(
            LayoutOmniItemBinding.inflate(
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

        val orderItem = itemList?.get(position)

        holder.cartListSwipeBinding.tvProductName.text = (orderItem?.skuName)
        holder.cartListSwipeBinding.tvPrice.text =
            (Utils.getPriceFormatted(orderItem?.oC_LineTotal?.toString(), selectedCurrency))
        holder.cartListSwipeBinding.tvSku.text = orderItem?.skuCode



        holder.cartListSwipeBinding.tvQty.text = "Qty:${orderItem?.quantity}"

        try {
            val decodedString = Base64.decode(orderItem?.skuimage, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            holder.cartListSwipeBinding.imageViewProduct.setImageBitmap(bitmap)
            holder.cartListSwipeBinding.tvNoImage.visibility = View.GONE
        } catch (exception: Exception) {
            exception.printStackTrace()
            if (!orderItem?.skuImageUrl.isNullOrEmpty()) {
                holder.cartListSwipeBinding.tvNoImage.visibility = View.GONE
                Glide.with(activity).load(orderItem?.skuImageUrl)
                    .into(holder.cartListSwipeBinding.imageViewProduct)
            } else {
                holder.cartListSwipeBinding.tvNoImage.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return if (itemList == null) 0 else itemList!!.size
    }

    class ItemHolder(val cartListSwipeBinding: LayoutOmniItemBinding) :
        RecyclerView.ViewHolder(cartListSwipeBinding.root)
}