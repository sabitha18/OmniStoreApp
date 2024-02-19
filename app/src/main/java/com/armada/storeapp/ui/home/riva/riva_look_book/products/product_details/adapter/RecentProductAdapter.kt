package com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.adapter

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Paint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.transition.CrossfadeTransition
import com.armada.storeapp.R
import com.armada.storeapp.data.local.model.RecentProduct
import com.armada.storeapp.databinding.LayoutItemRelatedBinding
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.OmniProductDetailsActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.ProductDetailsActivity
import com.armada.storeapp.ui.utils.Constants
import com.armada.storeapp.ui.utils.Utils
import java.util.*
import kotlin.collections.ArrayList

class RecentProductAdapter(
    private val selectedCurrency:String,
    val recentWidth:Int,
    val recentHeight:Int,
    val productWidthHalf:Int,
    private val activity: ProductDetailsActivity,
    private val arrlist: ArrayList<RecentProduct>
) : RecyclerView.Adapter<RecentProductAdapter.MyViewHolder>() {
    private var amazingPrice = 0.0
    var colorFinalPrice = 0
    var colorRegularPrice = 0
    var colrAmazingPrice = 0
    var margin10=0

    init {
        amazingPrice = Utils.getAmazingPrice()
        colorFinalPrice = ContextCompat.getColor(activity, R.color.black)
        colorRegularPrice = ContextCompat.getColor(activity, R.color.regular_price)
        colrAmazingPrice = ContextCompat.getColor(activity, R.color.final_price)
        margin10 = activity.resources.getDimension(R.dimen.ten_dp).toInt()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutItemRelatedBinding.inflate(LayoutInflater.from(activity),parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val selectedProduct = arrlist[position]
        holder.binding.progressBar.visibility = View.VISIBLE
               val small_param = RelativeLayout.LayoutParams(recentWidth, recentHeight)
        val small_param_half = RelativeLayout.LayoutParams(productWidthHalf, recentHeight)

        val small_pa1 =
            RelativeLayout.LayoutParams(recentWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
        val small_pal_half =
            RelativeLayout.LayoutParams(productWidthHalf, ViewGroup.LayoutParams.WRAP_CONTENT)

        if (!activity.isFinishing) {
            try {
                holder.binding.imgProduct.load(if (selectedProduct?.image_url != null && selectedProduct?.image_url != "") selectedProduct?.image_url else Constants.strNoImage) {
                    crossfade(true)
                    crossfade(800)
                    allowConversionToBitmap(true)
                    bitmapConfig(Bitmap.Config.ARGB_8888)
                    allowHardware(true)
                    listener(object : ImageRequest.Listener {
                        override fun onSuccess(
                            request: ImageRequest,
                            metadata: ImageResult.Metadata
                        ) {
                            super.onSuccess(request, metadata)
                            holder.binding.progressBar.visibility = View.GONE
                        }
                    })
                    transition(CrossfadeTransition())
                }
                
            } catch (e: Exception) {
                e.printStackTrace()
            } catch (e: OutOfMemoryError) {
                e.printStackTrace()
            }
        }
        
             if (arrlist.size > 2) {
            holder.binding.root.layoutParams = small_pa1
            holder.binding.imgProduct.layoutParams = small_param
        } else {
            holder.binding.root.layoutParams = small_pal_half
            holder.binding.imgProduct.layoutParams = small_param_half
        }
        
            small_pa1.rightMargin = margin10
            small_pal_half.rightMargin = margin10

        val param = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            activity.resources.getDimension(R.dimen.thirty_dp).toInt()
        )
        param.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.imgProduct)
        param.bottomMargin = activity.resources.getDimension(R.dimen.ten_dp).toInt()

        
        holder.binding.tvRegularPrice.paintFlags =
            holder.binding.tvRegularPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        holder.binding.tvProductName.text = (selectedProduct?.name?.split(' ')?.joinToString(" ") {
            it.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }
        })

        if (selectedProduct?.final_price_with_tax != null && !selectedProduct?.final_price_with_tax.isEmpty())
            holder.binding.tvRegularPrice.text =
                (Utils.getPriceFormatted(selectedProduct?.final_price_with_tax,selectedCurrency))

        try {

            if (selectedProduct?.regular_price_with_tax != null && !selectedProduct?.regular_price_with_tax!!.isEmpty()) {
                var strPrice = selectedProduct?.regular_price_with_tax
                strPrice = strPrice?.replace("٫", ".")?.trim()
                strPrice = strPrice?.replace(",", "")?.trim()
                if (strPrice?.contains(" ") == true) {
                    strPrice = strPrice?.split(" ")!![0]
                }
                selectedProduct?.regular_price_with_tax = strPrice
                holder.binding.tvRegularPrice.text =
                  Utils.getPriceFormatted(strPrice!!,selectedCurrency)
            }
        } catch (e: Exception) {
            selectedProduct?.regular_price_with_tax = selectedProduct?.final_price_with_tax
        }
        if (selectedProduct?.final_price_with_tax != null && selectedProduct?.regular_price_with_tax != null) {
         
            if ((selectedProduct?.regular_price_with_tax)?.toDouble() != (selectedProduct?.final_price_with_tax).toDouble()) {
                holder.binding.tvRegularPrice.visibility = View.VISIBLE
                holder.binding.tvDiscountedPrice.visibility = View.VISIBLE
                val discount = 100 - Math.ceil(
                    (java.lang.Float.parseFloat(selectedProduct?.final_price_with_tax) / java.lang.Float.parseFloat(selectedProduct?.regular_price_with_tax) * 100).toDouble()
                )
//                holder.binding.txtDiscount.text = String.format(
//                    Locale.ENGLISH,
//                    "%.0f",
//                    discount
//                ) + "% " + activity.resources.getString(R.string.off)
                holder.binding.tvDiscountedPrice.text =
                    "(" + String.format(Locale.ENGLISH, "%.0f", discount) + "%)"
            } else {
                holder.binding.tvDiscountedPrice.visibility = View.GONE

            }

            if (selectedProduct?.final_price_with_tax != null && !selectedProduct?.final_price_with_tax.isEmpty() && selectedProduct?.final_price_with_tax.toDouble() <= amazingPrice)
                holder.binding.tvRegularPrice.setTextColor(colrAmazingPrice)

        } else {
            holder.binding.tvRegularPrice.visibility = View.GONE
        }

        holder.binding.tvDiscountedPrice.includeFontPadding = false
        holder.binding.tvRegularPrice.includeFontPadding = false
        holder.binding.tvRegularPrice.includeFontPadding = false
        
        holder.binding.txtSale.visibility = View.GONE

        holder.binding.txtSold.alpha = 0.7.toFloat()
        holder.binding.root.setOnClickListener()
        {
            val intent = Intent(activity, OmniProductDetailsActivity::class.java)
            //println("Here i am product details item clicked 222")
            intent.putExtra("id", selectedProduct?.id?.toString())
            intent.putExtra("name", selectedProduct?.name)
            intent.putExtra("image", selectedProduct?.image_url)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.binding.imgProduct.transitionName = "imageproduct"
                val options: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        activity,
                        holder.binding.imgProduct,
                        "imageproduct"
                    )
                activity.startActivity(intent, options.toBundle())
            } else {
                activity.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return arrlist.size
    }

    class MyViewHolder(val binding: LayoutItemRelatedBinding) :
        RecyclerView.ViewHolder(binding.root)
}
