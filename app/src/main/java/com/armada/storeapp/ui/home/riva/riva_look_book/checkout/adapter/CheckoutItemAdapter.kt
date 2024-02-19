package com.armada.storeapp.ui.home.riva.riva_look_book.checkout.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.transition.CrossfadeTransition
import com.armada.storeapp.R
import com.armada.storeapp.data.model.response.CheckStockResponseModel
import com.armada.storeapp.data.model.response.StockResponseModel
import com.armada.storeapp.databinding.LayoutItemCheckoutBinding
import com.armada.storeapp.ui.home.riva.riva_look_book.checkout.CheckoutActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.checkout.adapter.CheckoutItemAdapter.*
import com.armada.storeapp.ui.utils.Constants
import java.util.*

class CheckoutItemAdapter(
    private val mContext: CheckoutActivity,
    private var itemList: ArrayList<CheckStockResponseModel.Item>?
) : RecyclerView.Adapter<ItemHolder>() {
    private val productWidth: Int
    private val productHeight: Int
    private val productGrid: Int
    private val textWidth: Int
    private val layoutInflater: LayoutInflater = LayoutInflater.from(mContext)
    private val viewTypeItem = 0
    private val screenWidth: Int
    private val listItemH: Int
    private val listItemW: Int
    private val margin5: Int
    private val margin7: Int
    private val margin10: Int
    var colorFinalPrice = ContextCompat.getColor(mContext, R.color.brown_text_color)
    var colorRegularPrice = ContextCompat.getColor(mContext, R.color.regular_price)
    val isEmpty: Boolean
        get() = itemCount == 0

    init {
        val metrics = mContext.resources.displayMetrics
        screenWidth = (metrics.widthPixels)

        productWidth =
            (screenWidth / 3).toInt() - (mContext.resources.getDimension(R.dimen.ten_dp)).toInt()
        productGrid =
            ((screenWidth / 3) - mContext.resources.getDimension(R.dimen.ten_dp)).toInt()
        productHeight = (productWidth * 1.3).toInt()
        listItemW =
            screenWidth - (2 * (mContext.resources.getDimension(R.dimen.ten_dp))).toInt()
        listItemH = listItemW
        textWidth =
            ((screenWidth / 2) - mContext.resources.getDimension(R.dimen.ten_dp)).toInt()

        margin5 = mContext.resources.getDimension(R.dimen.five_dp).toInt()
        margin7 = mContext.resources.getDimension(R.dimen.seven_dp).toInt()
        margin10 = mContext.resources.getDimension(R.dimen.ten_dp).toInt()

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemHolder {
        return ItemHolder(
            LayoutItemCheckoutBinding.inflate(
                LayoutInflater.from(mContext),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ItemHolder,
        position: Int
    ) {
        val selectedItem = itemList?.get(position)
        holder.binding.imgProduct.load(if (selectedItem?.image != null && selectedItem.image != "") selectedItem.image else Constants.strNoImage) {
            crossfade(true)
            crossfade(800)
            size(productWidth, productHeight)
            allowConversionToBitmap(true)
            bitmapConfig(Bitmap.Config.ARGB_8888)
            allowHardware(true)
            listener(object : ImageRequest.Listener {
                override fun onSuccess(
                    request: ImageRequest,
                    metadata: ImageResult.Metadata
                ) {
                    super.onSuccess(request, metadata)
                }
            })
            transition(CrossfadeTransition())
        }

    }

    override fun getItemViewType(position: Int): Int {
        return viewTypeItem
    }


    override fun getItemCount(): Int {
        return if (itemList == null) 0 else itemList!!.size
    }

    inner class ItemHolder(val binding: LayoutItemCheckoutBinding) :
        RecyclerView.ViewHolder(binding.root)

}
