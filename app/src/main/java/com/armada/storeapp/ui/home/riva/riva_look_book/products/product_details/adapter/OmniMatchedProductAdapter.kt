package com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.adapter

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
import com.armada.storeapp.data.model.response.Related
import com.armada.storeapp.databinding.LayoutItemRelatedBinding
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.OmniProductDetailsActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.ProductDetailsActivity
import com.armada.storeapp.ui.utils.Constants
import com.armada.storeapp.ui.utils.Utils
import java.util.*
import kotlin.collections.ArrayList

class OmniMatchedProductAdapter(
    private val selectedCurrency:String,
    private val productheight: Int,
    private val screenWidth: Int,
    private val activity: OmniProductDetailsActivity,
    private val arrlist: ArrayList<Related>?
) : RecyclerView.Adapter<OmniMatchedProductAdapter.MyViewHolder>() {
    private var amazingPrice = 0.0
    var colorFinalPrice = 0
    var colorRegularPrice = 0
    var colrAmazingPrice = 0
    private var width: Int = 0
    private var widthHalf: Int = 0
    private var margin5: Int = 0
    private var margin7 = 0
    private var margin10 = 0

    init {

        width = ((screenWidth / 2) - activity?.resources?.getDimension(R.dimen.seven_dp)!!).toInt()
        widthHalf =
            ((screenWidth / 2) - activity?.resources?.getDimension(R.dimen.seven_dp)!!).toInt()

        amazingPrice = Utils.getAmazingPrice()
        colorFinalPrice = ContextCompat.getColor(activity, R.color.black)
        colorRegularPrice = ContextCompat.getColor(activity, R.color.regular_price)
        colrAmazingPrice = ContextCompat.getColor(activity, R.color.final_price)
        margin5 = activity.resources.getDimension(R.dimen.five_dp).toInt()
        margin7 = activity.resources.getDimension(R.dimen.seven_dp).toInt()
        margin10 = activity.resources.getDimension(R.dimen.ten_dp).toInt()

    }

    var onItemAddedToCart: ((Related) -> Unit)? = null
    var onItemClick: ((Related) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        return MyViewHolder(
            LayoutItemRelatedBinding.inflate(
                LayoutInflater.from(activity),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val selectedItem = arrlist?.get(position)

            try {
                if (widthHalf.toInt() > 0 && productheight.toInt() > 0) {
                    holder.binding.imgProduct.load(if (selectedItem?.image != null && selectedItem?.image != "") selectedItem?.image else Constants.strNoImage) {
                        crossfade(true)
                        allowConversionToBitmap(true)
                        bitmapConfig(Bitmap.Config.ARGB_8888)
                        allowHardware(true)
                        size(widthHalf, productheight)
                        listener(object : ImageRequest.Listener {
                            override fun onSuccess(
                                request: ImageRequest,
                                metadata: ImageResult.Metadata
                            ) {
                                super.onSuccess(request, metadata)
                                holder.binding.progressBar.visibility = View.GONE
                            }
                        })
                        transition(CrossfadeTransition(800, false))
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            } catch (e: OutOfMemoryError) {
                e.printStackTrace()
            }

        val small_param = RelativeLayout.LayoutParams(width, productheight)
        val small_param_half = RelativeLayout.LayoutParams(widthHalf, productheight)

        val small_pa1 = RelativeLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        val small_pal_half =
            RelativeLayout.LayoutParams(widthHalf, ViewGroup.LayoutParams.WRAP_CONTENT)
        if (arrlist?.size!! > 2) {
            small_pa1.topMargin = margin5
//            holder.binding.root.layoutParams = small_pa1
            holder.binding.imgProduct.layoutParams = small_param
        } else {
            small_pa1.topMargin = margin5
//            holder.binding.root.layoutParams = small_pal_half
            holder.binding.imgProduct.layoutParams = small_param_half
        }

        small_pa1.rightMargin = margin10
        small_pal_half.rightMargin = margin10

        if (selectedItem?.is_salable!!) {
            holder.binding.txtSold.visibility = View.GONE
            holder.binding.imageViewAddToBag.visibility = View.VISIBLE
        } else {
            holder.binding.txtSold.visibility = View.VISIBLE
            holder.binding.imageViewAddToBag.visibility = View.GONE
        }


        holder.binding.tvProductName.text = (selectedItem?.name?.split(' ')?.joinToString(" ") {
            it.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }
        })


        if (selectedItem?.price != null && !selectedItem?.price.isEmpty())
            holder.binding.tvRegularPrice.text =
                (Utils.getPriceFormatted(selectedItem?.final_price,selectedCurrency))

        try {

            if (selectedItem?.price != null && (selectedItem?.price?.isEmpty() == false)) {
                var strPrice = selectedItem?.price
                strPrice = strPrice?.replace("٫", ".")?.trim()
                strPrice = strPrice?.replace(",", "")?.trim()
                strPrice = strPrice?.replace("$", "")?.trim()
                selectedItem?.price = strPrice
                holder.binding.tvRegularPrice.text =
                    Utils.getPriceFormatted(strPrice!!,selectedCurrency)
            }
        } catch (e: Exception) {
            selectedItem?.price = selectedItem?.final_price!!
        }

        if (selectedItem?.final_price != null && selectedItem?.price != null) {
            if ((selectedItem?.price)?.toDouble() != (selectedItem?.final_price).toDouble()) {
                holder.binding.tvRegularPrice.paintFlags =
                    holder.binding.tvRegularPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                holder.binding.tvDiscountedPrice.visibility = View.VISIBLE
                val discount = 100 - Math.ceil(
                    (java.lang.Float.parseFloat(selectedItem?.final_price) / java.lang.Float.parseFloat(
                        selectedItem?.price
                            ?: "0.0"
                    ) * 100).toDouble()
                )
                holder.binding.tvDiscountedPrice.text =  Utils.getPriceFormatted(selectedItem?.final_price,selectedCurrency)
//                    "(" + String.format(Locale.ENGLISH, "%.0f", discount) + "%)"

            } else {
                holder.binding.tvDiscountedPrice.visibility = View.GONE
            }

        }

        if (selectedItem?.is_salable != null && selectedItem?.is_salable!!) {
            holder.binding.txtSold.visibility = View.GONE
            holder.binding.imageViewAddToBag.isEnabled = true
        } else {
            holder.binding.txtSold.visibility = View.GONE
            holder.binding.imageViewAddToBag.isEnabled = false
        }

        holder.binding.txtSold.alpha = 0.7.toFloat()
        holder.binding.imageViewAddToBag.setOnClickListener {
            onItemAddedToCart?.invoke(selectedItem!!)
        }
        holder.binding.root.setOnClickListener()
        {
            val intent = Intent(activity, OmniProductDetailsActivity::class.java)
            //println("Here i am product details item clicked 111")
            intent.putExtra("id", selectedItem?.id)
            intent.putExtra("name", selectedItem?.name)
            intent.putExtra("image", selectedItem?.image)
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
        return arrlist!!.size
    }

    class MyViewHolder(val binding: LayoutItemRelatedBinding) :
        RecyclerView.ViewHolder(binding.root)
}