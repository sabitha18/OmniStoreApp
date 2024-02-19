package com.armada.riva.HOME.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.ImageRequest
import coil.request.ImageResult
import com.armada.storeapp.R
import com.armada.storeapp.SearchQuery
import com.armada.storeapp.data.local.model.RecentSearch
import com.armada.storeapp.databinding.ItemSearchBinding
import com.armada.storeapp.ui.home.riva.riva_look_book.RivaLookBookActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.OmniProductDetailsActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.ProductDetailsActivity
import com.armada.storeapp.ui.utils.Constants
import com.armada.storeapp.ui.utils.Utils
import com.bumptech.glide.util.Util
import java.lang.Float
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.ceil

class SearchAdapter(private val selectedCurrency: String,
    val searchList: ArrayList<SearchQuery.Item1?>?,
    private val rivaLookBookActivity: RivaLookBookActivity,
    strSearchString: String
) : RecyclerView.Adapter<SearchAdapter.MyViewHolder>() {

    private var strSearchString: String = ""

    private var amazingPrice = 0.0
    var colorFinalPrice = 0
    var colorRegularPrice = 0
    var colrAmazingPrice = 0
    var productWidth = 0
    var productheight = 0
    var typeSemibold: Typeface? = null
    var typeBold: Typeface? = null
    var typeNormal: Typeface? = null
    var typeMedium: Typeface? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemSearchBinding.inflate(
                LayoutInflater.from(rivaLookBookActivity),
                parent,
                false
            )
        )
    }

    init {

        this.strSearchString = strSearchString

        amazingPrice = Utils.getAmazingPrice()
        colorFinalPrice = ContextCompat.getColor(rivaLookBookActivity, R.color.brown_text_color)
        colorRegularPrice = ContextCompat.getColor(rivaLookBookActivity, R.color.regular_price)
        colrAmazingPrice = ContextCompat.getColor(rivaLookBookActivity, R.color.final_price)

    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val searchItem =searchList?.get(position)

        holder.itemSearchBinding.spin.visibility=View.VISIBLE

        val small_param = RelativeLayout.LayoutParams(productWidth, productheight)
        val small_pa1 =
            RelativeLayout.LayoutParams(productWidth, ViewGroup.LayoutParams.WRAP_CONTENT)

            holder.itemSearchBinding.imgProduct.load(if (searchItem?.small_image?.url != null && searchItem?.small_image.url != "") searchItem.small_image.url else Constants.strNoImage) {
                crossfade(true)
                allowConversionToBitmap(true)
                bitmapConfig(Bitmap.Config.ARGB_8888)
                allowHardware(true)
                listener(object : ImageRequest.Listener {
                    override fun onSuccess(
                        request: ImageRequest,
                        metadata: ImageResult.Metadata
                    ) {
                        super.onSuccess(request, metadata)
                        holder.itemSearchBinding.progress.visibility = View.GONE
                    }
                })
            }

        //small_pa1.topMargin = margin10
        holder.itemSearchBinding.rltmain.layoutParams = small_pa1
        holder.itemSearchBinding.imgProduct.layoutParams = small_param
        holder.itemSearchBinding.relImage.layoutParams = small_param

        small_pa1.rightMargin = rivaLookBookActivity?.resources?.getDimension(R.dimen.eight_dp)?.toInt() ?: 0

        val param = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, rivaLookBookActivity?.resources?.getDimension(
                R.dimen.thirty_dp
            )?.toInt() ?: 0
        )
        param.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.imgProduct)
        param.bottomMargin = rivaLookBookActivity?.resources?.getDimension(R.dimen.eight_dp)?.toInt() ?: 0

        holder.itemSearchBinding.relSold.setPadding(
            rivaLookBookActivity?.resources?.getDimension(R.dimen.eight_dp)?.toInt() ?: 0,
            rivaLookBookActivity?.resources?.getDimension(R.dimen.eight_dp)?.toInt() ?: 0,
            rivaLookBookActivity?.resources?.getDimension(R.dimen.eight_dp)?.toInt() ?: 0,
            rivaLookBookActivity?.resources?.getDimension(R.dimen.eight_dp)?.toInt() ?: 0
        )

        param.addRule(RelativeLayout.ALIGN_PARENT_LEFT)

        holder.itemSearchBinding.relSold.layoutParams = param

        holder.itemSearchBinding.txtProductName.text = (searchItem?.name)
        val price = searchItem?.price_range?.minimum_price

        holder.itemSearchBinding.txtPrice.setTextColor(colorFinalPrice)

        holder.itemSearchBinding.txtPrice.text =
            (Utils.getPriceFormatted(price?.final_price?.value.toString(),selectedCurrency))

        holder.itemSearchBinding.txtRegularPrice.text =
            (Utils.getPriceFormatted(price?.regular_price?.value.toString(),selectedCurrency))

        holder.itemSearchBinding.txtRegularPrice.paintFlags =
            holder.itemSearchBinding.txtRegularPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG


        try {
            if (price?.final_price?.value != null && price.regular_price.value != null) {

                if (price.regular_price.value != price.final_price.value) {
                    holder.itemSearchBinding.txtRegularPrice.setTextColor(colorFinalPrice)
                    holder.itemSearchBinding.txtRegularPrice.visibility = View.VISIBLE
                    holder.itemSearchBinding.txtDiscount.visibility = View.VISIBLE
                    val discount = 100 - ceil(
                        (Float.parseFloat(price.final_price.value.toString()) / Float.parseFloat(
                            price.regular_price.value.toString()
                        ) * 100).toDouble()
                    )
                    holder.itemSearchBinding.txtDiscount.text =
                        "(" + String.format(Locale.ENGLISH, "%.0f", discount) + "%)"
                } else {
                    holder.itemSearchBinding.txtPrice.setTextColor(colorFinalPrice)
                    holder.itemSearchBinding.txtRegularPrice.visibility = View.GONE
                    holder.itemSearchBinding.txtDiscount.visibility = View.GONE
                }

//                if (price?.final_price != null && price?.final_price > 0.0 && price?.final_price.toDouble() <= amazingPrice)
//                    userViewHolder.txtProductPrice.setTextColor(colrAmazingPrice)

            } else {
                holder.itemSearchBinding.txtPrice.setTextColor(colorRegularPrice)
                holder.itemSearchBinding.txtRegularPrice.setTextColor(colorRegularPrice)
                holder.itemSearchBinding.txtRegularPrice.visibility = View.GONE
                holder.itemSearchBinding.txtDiscount.visibility = View.GONE
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        holder.itemSearchBinding.txtSale.visibility = View.GONE

        holder.itemSearchBinding.relSold.alpha = 0.7.toFloat()

        holder.itemSearchBinding.rltmain.setOnClickListener {
            ////Adding to recent Search
            val recentModel = RecentSearch(0,
                searchItem?.id.toString(),
                searchItem?.id.toString(),
                searchItem?.name.toString(),
                searchItem?.small_image?.url.toString()
            )
//            globalClass.insertUpdateRecentSearch(helper!!, recentModel) //todo


            val intent = Intent(rivaLookBookActivity, OmniProductDetailsActivity::class.java)
            intent.putExtra("id", searchItem?.id.toString())
            intent.putExtra("cat_id", "-1")
            intent.putExtra("name", searchItem?.name)
            intent.putExtra("image", searchItem?.small_image?.url)
            rivaLookBookActivity.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return if (searchList!!.size > 10) 10 else searchList.size
    }

    class MyViewHolder(val itemSearchBinding: ItemSearchBinding) :
        RecyclerView.ViewHolder(itemSearchBinding.root)
}