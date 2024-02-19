package com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing.adapter

import MultipleBannerAdapter
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.armada.storeapp.R
import com.armada.storeapp.data.model.response.CollectionListItemModel
import com.armada.storeapp.databinding.FooterItemBinding
import com.armada.storeapp.databinding.HeaderItemBinding
import com.armada.storeapp.databinding.LayoutProductItemBinding
import com.armada.storeapp.ui.home.riva.riva_look_book.RivaLookBookActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.OmniProductDetailsActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.ProductDetailsActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing.listener.ProductListInterface
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing.match_with.MatchWithActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing.model.ProductListMDataModel
import com.armada.storeapp.ui.utils.Constants
import com.armada.storeapp.ui.utils.Utils
import com.armada.storeapp.ui.utils.video_view.SimpleMainThreadMediaPlayerListener
import com.armada.storeapp.ui.utils.video_view.SingleVideoPlayerManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.lang.Float
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.ceil
import kotlin.math.roundToInt

class ProductListAdapter(
    private val selectedCurrency: String,
    private val mContext: RivaLookBookActivity,
    private var productItemList: ArrayList<ProductListMDataModel>?,
    private var homeModel: CollectionListItemModel?,
    private val productInterface: ProductListInterface
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onItemClick: ((ProductListMDataModel) -> Unit)? = null

    var productListInterface: ProductListInterface? = null
    private var screenWidth: Int
    private var density: Double
    private var fullWidth: Double
    var banner_height = 320
    val VIEW_TYPE_ITEM = 0
    val VIEW_TYPE_FOOTER = 1
    val VIEW_TYPE_HEADER = 2
    private var favPost: Int = 0
    private var isLoadingAdded = false
    var strId: String = ""

    init {
        val metrics = mContext.resources.displayMetrics
//        strUserId = SharedPreferencesManager.getString(mContext, Constants.PREFS_USER_ID, "")
        screenWidth = (metrics.widthPixels)
        fullWidth = (metrics.widthPixels.toDouble())
        density = (metrics.widthPixels.toDouble() / 320)
//        productListInterface=productInterface
    }

    val mVideoPlayerManager =
        SingleVideoPlayerManager {

        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            VIEW_TYPE_ITEM -> {
                return ItemViewHolder(
                    LayoutProductItemBinding.inflate(
                        LayoutInflater.from(mContext),
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_FOOTER -> {
                return LoadingViewHolder(
                    FooterItemBinding.inflate(
                        LayoutInflater.from(mContext),
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_HEADER -> {
                return HeaderViewHolder(
                    HeaderItemBinding.inflate(
                        LayoutInflater.from(mContext),
                        parent,
                        false
                    )
                )
            }
            else -> {
                return LoadingViewHolder(
                    FooterItemBinding.inflate(
                        LayoutInflater.from(mContext),
                        parent,
                        false
                    )
                )
            }
        }

    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemViewHolder -> {
                holder.onBind(position, holder)
//                val currentItem = productItemList?.get(position)
//
//                holder.binding.tvOriginalPrice.setPaintFlags(0)
//                Glide.with(mContext)
//                    .load(if (currentItem?.image != null && currentItem.image != "") currentItem.image else Constants.strNoImage)
//                    .listener(object : RequestListener<Drawable> {
//                        override fun onResourceReady(
//                            resource: Drawable?,
//                            model: Any?,
//                            target: Target<Drawable>?,
//                            dataSource: DataSource?,
//                            isFirstResource: Boolean
//                        ): Boolean {
//                            return false
//                        }
//
//                        override fun onLoadFailed(
//                            e: GlideException?,
//                            model: Any?,
//                            target: Target<Drawable>?,
//                            isFirstResource: Boolean
//                        ): Boolean {
//                            return false
//                        }
//                    })
//                    .transition(DrawableTransitionOptions.withCrossFade(800))
//                    .into(holder.binding.imageViewProduct)
//
//
//
//                holder.binding.tvItemName.text = currentItem?.name
//
//                holder.binding.tvBrand.visibility = View.GONE
//
//
//                if (currentItem?.final_price != currentItem?.price) {
//                    holder.binding.tvDiscountedPrice.visibility = View.VISIBLE
//                    holder.binding.tvDiscountedPrice.text =
//                        (Utils.getPriceFormatted(currentItem?.final_price,selectedCurrency))
//
//                    val discount = 100 - ceil(
//                        (Float.parseFloat(currentItem?.final_price) / Float.parseFloat(
//                            currentItem?.price
//                        ) * 100).toDouble()
//                    )
////                    holder.binding.txtDiscount.visibility = View.VISIBLE
////                    userViewHolder.itemView.txtDiscount.text = String.format(
////                        Locale.ENGLISH,
////                        "%.0f",
////                        discount
////                    ) + "% " + mContext.resources.getString(R.string.off)
////                    holder.binding.txtDiscount.text =
////                        "(" + String.format(Locale.ENGLISH, "%.0f", discount) + "%)"
//                    holder.binding.tvOriginalPrice.paintFlags =
//                        holder.binding.tvOriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
//                } else {
////                    holder.binding.txtDiscount.visibility = View.GONE
//                    holder.binding.tvDiscountedPrice.visibility = View.GONE
//                }
//
//
//                try {
//
//                    if (currentItem?.price != null) {
//                        var strPrice =
//                            currentItem.price
//                        strPrice = strPrice?.replace("٫", ".")?.trim()
//                        strPrice = strPrice?.replace(",", "")?.trim()
//                        if (strPrice!!.contains(" ")) {
//                            strPrice = strPrice.split(" ")[0]
//                        }
//                        holder.binding.tvOriginalPrice.text =
//                            Utils.getPriceFormatted(strPrice,selectedCurrency)
//
//                    }
//                } catch (e: Exception) {
////                currentItem.price = currentItem.final_price
//                }
//                try {
//                    currentItem?.arrListColors = ArrayList()
//                    if (!currentItem?.configurable_option!!.isNullOrEmpty()) {
//                        currentItem?.configurable_option?.forEach {
//                            if (!it.type.isNullOrEmpty()) {
//                                if (it.type.lowercase() == "color") {
//                                    if (!it.attributes.isNullOrEmpty()) {
//                                        it.attributes.forEach {
//                                            if (!it.color_code.isNullOrEmpty()) {
//                                                currentItem.arrListColors!!.add(it.color_code.toString())
//                                            }
//                                        }
//
//                                    }
//                                }
//                            }
//                        }
//
//                    } else {
//                        //println("Here i am colors 888")
//                    }
//
//                    if (currentItem.arrListColors!!.size > 0) {
//                        val layoutManagerGlobal =
//                            LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false)
//                        holder.binding.rcyColorSwatch.layoutManager = layoutManagerGlobal
//                        val colorAdapter =
//                            ProductColorSwatchAdapter(mContext, currentItem.arrListColors!!)
//                        holder.binding.rcyColorSwatch.adapter = colorAdapter
//                        holder.binding.rcyColorSwatch.visibility = View.VISIBLE
//                    } else {
//                        holder.binding.rcyColorSwatch.visibility = View.INVISIBLE
//                    }
//                } catch (e: Exception) {
//                    //println("Here i am exception colors " + e.localizedMessage)
//                    e.printStackTrace()
//                }
//                holder.binding.imageViewProduct.setOnClickListener {
//                    onItemClick?.invoke(currentItem!!)
////                    productListInterface?.onProductClickEvent(currentItem!!)
//                    val intent = Intent(mContext, OmniProductDetailsActivity::class.java)
//                    intent.putExtra("id", currentItem?.id.toString())
//                    intent.putExtra("name", currentItem?.name)
//                    intent.putExtra("image", currentItem?.image)
//                    //intent.putExtra("size_guide", strSizeGuide)
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        holder.binding.imageViewProduct.transitionName = "imageproduct"
//                        val options: ActivityOptionsCompat =
//                            ActivityOptionsCompat.makeSceneTransitionAnimation(
//                                (mContext),
//                                holder.binding.imageViewProduct,
//                                "imageproduct"
//                            )
//                        mContext.startActivity(intent, options.toBundle())
//                    } else {
//                        mContext.startActivity(intent)
//                    }
//                }
//
//                holder.binding.imageViewSeeLook.setOnClickListener {
////                    productListInterface?.onSeeTheLook(currentItem!!)
//                    val intent = Intent(mContext, MatchWithActivity::class.java)
//                    intent.putExtra("id", currentItem?.id.toString())
//                    intent.putExtra("name", currentItem?.name)
//                    intent.putExtra("image", currentItem?.image)
//                    //intent.putExtra("size_guide", strSizeGuide)
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        holder.binding.imageViewProduct.transitionName = "imageproduct"
//                        val options: ActivityOptionsCompat =
//                            ActivityOptionsCompat.makeSceneTransitionAnimation(
//                                (mContext),
//                                holder.binding.imageViewProduct,
//                                "imageproduct"
//                            )
//                        mContext.startActivity(intent, options.toBundle())
//                    } else {
//                        mContext.startActivity(intent)
//                    }
//            }
            }
            is HeaderViewHolder -> {
                holder.onBind(position)
            }
            is LoadingViewHolder -> {

            }
            else -> {
                val viewHolder = holder as HeaderViewHolder
                viewHolder.onBind(position)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        var column_size: Int = VIEW_TYPE_ITEM
        try {
            column_size =
                if (homeModel != null && homeModel!!.has_banner != null && homeModel!!.has_banner.equals(
                        "1"
                    ) && position == 0
                )
                    VIEW_TYPE_HEADER
                else if (position == productItemList!!.size - 1 && isLoadingAdded)
                    VIEW_TYPE_FOOTER
                else if (productItemList!![position].has_custom_options != null && productItemList!![position].has_custom_options!!)
                    VIEW_TYPE_HEADER
                else
                    VIEW_TYPE_ITEM

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return column_size
    }

    override fun getItemCount(): Int {
        if (productItemList == null)
            return 0
        return productItemList?.size!!
    }


    inner class ItemViewHolder(val binding: LayoutProductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(position: Int, holder: ItemViewHolder) {
            try {
                val currentItem = productItemList?.get(position)
                var productWidth =
                    (screenWidth / 2) - (mContext.resources.getDimension(R.dimen.ten_dp)).toInt()
                var productGrid =
                    ((screenWidth / 2) - mContext.resources.getDimension(R.dimen.ten_dp)).toInt()
//            if (layout.equals("List", true)) {
//                productWidth =
//                    (screenWidth) - (mContext.resources.getDimension(R.dimen.ten_dp)).toInt()
//                productGrid =
//                    ((screenWidth) - mContext.resources.getDimension(R.dimen.ten_dp)).toInt()
//            }
                var productheight = (productWidth * 1.4).toInt()
                val listItemW =
                    screenWidth - (2 * (mContext.resources.getDimension(R.dimen.ten_dp))).toInt()
                var listItemH = listItemW
                var textWidth =
                    ((screenWidth / 2) - mContext.resources.getDimension(R.dimen.ten_dp)).toInt()

                val margin5 = mContext.resources.getDimension(R.dimen.five_dp).toInt()
                var margin7 = mContext.resources.getDimension(R.dimen.seven_dp).toInt()
                var margin10 = mContext.resources.getDimension(R.dimen.ten_dp).toInt()
                val margin3 = mContext.resources.getDimension(R.dimen.corner_radius).toInt()
//
                val listWidth = (screenWidth)
                //listHeight = (constant * 510)
                var listHeight = (listWidth * 1.4).toInt()

                var gridWidth = screenWidth / 2
//            if (layout.equals("List", true))
//                gridWidth = screenWidth

                //gridHeight = (constant * 510) / 2
                val gridHeight = (gridWidth * 1.4).toInt()

                ///image height width
                val relParams = RelativeLayout.LayoutParams(gridWidth, gridHeight)
                val relParamMain = RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                val m = position + 1
                if (m % 2 == 0) {
                    relParamMain.marginEnd = margin3
                } else {
                    relParamMain.marginStart = margin3
                    relParamMain.marginEnd = margin3
                }

                relParamMain.bottomMargin = margin5
                binding.root.layoutParams = relParamMain

                binding.imageViewProduct.layoutParams = relParams
//            binding.root.layoutParams = relParams

                holder.binding.tvOriginalPrice.setPaintFlags(0)
                Glide.with(mContext)
                    .load(if (currentItem?.image != null && currentItem.image != "") currentItem.image else Constants.strNoImage)
                    .listener(object : RequestListener<Drawable> {
                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }
                    })
                    .transition(DrawableTransitionOptions.withCrossFade(800))
                    .into(holder.binding.imageViewProduct)



                holder.binding.tvItemName.text = currentItem?.name

                holder.binding.tvBrand.visibility = View.GONE


                if (currentItem?.final_price != currentItem?.price) {
                    holder.binding.tvDiscountedPrice.visibility = View.VISIBLE
                    holder.binding.tvDiscountedPrice.text =
                        (Utils.getPriceFormatted(currentItem?.final_price, selectedCurrency))

                    val discount = 100 - ceil(
                        (Float.parseFloat(currentItem?.final_price) / Float.parseFloat(
                            currentItem?.price
                        ) * 100).toDouble()
                    )
//                    holder.binding.txtDiscount.visibility = View.VISIBLE
//                    userViewHolder.itemView.txtDiscount.text = String.format(
//                        Locale.ENGLISH,
//                        "%.0f",
//                        discount
//                    ) + "% " + mContext.resources.getString(R.string.off)
//                    holder.binding.txtDiscount.text =
//                        "(" + String.format(Locale.ENGLISH, "%.0f", discount) + "%)"
                    holder.binding.tvOriginalPrice.paintFlags =
                        holder.binding.tvOriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else {
//                    holder.binding.txtDiscount.visibility = View.GONE
                    holder.binding.tvDiscountedPrice.visibility = View.GONE
                }


                try {

                    if (currentItem?.price != null) {
                        var strPrice =
                            currentItem.price
                        strPrice = strPrice?.replace("٫", ".")?.trim()
                        strPrice = strPrice?.replace(",", "")?.trim()
                        if (strPrice!!.contains(" ")) {
                            strPrice = strPrice.split(" ")[0]
                        }
                        holder.binding.tvOriginalPrice.text =
                            Utils.getPriceFormatted(strPrice, selectedCurrency)

                    }
                } catch (e: Exception) {
                currentItem?.price = currentItem?.final_price
                }

//            Glide.with(mContext)
//                .load(if (currentItem?.image != null && currentItem.image != "") currentItem.image else Constants.strNoImage)
//                .listener(object : RequestListener<Drawable> {
//                    override fun onResourceReady(
//                        resource: Drawable?,
//                        model: Any?,
//                        target: Target<Drawable>?,
//                        dataSource: DataSource?,
//                        isFirstResource: Boolean
//                    ): Boolean {
//                        return false
//                    }
//
//                    override fun onLoadFailed(
//                        e: GlideException?,
//                        model: Any?,
//                        target: Target<Drawable>?,
//                        isFirstResource: Boolean
//                    ): Boolean {
//                        return false
//                    }
//                })
//                .transition(DrawableTransitionOptions.withCrossFade(800))
//                .into(binding.imageViewProduct)
//
//            binding.tvItemName.text = currentItem?.name
//
//            binding.tvBrand.visibility = View.GONE
//
//
//            if (currentItem?.final_price != null) {
//                binding.tvDiscountedPrice.text =
//                    (Utils.getPriceFormatted(currentItem?.final_price, selectedCurrency))
//                holder.binding.tvOriginalPrice.paintFlags =
//                    holder.binding.tvOriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
//            }
//
//
//            try {
//
//                if (currentItem?.price != null) {
//                    var strPrice =
//                        currentItem.price
//                    strPrice = strPrice?.replace("٫", ".")?.trim()
//                    strPrice = strPrice?.replace(",", "")?.trim()
//                    if (strPrice!!.contains(" ")) {
//                        strPrice = strPrice.split(" ")[0]
//                    }
//                    binding.tvOriginalPrice.text =
//                        Utils.getPriceFormatted(strPrice, selectedCurrency)
//
//                }
//            } catch (e: Exception) {
////                currentItem.price = currentItem.final_price
//            }

//                    val discount = 100 - ceil(
//                        (Float.parseFloat(currentItem.final_price) / Float.parseFloat(
//                            bcurrentItem.price
//                        ) * 100).toDouble()
//                    )
//                    userViewHolder.itemView.txtDiscount.text = String.format(
//                        Locale.ENGLISH,
//                        "%.0f",
//                        discount
//                    ) + "% " + mContext.resources.getString(R.string.off)
//                    userViewHolder.itemView.txtDiscountPrice.text =
//                        "(" + String.format(Locale.ENGLISH, "%.0f", discount) + "%)"
//                    // println("Display discount: " + String.format(Locale.ENGLISH, "%.0f", discount) + "% ")

                holder.binding.toggleWishlist.setOnClickListener {
                    Toast.makeText(mContext, "Pressed", Toast.LENGTH_SHORT).show()
                }

                holder.binding.root.setOnClickListener {

                    productListInterface?.onProductClickEvent(currentItem!!)
                    val intent = Intent(mContext, OmniProductDetailsActivity::class.java)
                    intent.putExtra("id", currentItem?.id.toString())
                    intent.putExtra("name", currentItem?.name)
                    intent.putExtra("image", currentItem?.image)
                    //intent.putExtra("size_guide", strSizeGuide)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        binding.imageViewProduct.transitionName = "imageproduct"
                        val options: ActivityOptionsCompat =
                            ActivityOptionsCompat.makeSceneTransitionAnimation(
                                (mContext),
                                binding.imageViewProduct,
                                "imageproduct"
                            )
                        mContext.startActivity(intent, options.toBundle())
                    } else {
                        mContext.startActivity(intent)
                    }
                }


                holder.binding.imageViewSeeLook.setOnClickListener {
//                    Toast.makeText(mContext, "Look clicked", Toast.LENGTH_SHORT).show()
                    productListInterface?.onSeeTheLook(currentItem!!)
                    val intent = Intent(mContext, MatchWithActivity::class.java)
                    intent.putExtra("id", currentItem?.id.toString())
                    intent.putExtra("name", currentItem?.name)
                    intent.putExtra("image", currentItem?.image)
                    //intent.putExtra("size_guide", strSizeGuide)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        binding.imageViewProduct.transitionName = "imageproduct"
                        val options: ActivityOptionsCompat =
                            ActivityOptionsCompat.makeSceneTransitionAnimation(
                                (mContext),
                                binding.imageViewProduct,
                                "imageproduct"
                            )
                        mContext.startActivity(intent, options.toBundle())
                    } else {
                        mContext.startActivity(intent)
                    }
                }

//            val id = helper.getItemIdFromWishList(currentItem.id)
//            productArrList!![i].is_wishList = id != ""
//            //println("wishlist id :: $id")
//            userViewHolder.itemView.togWishlist.isChecked =
//                productArrList!![i].is_wishList ?: false
//            //println("wishlist id boolean :: ${productArrList!![i].is_wishList}")
//            userViewHolder.itemView.togWishlist.setOnClickListener {
//                val strWishFinalPrice = currentItem.final_price
//                val strWishProductID = currentItem.id
//                if (AppController.instance.isLoggedIn) {
//                    productArrList!![i].is_wishList =
//                        !helper.getItemIdFromWishList(currentItem.id).isNullOrEmpty()
//                    if (productArrList!![i].is_wishList == true) {
//                        //println("wishlist id DELETE :: $id")
//                        val strWishItemID = helper.getItemIdFromWishList(currentItem.id)
//                        productListInterface?.toggleWishList(
//                            "Delete",
//                            strWishItemID.toString(),
//                            i,
//                            productArrList
//                        )
//                    } else {
//                        productListInterface?.toggleWishList(
//                            "Add",
//                            strWishProductID.toString(),
//                            i,
//                            productItemList
//                        )
//                    }
//                } else {
//                    holder.binding.toggleWishlist.isChecked = false
//                    val intent = Intent(mContext, LoginActivity::class.java)
//                    mContext.startActivity(intent)
//                }
//            }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    inner class LoadingViewHolder(val footerItemBinding: FooterItemBinding) :
        RecyclerView.ViewHolder(footerItemBinding.root) {

        init {
            val margin10 = mContext.resources.getDimension(R.dimen.ten_dp).toInt()
            val paramView =
                footerItemBinding.root.layoutParams as RecyclerView.LayoutParams
            paramView.width = screenWidth.toFloat().roundToInt()
            // paramView.rightMargin = margin10 + margin10
        }
    }

    inner class HeaderViewHolder(val headerItemBinding: HeaderItemBinding) :
        RecyclerView.ViewHolder(headerItemBinding.root) {
        init {

            val paramView =
                headerItemBinding.root.layoutParams as RecyclerView.LayoutParams
            paramView.width = screenWidth.toFloat().roundToInt()
        }

        fun onBind(position: Int) {
            val currentItem = productItemList?.get(position)
            if (homeModel != null && homeModel!!.has_banner != null && homeModel!!.has_banner.equals(
                    "1"
                )
            ) {
                headerItemBinding.progressBar?.visibility = View.VISIBLE
                val small_param =
                    RelativeLayout.LayoutParams(fullWidth.toInt(), banner_height)
                headerItemBinding.imgBanner.layoutParams = small_param
                headerItemBinding.imgBanner.visibility = View.VISIBLE

                if (homeModel?.banner != null && (homeModel?.banner?.contains(".gif") == true)) {
                    Utils.loadGifUsingCoilWithSize(
                        mContext,
                        if (homeModel?.banner != null && !homeModel?.banner.equals("")) homeModel!!.banner else Constants.strNoImage,
                        headerItemBinding.imgBanner,
                        fullWidth.toInt(),
                        banner_height
                    )
                    Glide.with(mContext)
                        .asGif()
                        .load(if (homeModel?.banner != null && !homeModel?.banner.equals("")) homeModel!!.banner else Constants.strNoImage)
                        .override(fullWidth.toInt(), banner_height)
                        .into(headerItemBinding.imgBanner)
                } else {

                    if (banner_height > 0) {

                        Glide.with(mContext)
                            .load(
                                if (homeModel!!.banner != null && !homeModel!!.banner.equals(
                                        ""
                                    )
                                ) homeModel!!.banner else Constants.strNoImage
                            )
                            .override(fullWidth.toInt(), banner_height)
                            .listener(object : RequestListener<Drawable> {

                                override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    headerItemBinding.progressBar.visibility = View.GONE
                                    return false
                                }

                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    headerItemBinding.progressBar.visibility = View.GONE
                                    return false
                                }
                            })
                            .into(headerItemBinding.imgBanner)

                    }
                }

                //----------------------------------------set price and name details
//                headerItemBinding.tvOriginalPrice.paintFlags =
//                    headerItemBinding.tvOriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
//                if (currentItem?.name?.isNullOrEmpty()!!)
//                    headerItemBinding.tvItemName.text = ""
//                else
//                    headerItemBinding.tvItemName.visibility=View.GONE
//
//                headerItemBinding.tvBrand.visibility = View.GONE
//
//                val priceDouble = currentItem?.final_price?.toDouble()
//                if (priceDouble != null && priceDouble != 0.0) {
//                    headerItemBinding.tvDiscountedPrice.text =
//                        (Utils.getPriceFormatted(currentItem?.final_price))
//                    try {
//
//                        if (currentItem?.price != null) {
//                            var strPrice =
//                                currentItem.price
//                            strPrice = strPrice?.replace("٫", ".")?.trim()
//                            strPrice = strPrice?.replace(",", "")?.trim()
//                            if (strPrice!!.contains(" ")) {
//                                strPrice = strPrice.split(" ")[0]
//                            }
//                            headerItemBinding.tvOriginalPrice.text =
//                                Utils.getPriceFormatted(strPrice)
//                        }
//                    } catch (e: Exception) {
////                currentItem.price = currentItem.final_price
//                    }
//                } else {
////                    headerItemBinding.tvOriginalPrice.text = ""
////                    headerItemBinding.tvDiscountedPrice.text = ""
////                    headerItemBinding.txtDiscount.text = ""
//                    headerItemBinding.tvOriginalPrice.visibility=View.GONE
//                    headerItemBinding.tvDiscountedPrice.visibility=View.GONE
//                    headerItemBinding.txtDiscount.visibility=View.GONE
//                }


                //______________________________________________________________________

//                headerItemBinding.root.setOnClickListener {
//
//                    productListInterface?.onProductClickEvent(currentItem!!)
//                    val intent = Intent(mContext, ProductDetailsActivity::class.java)
//                    intent.putExtra("id", currentItem?.id.toString())
//                    intent.putExtra("name", currentItem?.name)
//                    intent.putExtra("image", currentItem?.image)
//                    //intent.putExtra("size_guide", strSizeGuide)
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        headerItemBinding.imageViewProduct.transitionName = "imageproduct"
//                        val options: ActivityOptionsCompat =
//                            ActivityOptionsCompat.makeSceneTransitionAnimation(
//                                (mContext),
//                                headerItemBinding.imageViewProduct,
//                                "imageproduct"
//                            )
//                        mContext.startActivity(intent, options.toBundle())
//                    } else {
//                        mContext.startActivity(intent)
//                    }
//                }


                headerItemBinding.root.setOnClickListener()
                {

                    if (!currentItem?.id.isNullOrEmpty() && currentItem?.id != "null"
                    ) {
                        val intent = Intent(mContext, OmniProductDetailsActivity::class.java)
                        intent.putExtra("id", currentItem?.id)
                        intent.putExtra("cat_id", strId)
                        intent.putExtra("name", currentItem?.name)
//                        intent.putExtra("size_guide", currentItem)
                        //intent.putExtra("image", productArrList!![i].image_url)
                        mContext.startActivity(intent)
                    }

                }

            } else if (currentItem?.has_custom_options == true) {
                headerItemBinding.progressBar.visibility = View.GONE
                if (currentItem?.hasMultipleBannerInOne == true) {
                    //If there are multiple banner in same index
                    //println("Multiple banner :: " + i)
                    val image_Height: Double =
                        (currentItem?.custom_options?.toInt()?.times((density)))
                            ?: 0.0
                    headerItemBinding.relMain.visibility = View.GONE
                    (headerItemBinding.rvDoubleProducts.layoutParams as RelativeLayout.LayoutParams).height =
                        image_Height.toInt()
                    headerItemBinding.rvDoubleProducts.visibility = View.VISIBLE
                    headerItemBinding.rvDoubleProducts.layoutManager =
                        androidx.recyclerview.widget.GridLayoutManager(
                            mContext!!,
                            2
                        )
                    headerItemBinding.rvDoubleProducts.adapter = MultipleBannerAdapter(
                        currentItem?.arrListMultipleProduct,
                        mContext
                    )
                    headerItemBinding.rvDoubleProducts.adapter?.notifyDataSetChanged()
                } else {
                    //println("Single banner :: " + i)

                    headerItemBinding.progressBar.visibility = View.VISIBLE
                    headerItemBinding.rvDoubleProducts.visibility = View.GONE
                    headerItemBinding.relMain.visibility = View.VISIBLE
                    val image_Height: Double =
                        (currentItem.custom_options.toInt() * (density))
                    val small_param =
                        RelativeLayout.LayoutParams(fullWidth.toInt(), image_Height.toInt())
                    headerItemBinding.imgBanner.layoutParams = small_param
                    headerItemBinding.imgBanner.visibility = View.VISIBLE

                    if (currentItem?.mediaType == "V") {
                        if (!currentItem.mediaFile.isNullOrEmpty()) {
                            headerItemBinding.myVideo.layoutParams = small_param
                            headerItemBinding.myVideo.addMediaPlayerListener(object :
                                SimpleMainThreadMediaPlayerListener() {
                                override fun onVideoCompletionMainThread() {

                                }

                                override fun onVideoStoppedMainThread() {

                                }

                                override fun onVideoPreparedMainThread() {
                                    headerItemBinding.myVideo.visibility = View.VISIBLE
                                    headerItemBinding.progressBar.visibility = View.GONE
                                }
                            })
                            mVideoPlayerManager.playNewVideo(
                                null,
                                headerItemBinding.myVideo,
                                currentItem.mediaFile
                            )
                        }
                    } else {
                        if (currentItem?.image != null && currentItem?.image?.contains(
                                ".gif"
                            ) == true
                        ) {
                            Utils.loadGifUsingCoilWithSize(
                                mContext,
                                if (currentItem?.image != null && !currentItem?.image.equals(
                                        ""
                                    )
                                )
                                    currentItem?.image
                                else
                                    Constants.strNoImage,
                                headerItemBinding.imgBanner,
                                fullWidth.toInt(),
                                banner_height
                            )
                            Glide.with(mContext)
                                .asGif()
                                .load(
                                    if (currentItem?.image != null && !currentItem?.image.equals(
                                            ""
                                        )
                                    ) currentItem?.image else Constants.strNoImage
                                )
                                .override(fullWidth.toInt(), image_Height.toInt())
                                .into(headerItemBinding.imgBanner)
                        } else {
                            //println("Here i am banner images 111 " + productArrList!![i].image)
                            Utils.loadImagesUsingCoilWithSize(
                                mContext,
                                if (currentItem?.image != null && !currentItem?.image.equals(
                                        ""
                                    )
                                ) currentItem?.image else Constants.strNoImage,
                                headerItemBinding.imgBanner,
                                fullWidth.toInt(),
                                image_Height.toInt()
                            )
                            Glide.with(mContext).load(
                                if (currentItem?.image != null && !currentItem?.image.equals(
                                        ""
                                    )
                                ) currentItem?.image else Constants.strNoImage
                            )
                                .override(fullWidth.toInt(), image_Height.toInt())
                                .into(headerItemBinding.imgBanner)
                        }
                    }

                    headerItemBinding.root.setOnClickListener()
                    {
                        favPost = position
                        //println("Here i am banner images clicked 222 " + productArrList!![i].image)
                        //println("Here i am is product clicked 222  " + productArrList!![i].entity_id)
                        if (!currentItem?.id.isNullOrEmpty() && currentItem?.id != "null"
                        ) {
//                            val bundle = Bundle()
//                            bundle.putExtra("id", currentItem?.id)
//                            bundle.putExtra("cat_id", strId)
//                            bundle.putExtra("name", currentItem?.name)
//                            bundle.putExtra("size_guide", strSizeGuide)
//                            val intent = Intent(mContext, ProductDetailActivity::class.java)
                            //intent.putExtra("image", productArrList!![i].image_url)todo
                        }

                    }

                    //
                    //
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        headerItemBinding.progressAssociateRvProduct.backgroundTintList = null
                        headerItemBinding.progressAssociateRvProduct.progressTintList =
                            ColorStateList.valueOf(mContext.resources.getColor(R.color.black))

                        headerItemBinding.progressAssociateRvProduct.progressBackgroundTintList =
                            ColorStateList.valueOf(mContext.resources.getColor(R.color.tab_unselected_color))
                    }

                    headerItemBinding.rvAssociateProducts.setOnScrollListener(object :
                        RecyclerView.OnScrollListener() {
                        override fun onScrolled(
                            recyclerView: RecyclerView,
                            dx: Int,
                            dy: Int
                        ) {
                            super.onScrolled(recyclerView, dx, dy)
                            val offset = recyclerView.computeHorizontalScrollOffset()
                            val extent = recyclerView.computeHorizontalScrollExtent()
                            val range = recyclerView.computeHorizontalScrollRange()

                            val percentage = 100.0f * offset / (range - extent).toFloat()
                            headerItemBinding.progressAssociateRvProduct.apply {
                                max = 100
                                progress = percentage.toInt()
                            }
                        }
                    })

                    if (currentItem?.show_container_grid == 1 && !currentItem?.associated_products.isNullOrEmpty()
                    ) {
                        headerItemBinding.rvAssociateProducts.visibility = View.VISIBLE
                        if (currentItem?.associated_products?.size!! > 3) {
                            headerItemBinding.progressAssociateRvProduct.visibility =
                                View.VISIBLE
                        }
                        val layoutManager = LinearLayoutManager(
                            mContext,
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                        if (currentItem?.container_width.isNullOrEmpty()) {
                            currentItem?.container_width = "0"
                        }
                        val adapter = AssociateProductsAdapter(
                            selectedCurrency,
                            currentItem?.associated_products,
                            currentItem?.container_width?.toDouble(),
                            currentItem?.final_price,
                            strId,
                            mContext
                        )
                        headerItemBinding.rvAssociateProducts.layoutManager =
                            layoutManager
                        headerItemBinding.rvAssociateProducts.adapter = adapter
                    } else {
                        headerItemBinding.rvAssociateProducts.visibility = View.GONE
                        headerItemBinding.progressAssociateRvProduct.visibility =
                            View.GONE
                    }

                }
            } else {
                headerItemBinding.progressBar.visibility = View.GONE
                headerItemBinding.imgBanner.visibility = View.GONE
            }

            headerItemBinding.imgBanner.setAdjustViewBounds(true)
        }

    }

//    fun updateFav() {
//        if (appController.is_favourite) {
//            if (productArrList!![favPost].wishlist_item_id == 0) {
//                productArrList!![favPost].wishlist_item_id = 1
//            } else {
//                productArrList!![favPost].wishlist_item_id = 0
//            }
//
//            appController.is_favourite = false
//        }
//        notifyDataSetChanged()
//    }

    private fun add(model: ProductListMDataModel) {
        productItemList!!.add(model)
        notifyItemInserted(productItemList!!.size - 1)
    }

    fun addAll(mcList: java.util.ArrayList<ProductListMDataModel>) {

        for (mc in java.util.ArrayList<ProductListMDataModel>(
            mcList
        )) {
            add(mc)
        }
    }

    fun updateList(list: ArrayList<ProductListMDataModel>) {
        productItemList = list
        notifyDataSetChanged()
    }

    private fun remove(model: ProductListMDataModel?) {
        val position = productItemList!!.indexOf(model)
        if (position > -1) {
            productItemList!!.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        isLoadingAdded = false
        while (itemCount > 0) {
            remove(getItem(0))
        }
    }

    fun addLoadingFooter() {
        if (!isLoadingAdded) {
            isLoadingAdded = true
            add(
                ProductListMDataModel(
                    barcode = null,
                    brand = null,
                    configurable_option = null,
                    description = null,
                    enable_special_text = null,
                    final_price = null,
                    has_options = null,
                    id = null,
                    image = null,
                    is_salable = null,
                    name = null,
                    options = listOf(),
                    ordered_qty = null,
                    price = null,
                    remaining_qty = null,
                    sale_img = null,
                    sale_img_h = null,
                    sale_img_w = null,
                    short_description = null,
                    sku = null,
                    special_text = null,
                    type = null,
                    wishlist_item_id = null,
                    is_wishList = null,
                    hasMargin = null,
                    has_custom_options = null,
                    arrListColors = null,
                    mediaType = null,
                    mediaFile = null,
                    custom_options = "",
                    associated_products = null,
                    show_container_grid = null,
                    container_width = null,
                    hasMultipleBannerInOne = null,
                    arrListMultipleProduct = null
                )
            )
        }
    }

    fun removeLoadingFooter() {
        if (isLoadingAdded) {
            isLoadingAdded = false
            val position = productItemList!!.size - 1
            val item = getItem(position)

            if (item != null) {
                productItemList!!.removeAt(position)
                notifyItemRemoved(position)
            }
        }
    }

    private fun getItem(position: Int): ProductListMDataModel? {
        return productItemList!![position]
    }

}