package com.armada.storeapp.ui.home.riva.riva_look_book.bag.adapter

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.armada.storeapp.R
import com.armada.storeapp.data.model.response.AddToCartResponse
import com.armada.storeapp.data.model.response.SkuMasterTypes
import com.armada.storeapp.databinding.SkuItemBinding
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.OmniProductDetailsActivity
import com.armada.storeapp.ui.utils.Constants
import com.armada.storeapp.ui.utils.SwipeRevealLayout.ViewBinderHelper
import com.armada.storeapp.ui.utils.Utils
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.layout_bag_item.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SkuItemAdapter(
    private val activity: FragmentActivity,
    private val selectedCurrency: String,
    var cartArrayList: ArrayList<SkuMasterTypes>?
) : RecyclerView.Adapter<SkuItemAdapter.ItemHolder>() {

    var onDeleteClicked: ((SkuMasterTypes) -> Unit)? = null
    var onQtyChanged: ((SkuMasterTypes) -> Unit)? = null


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemHolder {
        return ItemHolder(
            SkuItemBinding.inflate(
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

        holder.cartListSwipeBinding.tvSkuName.text = cartItem?.skuCode

        holder.cartListSwipeBinding.root.setOnClickListener {
            println(" clicked ------   ")
            onDeleteClicked?.invoke(cartItem!!)

        }
    }

    private fun viewItemImage(
        imageStringBase64: String?,
        imageViewProduct: ImageView,
        cartItem: SkuMasterTypes
    ) {
//        var base64String = imageStringBase64
//        if (base64String!!.contains("data:image/jpeg;base64")) {
//            base64String = base64String.replace("data:image/jpeg;base64,", "")
//        }

    }

    fun clearAdapter(){
        cartArrayList?.clear()
        notifyDataSetChanged()
    }

    fun setAdapter(list:ArrayList<SkuMasterTypes>){
        cartArrayList=list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (cartArrayList == null) 0 else cartArrayList!!.size
    }

    class ItemHolder(val cartListSwipeBinding: SkuItemBinding) :
        RecyclerView.ViewHolder(cartListSwipeBinding.root)
}