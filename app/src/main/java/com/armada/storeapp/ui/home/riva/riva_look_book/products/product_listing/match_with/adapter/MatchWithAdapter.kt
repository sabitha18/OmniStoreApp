package com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing.match_with.adapter

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.transition.CrossfadeTransition
import com.armada.storeapp.R
import com.armada.storeapp.data.model.response.Related
import com.armada.storeapp.databinding.LayoutItemRelatedBinding
import com.armada.storeapp.databinding.LayoutMatchItemBinding
import com.armada.storeapp.ui.home.riva.riva_look_book.bag.BagActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.bag.OmniBagActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.ProductDetailsActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.adapter.MatchedProductAdapter
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing.match_with.MatchWithActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing.model.ProductListMSortingModel
import com.armada.storeapp.ui.utils.Constants
import com.armada.storeapp.ui.utils.Utils
import java.util.*
import kotlin.collections.ArrayList

class MatchWithAdapter(
    private val selectedCurrency: String,
    private val activity: MatchWithActivity,
    private val itemList: ArrayList<Related>?
) : RecyclerView.Adapter<MatchWithAdapter.MyViewHolder>() {

    var onProductAddedToCart: ((Related) -> Unit)? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MatchWithAdapter.MyViewHolder {

        return MyViewHolder(
            LayoutMatchItemBinding.inflate(
                LayoutInflater.from(activity),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val selectedItem = itemList?.get(position)

        holder.binding.tvRegularPrice.paintFlags = 0
        holder.binding.imgProduct.load(if (selectedItem?.image != null && selectedItem?.image != "") selectedItem?.image else Constants.strNoImage) {
            crossfade(true)
            crossfade(800)
            allowConversionToBitmap(true)
            bitmapConfig(Bitmap.Config.ARGB_8888)
            allowHardware(true)
            transition(CrossfadeTransition())
        }

        holder.binding.tvProductName.text = (selectedItem?.name?.split(' ')?.joinToString(" ") {
            it.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }
        })

        if (selectedItem?.is_salable!!) {
            holder.binding.txtSold.visibility = View.GONE
        } else {
            holder.binding.txtSold.visibility = View.GONE
            holder.binding.btnAddToBag.visibility = View.GONE
            holder.binding.btnViewBag.visibility = View.GONE
        }

        if (selectedItem?.is_added_to_cart) {
            holder.binding.btnViewBag.visibility = View.VISIBLE
            holder.binding.btnAddToBag.visibility = View.GONE
        } else {
            holder.binding.btnViewBag.visibility = View.GONE
            holder.binding.btnAddToBag.visibility = View.VISIBLE
        }

        if (selectedItem?.price != null && !selectedItem?.price.isEmpty())
            holder.binding.tvRegularPrice.text =
                (Utils.getPriceFormatted(selectedItem?.price, selectedCurrency))

        if ((selectedItem?.price)?.toDouble() != (selectedItem?.final_price)?.toDouble()) {
            val discount = 100 - Math.ceil(
                (java.lang.Float.parseFloat(selectedItem?.final_price) / java.lang.Float.parseFloat(
                    selectedItem?.price
                        ?: "0.0"
                ) * 100).toDouble()
            )
//            holder.binding.txtDiscount.text = String.format(
//                Locale.ENGLISH,
//                "%.0f",
//                discount
//            ) + "% " + activity?.resources.getString(R.string.off)
            if (selectedItem?.final_price != null && !selectedItem?.final_price.isEmpty())
                holder.binding.tvDiscountedPrice.text =
                    (Utils.getPriceFormatted(selectedItem?.final_price, selectedCurrency))
            holder.binding.txtDiscount.text =
                String.format(Locale.ENGLISH, "%.0f", discount) + "% OFF"

            holder.binding.tvDiscountedPrice.visibility = View.VISIBLE
            holder.binding.txtDiscount.visibility = View.VISIBLE
            holder.binding.tvRegularPrice.paintFlags =
                holder.binding.tvRegularPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        } else {
            holder.binding.tvDiscountedPrice.visibility = View.GONE
            holder.binding.txtDiscount.visibility = View.GONE

        }

        holder.binding.btnAddToBag.setOnClickListener {
            onProductAddedToCart?.invoke(selectedItem)
        }
        holder.binding.btnViewBag.setOnClickListener {
            val intent = Intent(activity, OmniBagActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {

        return itemList?.size ?: 0
    }

    class MyViewHolder(val binding: LayoutMatchItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}