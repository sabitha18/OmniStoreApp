package com.armada.storeapp.ui.home.riva.riva_look_book.omni_order_place.adapter

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
import com.armada.storeapp.data.model.response.SkuMasterTypes
import com.armada.storeapp.databinding.LayoutOmniItemBinding
import com.armada.storeapp.databinding.TestLayoutBinding
import com.armada.storeapp.ui.utils.Constants
import com.armada.storeapp.ui.utils.SwipeRevealLayout.ViewBinderHelper
import com.armada.storeapp.ui.utils.Utils
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.layout_bag_item.view.*
import java.util.*

class OmniItemAdapter(
    private val activity: FragmentActivity,
    private val selectedCurrency: String,
    var cartArrayList: ArrayList<SkuMasterTypes>?
) : RecyclerView.Adapter<OmniItemAdapter.ItemHolder>() {

    private var arrListGlobal: ArrayList<SkuMasterTypes>? = null
    var onDeleteClicked: ((SkuMasterTypes) -> Unit)? = null
    var onQtyChanged: ((SkuMasterTypes) -> Unit)? = null

    init {
        arrListGlobal = ArrayList<SkuMasterTypes>()
        arrListGlobal = cartArrayList  /// To Keep Updated cart List for cart item stock

    }


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

        val cartItem = cartArrayList?.get(position)

        holder.cartListSwipeBinding.tvProductName.text = (cartItem?.productGroupName)
        holder.cartListSwipeBinding.tvPrice.text =
            (Utils.getPriceFormatted(cartItem?.stylePrice?.toString(), selectedCurrency))
        holder.cartListSwipeBinding.tvSku.text = cartItem?.skuCode

        if (cartItem?.quantity == null)
            cartItem?.quantity = 1
        holder.cartListSwipeBinding.tvQty.text = (cartItem?.quantity.toString())

//        holder.cartListSwipeBinding.tvStock.text = "Avail.Qty: ${cartItem?.availableQty}"

//        if (cartItem?.stock!! > 0) {
//            holder.cartListSwipeBinding.tvStock.text = "Avail.Qty: ${cartItem?.stock}"
//            holder.cartListSwipeBinding.tvStock.setTextColor(activity.resources.getColor(R.color.green))
//            holder.cartListSwipeBinding.tvAvailability.text = "Available"
//        } else {
//            holder.cartListSwipeBinding.tvStock.text = "Avail.Qty: 0"
//            holder.cartListSwipeBinding.tvStock.setTextColor(activity.resources.getColor(R.color.red))
//            holder.cartListSwipeBinding.tvAvailability.text = "Unavailable"
//        }

        holder.cartListSwipeBinding.tvQty.text = "Qty:${cartItem?.quantity}"

        try {
            val decodedString = Base64.decode(cartItem?.skuImage, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            holder.cartListSwipeBinding.imageViewProduct.setImageBitmap(bitmap)
            holder.cartListSwipeBinding.tvNoImage.visibility = View.GONE
        } catch (exception: Exception) {
            exception.printStackTrace()
            if (!cartItem?.imageUrl.isNullOrEmpty()) {
                holder.cartListSwipeBinding.tvNoImage.visibility = View.GONE
                Glide.with(activity).load(cartItem?.imageUrl)
                    .into(holder.cartListSwipeBinding.imageViewProduct)
            } else {
                holder.cartListSwipeBinding.tvNoImage.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return if (cartArrayList == null) 0 else cartArrayList!!.size
    }

    class ItemHolder(val cartListSwipeBinding: LayoutOmniItemBinding) :
        RecyclerView.ViewHolder(cartListSwipeBinding.root)
}