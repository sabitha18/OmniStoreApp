package com.armada.storeapp.ui.home.riva.riva_look_book.search.adapter

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
import com.armada.storeapp.data.model.response.Stock
import com.armada.storeapp.databinding.LayoutArticleItemBinding
import com.armada.storeapp.databinding.TestLayoutBinding
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.OmniProductDetailsActivity
import com.armada.storeapp.ui.utils.Constants
import com.armada.storeapp.ui.utils.SwipeRevealLayout.ViewBinderHelper
import com.armada.storeapp.ui.utils.Utils
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.layout_bag_item.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ArticleProductAdapter(
    private val activity: FragmentActivity,
    private val selectedCurrency: String,
    var productList: ArrayList<Stock>?
) : RecyclerView.Adapter<ArticleProductAdapter.ItemHolder>() {

//    var onDeleteClicked: ((SkuMasterTypes) -> Unit)? = null
//    var onQtyChanged: ((SkuMasterTypes) -> Unit)? = null


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemHolder {
        return ItemHolder(
            LayoutArticleItemBinding.inflate(
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

        val cartItem = productList?.get(position)

        holder.articleItemBinding.tvProductName.text = (cartItem?.skuName)
        holder.articleItemBinding.tvPrice.text =
            (Utils.getPriceFormatted(cartItem?.price?.toString(), selectedCurrency))

//        holder.cartListSwipeBinding.tvColor.text = cartItem?.colorName
//        holder.cartListSwipeBinding.tvSize.text = cartItem?.sizeName
        holder.articleItemBinding.tvSku.text = cartItem?.skuCode

//        if (cartItem?.quantity == null)
//            cartItem?.quantity = 1
//        holder.articleItemBinding.tv.text = (cartItem?.stockQty.toString())

//        holder.cartListSwipeBinding.tvStock.text = "Avail.Qty: ${cartItem?.availableQty}"

//        if (cartItem?.availableQty!! > 0) {
//            holder.cartListSwipeBinding.tvStock.text = "Avail.Qty: ${cartItem?.av}"
//            holder.cartListSwipeBinding.tvStock.setTextColor(activity.resources.getColor(R.color.green))
//            cartItem?.status = "Available"
//            cartItem?.availabilityStatus = true
//            holder.cartListSwipeBinding.tvAvailability.text = "Available"
//        } else {
//            holder.cartListSwipeBinding.tvStock.text = "Avail.Qty: 0"
//            holder.cartListSwipeBinding.tvStock.setTextColor(activity.resources.getColor(R.color.red))
//            holder.cartListSwipeBinding.tvAvailability.text = "Unavailable"
//            cartItem?.status = "Unavailable"
////            holder.cartListSwipeBinding.tvQty.text = "1"
//            cartItem?.availabilityStatus = false
//        }

//        if (cartItem?.quantity!! > cartItem?.availableQty!!) {
//            cartItem?.quantity = cartItem?.stock
//            holder.cartListSwipeBinding.tvQty.text = cartItem?.quantity?.toString()
//            Utils.showSnackbar(
//                holder.itemView.rootView,
//                "Maximum quantity for this item has reached"
//            )
//        }

//        if (cartItem?.availabilityStatus == null)
//            cartItem?.availabilityStatus = false
//        if (cartItem?.availableQty == null)
//            cartItem?.availableQty = 0
//        cartItem?.availabilityStatus = cartItem?.availableQty!! > 0

//        if (cartItem?.availabilityStatus == true)
//            holder.cartListSwipeBinding.tvAvailability.text = "Available"
//        else
//            holder.cartListSwipeBinding.tvAvailability.text = "Unavailable"

//        holder.cartListSwipeBinding.imageButtonDelete.setOnClickListener {
//            onDeleteClicked?.invoke(cartItem!!)
//        }
//        holder.cartListSwipeBinding.imageButtonAdd.setOnClickListener {
//            if (cartItem?.availabilityStatus!!) {
//                if (cartItem.quantity!! < cartItem?.availableQty!!) {
//                    val qty = cartItem?.quantity!! + 1
//                    holder.cartListSwipeBinding.tvQty.text = qty.toString()
//                    cartItem?.quantity = qty
//                    onQtyChanged?.invoke(cartItem!!)
//                } else {
//                    Utils.showSnackbar(
//                        holder.itemView.rootView,
//                        "Maximum quantity has reached for this item"
//                    )
//                }
//            }
//
//        }

//        holder.cartListSwipeBinding.imageButtonSubstract.setOnClickListener {
//            if (cartItem?.availabilityStatus!!) {
//                if (cartItem?.quantity!! > 1) {
//                    val qty = cartItem?.quantity!! - 1
//                    holder.cartListSwipeBinding.tvQty.text = qty.toString()
//                    cartItem?.quantity = qty
//                    onQtyChanged?.invoke(cartItem!!)
//                }
//            }
//        }
        holder.articleItemBinding.imageViewProduct.setImageDrawable(null)

        try {
            val decodedString = Base64.decode(cartItem?.skuImageSource, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            holder.articleItemBinding.imageViewProduct.setImageBitmap(bitmap)
            holder.articleItemBinding.tvNoImage.visibility = View.GONE
        } catch (exception: Exception) {
            exception.printStackTrace()
                holder.articleItemBinding.tvNoImage.visibility = View.VISIBLE

        }

        holder.articleItemBinding.root.setOnClickListener {

                val intent = Intent(activity, OmniProductDetailsActivity::class.java)
                intent.putExtra("id", cartItem?.skuCode)
                intent.putExtra("cat_id", "-1")
                intent.putExtra("image", cartItem?.skuImageSource)

                holder.articleItemBinding.imageViewProduct.transitionName = "imageproduct"
                val options: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        activity,
                        holder.articleItemBinding.imageViewProduct,
                        "imageproduct"
                    )
                activity.startActivity(intent, options.toBundle())


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
        productList?.clear()
        notifyDataSetChanged()
    }

    fun setAdapter(list:ArrayList<Stock>){
        productList=list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (productList == null) 0 else productList!!.size
    }

    class ItemHolder(val articleItemBinding: LayoutArticleItemBinding) :
        RecyclerView.ViewHolder(articleItemBinding.root)
}