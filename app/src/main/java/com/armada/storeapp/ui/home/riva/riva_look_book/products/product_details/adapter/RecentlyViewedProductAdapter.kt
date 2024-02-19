//package com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.adapter
//
//import android.app.Activity
//import android.graphics.Bitmap
//import android.graphics.Paint
//import android.os.Build
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.*
//import androidx.core.app.ActivityOptionsCompat
//import androidx.core.content.ContextCompat
//import coil.request.ImageRequest
//import coil.request.ImageResult
//import coil.transition.CrossfadeTransition
//import com.armada.riva.ProductDetail.ProductDetailActivity
//import java.util.*
//import kotlin.collections.ArrayList
//
//class RecentlyViewedProductAdapter {
//    ///recent products adapter
//    private inner class RecentProductAdapter(
//        private val activity: Activity,
//        private val arrlist: ArrayList<RecentlyViewModel>
//    ) : androidx.recyclerview.widget.RecyclerView.Adapter<RecentProductAdapter.MyViewHolder>() {
//        private var amazingPrice = 0.0
//        var colorFinalPrice = 0
//        var colorRegularPrice = 0
//        var colrAmazingPrice = 0
//
//        init {
//            amazingPrice = AppController.instance.getAmazingPrice()
//            colorFinalPrice = ContextCompat.getColor(activity, R.color.price_black_color)
//            colorRegularPrice = ContextCompat.getColor(activity, R.color.regular_price)
//            colrAmazingPrice = ContextCompat.getColor(activity, R.color.final_price)
//
//        }
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
//            val v =
//                LayoutInflater.from(parent.context).inflate(R.layout.related_items, parent, false)
//            return MyViewHolder(v)
//        }
//
//        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//
//            val userViewHolder = holder
//            val bm = arrlist[position]
//            userViewHolder.togWishList.tag = position
//            userViewHolder.spinProgress.start()
//            userViewHolder.spinProgress.recreateWithParams(
//                this@ProductDetailActivity,
//                DialogUtils.getColor(this@ProductDetailActivity, R.color.black),
//                120,
//                true
//            )
//
//            val small_param = RelativeLayout.LayoutParams(recentWidth, recentHeight)
//            val small_param_half = RelativeLayout.LayoutParams(productWidthHalf, recentHeight)
//
//            val small_pa1 =
//                RelativeLayout.LayoutParams(recentWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
//            val small_pal_half =
//                RelativeLayout.LayoutParams(productWidthHalf, ViewGroup.LayoutParams.WRAP_CONTENT)
//
//            ////////////////////////////////////////////////////////////////////////////////////////
//
//            if (!this@ProductDetailActivity.isFinishing) {
//                try {
//                    userViewHolder.imgProduct.load(if (bm.image_url != null && bm.image_url != "") bm.image_url else Global.strNoImage) {
//                        crossfade(true)
//                        crossfade(800)
//                        allowConversionToBitmap(true)
//                        bitmapConfig(Bitmap.Config.ARGB_8888)
//                        allowHardware(true)
//                        listener(object : ImageRequest.Listener {
//                            override fun onSuccess(
//                                request: ImageRequest,
//                                metadata: ImageResult.Metadata
//                            ) {
//                                super.onSuccess(request, metadata)
//                                userViewHolder.spinProgress.visibility = View.GONE
//                                userViewHolder.spinProgress.stop()
//                            }
//                        })
//                        transition(CrossfadeTransition())
//                    }
//
////                    Glide.with(this@ProductDetailActivity)
////                        .load(if (bm.image != null && bm.image != "") bm.image else Global.strNoImage)
////                        .override(recentWidth, recentHeight)
////                        .listener(object : RequestListener<Drawable> {
////
////
////                            override fun onResourceReady(
////                                resource: Drawable?,
////                                model: Any?,
////                                target: Target<Drawable>?,
////                                dataSource: DataSource?,
////                                isFirstResource: Boolean
////                            ): Boolean {
////                                userViewHolder.spinProgress.visibility = View.GONE
////                                userViewHolder.spinProgress.stop()
////                                return false
////                            }
////
////                            override fun onLoadFailed(
////                                e: GlideException?,
////                                model: Any?,
////                                target: Target<Drawable>?,
////                                isFirstResource: Boolean
////                            ): Boolean {
////                                return false
////                            }
////                        })
////                        .transition(DrawableTransitionOptions.withCrossFade(800))
////                        .into(userViewHolder.imgProduct)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                } catch (e: OutOfMemoryError) {
//                    e.printStackTrace()
//                }
//            }
//
//            ////////////////////////////////////////////////////////////////////////////////////////
//
//            if (arrlist.size > 2) {
//                small_pa1.topMargin = margin10
//                userViewHolder.rltMain.layoutParams = small_pa1
//                userViewHolder.imgProduct.layoutParams = small_param
//                userViewHolder.relImage.layoutParams = small_param
//            } else {
//                small_pa1.topMargin = margin10
//                userViewHolder.rltMain.layoutParams = small_pal_half
//                userViewHolder.imgProduct.layoutParams = small_param_half
//                userViewHolder.relImage.layoutParams = small_param_half
//            }
//
//            if (AppController.instance.isLangArebic) {
//                small_pa1.leftMargin = margin10
//                small_pal_half.leftMargin = margin10
//            } else {
//                small_pa1.rightMargin = margin10
//                small_pal_half.rightMargin = margin10
//            }
//
//            userViewHolder.togWishList.isChecked = bm.is_wishlist
//
//            userViewHolder.togWishList.setOnClickListener {
//
//                strWishFinalPrice = bm.final_price
//                strWishProductID = bm.entity_id
//
//                if (AppController.instance.isLoggedIn) {
//                    if (bm.is_wishlist) {
//                        strWishItemID = helper!!.getItemIdFromWishList(bm.entity_id)
//                        wishListToggle("Delete", bm.name, strWishProductID, strWishFinalPrice)
//                        userViewHolder.togWishList.isChecked = false
//                        bm.is_wishlist = false
//                    } else {
//                        wishListToggle("Add", bm.name, strWishProductID, strWishFinalPrice)
//                        userViewHolder.togWishList.isChecked = true
//                        bm.is_wishlist = true
//                    }
//                } else {
//                    userViewHolder.togWishList.isChecked = false
//                    val intent = Intent(this@ProductDetailActivity, LoginActivity::class.java)
//                    startActivity(intent)
//                }
//            }
//
//            val param = RelativeLayout.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                resources.getDimension(R.dimen.thirty_dp).toInt()
//            )
//            param.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.imgProduct)
//            param.bottomMargin = resources.getDimension(R.dimen.ten_dp).toInt()
//
//            userViewHolder.relSold.setPadding(
//                resources.getDimension(R.dimen.seven_dp).toInt(),
//                margin5, margin7, margin5
//            )
//
//            if (AppController.instance.isLangArebic) {
//                param.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
//            } else {
//                param.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
//            }
//
//            userViewHolder.relSold.layoutParams = param
//
//            /*  userViewHolder.txtProductName.text = (bm.name.split(' ').joinToString(" ") {
//                  it.replaceFirstChar {
//                      if (it.isLowerCase()) it.titlecase(
//                          Locale.getDefault()
//                      ) else it.toString()
//                  }
//              })*/
//
//            /*if (bm.final_price != null && !bm.final_price.isEmpty()) {
//                if (bm.final_price.toDouble() <= amazingPrice)
//                    userViewHolder.txtProductPrice.setTextColor(colrAmazingPrice)
//                else userViewHolder.txtProductPrice.setTextColor(colorFinalPrice)
//
//                userViewHolder.txtProductPrice.text = (AppController.instance.getPriceFormatted(bm.final_price))
//            }
//
//            if (bm.final_price != null && bm.regular_price != null) {
//                if (bm.regular_price.equals(bm.final_price)) {
//                    userViewHolder.txtDiscounts.visibility = View.GONE
//                } else {
//                    userViewHolder.txtDiscounts.visibility = View.VISIBLE
//                    val discount = 100 - Math.ceil((java.lang.Float.parseFloat(bm.final_price) / java.lang.Float.parseFloat(bm.regular_price) * 100).toDouble())
//                    userViewHolder.txtDiscounts.text = String.format(Locale.ENGLISH, "%.0f", discount) + "% " + resources.getString(R.string.off)
//                }
//            } else {
//                userViewHolder.txtDiscounts.visibility = View.GONE
//            }
//
//            userViewHolder.txtDiscounts.visibility = View.GONE
//            userViewHolder.txtProductPrice.typeface = typeSemiBold
//            userViewHolder.txtProductName.typeface = typeNormal
//            userViewHolder.txtRegularPrice.typeface = typeNormal
//            userViewHolder.txtDiscounts.alpha = 0.8.toFloat()*/
//            userViewHolder.txtRegularPrice.paintFlags =
//                userViewHolder.txtRegularPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
//            userViewHolder.txtProductName.text = (bm.name.split(' ').joinToString(" ") {
//                it.replaceFirstChar {
//                    if (it.isLowerCase()) it.titlecase(
//                        Locale.getDefault()
//                    ) else it.toString()
//                }
//            })
//
//            if (bm.final_price != null && !bm.final_price.isEmpty())
//                userViewHolder.txtProductPrice.text =
//                    (AppController.instance.getPriceFormatted(bm.final_price))
//
//            try {
//
//                if (bm.regular_price != null && !bm.regular_price.isEmpty()) {
//                    var strPrice = bm.regular_price
//                    strPrice = strPrice.replace("٫", ".").trim()
//                    strPrice = strPrice.replace(",", "").trim()
//                    //strPrice = strPrice.replace("$", "").trim()
//                    if (strPrice.contains(" ")) {
//                        strPrice = strPrice.split(" ")[0]
//                    }
//                    bm.regular_price = strPrice
//                    userViewHolder.txtRegularPrice.text =
//                        AppController.instance.getPriceFormattedWithoutCurrency(strPrice)
//                }
//            } catch (e: Exception) {
//                bm.regular_price = bm.final_price
//            }
//
//            //println("Here i am recent products 11   " + bm.name)
//            if (bm.final_price != null && bm.regular_price != null) {
//                //println("Here i am recent products 22   " + bm.name)
//                if ((bm.regular_price).toDouble() != (bm.final_price).toDouble()) {
//                    //println("Here i am recent products 33   " + bm.name)
//                    //userViewHolder.txtProductPrice.setTextColor(colorFinalPrice)
//                    userViewHolder.txtProductPrice.typeface = typeSemiBold
//                    userViewHolder.txtRegularPrice.visibility = View.VISIBLE
//                    userViewHolder.txtDiscounts.visibility = View.GONE
//                    userViewHolder.txtDiscountPrice.visibility = View.VISIBLE
//                    val discount = 100 - Math.ceil(
//                        (java.lang.Float.parseFloat(bm.final_price) / java.lang.Float.parseFloat(bm.regular_price) * 100).toDouble()
//                    )
//                    userViewHolder.txtDiscounts.text = String.format(
//                        Locale.ENGLISH,
//                        "%.0f",
//                        discount
//                    ) + "% " + resources.getString(R.string.off)
//                    userViewHolder.txtDiscountPrice.text =
//                        "(" + String.format(Locale.ENGLISH, "%.0f", discount) + "%)"
//                    // println("Display discount: " + String.format(Locale.ENGLISH, "%.0f", discount) + "% ")
//                    //println("Here i am recent products 444   " + discount)
//                } else {
//                    //println("Here i am recent products 55   " + bm.name)
//                    userViewHolder.txtProductPrice.typeface = typeSemiBold
//                    userViewHolder.txtDiscountPrice.visibility = View.GONE
//                    //userViewHolder.txtProductPrice.setTextColor(colorRegularPrice)
//                    userViewHolder.txtRegularPrice.visibility = View.GONE
//
//                }
//
//                if (bm.final_price != null && !bm.final_price.isEmpty() && bm.final_price.toDouble() <= amazingPrice)
//                    userViewHolder.txtProductPrice.setTextColor(colrAmazingPrice)
//
//            } else {
//                //println("Here i am recent products 666   " + bm.name)
//                userViewHolder.txtProductPrice.typeface = typeSemiBold
//                //userViewHolder.txtProductPrice.setTextColor(colorRegularPrice)
//                //userViewHolder.txtRegularPrice.setTextColor(colorRegularPrice)
//                userViewHolder.txtRegularPrice.visibility = View.GONE
//                userViewHolder.txtDiscounts.visibility = View.GONE
//                userViewHolder.txtDiscountPrice.visibility = View.GONE
//            }
//
//            userViewHolder.txtDiscounts.visibility = View.GONE
//            //userViewHolder.txtProductPrice.typeface = typeBold
//            userViewHolder.txtProductName.typeface = typeNormal
//            userViewHolder.txtRegularPrice.typeface = typeBold
//            userViewHolder.txtDiscountPrice.typeface = typeBold
//            userViewHolder.txtSale.typeface = typeSemiBold
//            userViewHolder.txtSold.typeface = typeSemiBold
//
//            userViewHolder.txtDiscountPrice.includeFontPadding = false
//            userViewHolder.txtProductPrice.includeFontPadding = false
//            userViewHolder.txtRegularPrice.includeFontPadding = false
//
//            userViewHolder.txtDiscounts.alpha = 0.8.toFloat()
//            userViewHolder.txtDiscounts.visibility = View.GONE
//
//            /*   if (bm.is_salable != null && bm.is_salable.equals("0")) {
//                   userViewHolder.relSold.visibility = View.VISIBLE
//               } else {
//                   userViewHolder.relSold.visibility = View.GONE
//               }*/
//
//
//            userViewHolder.txtSale.visibility = View.GONE
//
//            /*if (bm.show_sale_badge!=null && bm.show_sale_badge==1)
//                userViewHolder.txtSale.visibility=View.VISIBLE
//            else userViewHolder.txtSale.visibility=View.GONE*/
//
//            userViewHolder.relSold.alpha = 0.7.toFloat()
//            userViewHolder.txtSold.typeface = typeMedium
//
//            userViewHolder.itemView.setOnClickListener()
//            {
//                val intent = Intent(this@ProductDetailActivity, ProductDetailActivity::class.java)
//                //println("Here i am product details item clicked 222")
//                intent.putExtra("id", bm.entity_id)
//                intent.putExtra("name", bm.name)
//                intent.putExtra("image", bm.image_url)
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    userViewHolder.imgProduct.transitionName = "imageproduct"
//                    val options: ActivityOptionsCompat =
//                        ActivityOptionsCompat.makeSceneTransitionAnimation(
//                            this@ProductDetailActivity,
//                            userViewHolder.imgProduct,
//                            "imageproduct"
//                        )
//                    startActivity(intent, options.toBundle())
//                } else {
//                    startActivity(intent)
//                }
//            }
//        }
//
//        override fun getItemCount(): Int {
//            return arrlist.size
//        }
//
//        private inner class MyViewHolder(itemView: View) :
//            androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
//
//            val txtProductName: TextView
//            val txtSold: TextView
//            val txtProductPrice: TextView
//            val txtRegularPrice: TextView
//            val imgProduct: ImageView
//            val relMain: RelativeLayout
//            val relText: RelativeLayout
//            val relImage: RelativeLayout
//            val rltMain: RelativeLayout
//            val relSold: RelativeLayout
//            val togWishList: ToggleButton
//            val txtDiscounts: TextView
//            val relloading: ProgressBar
//            val spinProgress: CamomileSpinner
//            val txtSale: TextView
//            val txtDiscountPrice: TextView
//
//            init {
//                imgProduct = itemView.findViewById(R.id.imgProduct) as ImageView
//                txtProductName = itemView.findViewById(R.id.txtProductName) as TextView
//                txtSold = itemView.findViewById(R.id.txtSold) as TextView
//                txtProductPrice = itemView.findViewById(R.id.txtPrice) as TextView
//                txtRegularPrice = itemView.findViewById(R.id.txtRegularPrice) as TextView
//                relMain = itemView.findViewById(R.id.linMainLayout) as RelativeLayout
//                relImage = itemView.findViewById(R.id.relImage) as RelativeLayout
//                rltMain = itemView.findViewById(R.id.rltmain) as RelativeLayout
//                relText = itemView.findViewById(R.id.relText) as RelativeLayout
//                relloading = itemView.findViewById(R.id.progress) as ProgressBar
//                txtDiscounts = itemView.findViewById(R.id.txtDiscount) as TextView
//                togWishList = itemView.findViewById(R.id.togWishlist) as ToggleButton
//                relSold = itemView.findViewById(R.id.relSold) as RelativeLayout
//                spinProgress = itemView.findViewById(R.id.spin) as CamomileSpinner
//                txtSale = itemView.findViewById(R.id.txtSale) as TextView
//                txtDiscountPrice = itemView.findViewById(R.id.txtDiscountPrice) as TextView
//            }
//        }
//    }
//
