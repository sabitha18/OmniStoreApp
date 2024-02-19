package com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.adapter

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Paint
import android.os.Build
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import coil.load
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.transition.CrossfadeTransition
import com.armada.storeapp.R
import com.armada.storeapp.data.model.response.Related
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.OmniProductDetailsActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.ProductDetailsActivity
import com.armada.storeapp.ui.utils.Constants
import com.armada.storeapp.ui.utils.Utils
import java.util.*
import kotlin.collections.ArrayList

class LikeImagePagerAdapter(
    private val selectedCurrency:String,
    private val imageWidth: Int,
    private val imageHeight:Int,
    private val activity: ProductDetailsActivity,
    private val images: ArrayList<Related>?
) : PagerAdapter() {
    private val inflater: LayoutInflater = activity.layoutInflater
    private var amazingPrice = 0.0
    var colorFinalPrice = 0
    var colorRegularPrice = 0
    var colrAmazingPrice = 0

    init {
        amazingPrice = Utils.getAmazingPrice()
        colorFinalPrice = ContextCompat.getColor(activity, R.color.black)
        colorRegularPrice = ContextCompat.getColor(activity, R.color.regular_price)
        colrAmazingPrice = ContextCompat.getColor(activity, R.color.final_price)

    }

    override fun destroyItem(container: View, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as View?)
    }

    override fun finishUpdate(container: View) {}

    override fun getCount(): Int {
        return images!!.size
    }

    override fun instantiateItem(view: View, position: Int): Any {

        val imageLayout = inflater.inflate(R.layout.layout_liked_item, null)
        val selectedItem = images!![position]

        val imgProduct = imageLayout.findViewById<ImageView>(R.id.imgProduct)
        val txtProductName = imageLayout.findViewById<TextView>(R.id.txtProductName)
        val spin = imageLayout.findViewById<ProgressBar>(R.id.spin)
        val togWishlist = imageLayout.findViewById<ToggleButton>(R.id.togWishlist)
        val linLayout = imageLayout.findViewById<RelativeLayout>(R.id.linLayout)
        val imgSale = imageLayout.findViewById<ImageView>(R.id.imgMatchSale)
        val txtMatchSale = imageLayout.findViewById<TextView>(R.id.txtMatchSale)

        val txtRegularPrice = imageLayout.findViewById<TextView>(R.id.txtRegularPrice)
        val txtProductPrice = imageLayout.findViewById<TextView>(R.id.txtPrice)
        val txtDiscountPriceLike = imageLayout.findViewById<TextView>(R.id.txtDiscountPrice)


        txtProductName.text = (selectedItem?.name?.split(' ')?.joinToString(" ") {
            it.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }
        })

        if (!activity.isFinishing) {
            imgProduct.load(if (selectedItem?.image != null && selectedItem?.image != "") selectedItem?.image else Constants.strNoImage) {
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
                        spin.visibility = View.GONE
                    }
                })
                transition(CrossfadeTransition())
            }


        }

        txtRegularPrice.paintFlags = txtRegularPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        txtProductName.text = (selectedItem?.name?.split(' ')?.joinToString(" ") {
            it.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }
        })

        if (selectedItem?.final_price != null && !selectedItem?.final_price.isEmpty())
            txtProductPrice.text = (Utils.getPriceFormatted(selectedItem?.final_price,selectedCurrency))

        try {

            if (selectedItem?.price != null && (selectedItem?.price?.isEmpty() == false)) {
                var strPrice = selectedItem?.price
                strPrice = strPrice?.replace("٫", ".")?.trim()
                strPrice = strPrice?.replace(",", "")?.trim()
                strPrice = strPrice?.replace("$", "")?.trim()
                selectedItem?.price = strPrice
                txtRegularPrice.text =
                    Utils.getPriceFormatted(strPrice!!,selectedCurrency)
            }
        } catch (e: Exception) {
            selectedItem?.price = selectedItem?.final_price
        }

        if (selectedItem?.final_price != null && selectedItem?.price != null) {
            if ((selectedItem?.price)?.toDouble() != (selectedItem?.final_price).toDouble()) {
                txtRegularPrice.visibility = View.VISIBLE
                txtDiscountPriceLike.visibility = View.VISIBLE
                val discount = 100 - Math.ceil(
                    (java.lang.Float.parseFloat(selectedItem?.final_price) / java.lang.Float.parseFloat(
                        selectedItem?.price
                            ?: "0.0"
                    ) * 100).toDouble()
                )
                txtDiscountPriceLike.text =
                    "(" + String.format(Locale.ENGLISH, "%.0f", discount) + "%)"

            } else {
                txtDiscountPriceLike.visibility = View.GONE
                txtRegularPrice.visibility = View.GONE

            }

            if (selectedItem?.final_price != null && !selectedItem?.final_price.isEmpty() && selectedItem?.final_price.toDouble() <= amazingPrice)
                txtProductPrice.setTextColor(colrAmazingPrice)

        } else {
            txtRegularPrice.visibility = View.GONE
            txtDiscountPriceLike.visibility = View.GONE
        }

        val imageSize = RelativeLayout.LayoutParams(imageWidth.toInt(), imageHeight.toInt())
        imageSize.addRule(RelativeLayout.CENTER_HORIZONTAL)
        imgProduct.layoutParams = imageSize

        linLayout.setOnClickListener()
        {
            val intent = Intent(activity, OmniProductDetailsActivity::class.java)
            intent.putExtra("id", selectedItem?.id)
            intent.putExtra("name", selectedItem?.name)
            intent.putExtra("image", selectedItem?.image)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imgProduct.transitionName = "imageproduct"
                val options: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        activity,
                        imgProduct,
                        "imageproduct"
                    )
                activity.startActivity(intent, options.toBundle())
            } else {
                activity.startActivity(intent)
            }

        }

        txtMatchSale.visibility = View.GONE

        (view as ViewPager).addView(imageLayout)

        return imageLayout
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}

    override fun saveState(): Parcelable? {
        return null
    }

    override fun startUpdate(container: View) {}
}
