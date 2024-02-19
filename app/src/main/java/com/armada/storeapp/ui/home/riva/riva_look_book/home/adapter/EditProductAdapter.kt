package com.armada.storeapp.ui.home.riva.riva_look_book.home.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.transition.CrossfadeTransition
import com.armada.storeapp.R
import com.armada.storeapp.data.model.response.EditorialResponse
import com.armada.storeapp.databinding.EditProductItemBinding
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.OmniProductDetailsActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.ProductDetailsActivity
import com.armada.storeapp.ui.utils.Constants
import com.armada.storeapp.ui.utils.Utils
import kotlinx.android.synthetic.main.edit_product_item.view.*

class EditProductAdapter(
    private val mContext: Context,
    private val selectedCurrency:String,
    var productArrList: ArrayList<EditorialResponse.Data.Product>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val textWidth: Int
    private val layoutInflater: LayoutInflater
    private var strUserId = ""
    private val grideHeight: Int
    private val gridWidth: Int
    private val screenWidth: Int
    private val margin5: Int
    private val margin7: Int
    private val margin10: Int
    private val margin3: Int
    var VIEW_TYPE_ITEM = 0
    private var amazingPrice = 0.0
    var colorFinalPrice = 0
    var colorRegularPrice = 0
    var colrAmazingPrice = 0

    val isEmpty: Boolean
        get() = itemCount == 0

    init {
        layoutInflater = LayoutInflater.from(mContext)
        val metrics = mContext.resources.displayMetrics
//        strUserId = SharedPreferencesManager.getString(mContext, Constants.PREFS_USER_ID, "")
        screenWidth = (metrics.widthPixels)

        textWidth = ((screenWidth / 2) - mContext.resources.getDimension(R.dimen.ten_dp)).toInt()

        margin5 = mContext.resources.getDimension(R.dimen.five_dp).toInt()
        margin7 = mContext.resources.getDimension(R.dimen.seven_dp).toInt()
        margin10 = mContext.resources.getDimension(R.dimen.ten_dp).toInt()
        margin3 = mContext.resources.getDimension(R.dimen.corner_radius).toInt()

        gridWidth = mContext.resources.getDimension(R.dimen.seventy_dp).toInt()
        grideHeight = (gridWidth * 1.4).toInt()

        amazingPrice = Utils.getAmazingPrice()
        colorFinalPrice = mContext.resources.getColor(R.color.black)
        colorRegularPrice = mContext.resources.getColor(R.color.regular_price)
        colrAmazingPrice = mContext.resources.getColor(R.color.final_price)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): androidx.recyclerview.widget.RecyclerView.ViewHolder {

        val binding = EditProductItemBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return ItemHolder(binding)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (getItemViewType(position)) {
            VIEW_TYPE_ITEM -> {
                holder as ItemHolder
                val productItem = productArrList?.get(position)
                val small_param = RelativeLayout.LayoutParams(gridWidth, grideHeight)
                val small_pa1 =
                    RelativeLayout.LayoutParams(gridWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
                small_pa1.topMargin = margin10
                small_pa1.bottomMargin = margin5

                small_pa1.rightMargin = margin10



                holder.editProductItemBinding.rltmain.layoutParams = small_pa1
                holder.editProductItemBinding.imgProduct.layoutParams = small_param
                holder.editProductItemBinding.relImage.layoutParams = small_param


                if (productItem?.final_price != null && !productItem?.final_price.isEmpty())
                    holder.editProductItemBinding.txtPrice.text =
                        Utils.getPriceFormatted(productItem.final_price,selectedCurrency)

                if (productItem?.regular_price != null && !productItem?.regular_price.isEmpty())
                    holder.editProductItemBinding.txtRegularPrice.text =
                        Utils.getPriceFormatted(productItem.regular_price,selectedCurrency)

                holder.editProductItemBinding.txtRegularPrice.visibility = View.GONE
                holder.editProductItemBinding.txtProductName.visibility = View.GONE

                if (productItem?.final_price != null && !productItem?.final_price.isEmpty() && (productItem.final_price.toDouble() <= amazingPrice)) {
                    holder.editProductItemBinding.txtPrice.setTextColor(colrAmazingPrice)
                } else {
                    holder.editProductItemBinding.txtPrice.setTextColor(colorFinalPrice)
                }
                holder.itemView.imgProduct.load(if (productItem?.image != null && productItem.image != "") "http:" + productItem.image else Constants.strNoImage) {
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
                            holder.editProductItemBinding.spin.visibility = View.GONE
                        }
                    })
                    transition(CrossfadeTransition())
                }

                holder.editProductItemBinding.root.setOnClickListener()
                {

                    val intent = Intent(mContext, OmniProductDetailsActivity::class.java)
                    intent.putExtra("id", productItem.product_id.toString())
                    intent.putExtra("cat_id", "765")
                    intent.putExtra("name", productItem.name)
                    intent.putExtra("size_guide", "")
                    mContext.startActivity(intent)
                }

            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE_ITEM
    }


    override fun getItemCount(): Int {
        return if (productArrList == null) 0 else productArrList!!.size
    }

    inner class ItemHolder(val editProductItemBinding: EditProductItemBinding) :
        RecyclerView.ViewHolder(editProductItemBinding.root)

}