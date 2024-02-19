package com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import com.armada.storeapp.R
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.request.AddToCartRequest
import com.armada.storeapp.data.model.request.ConfigRequestModel
import com.armada.storeapp.data.model.response.*
import com.armada.storeapp.ui.base.BaseActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.bag.BagActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing.match_with.MatchWithAddToCartBottomSheetDialog
import com.armada.storeapp.ui.utils.ConnectionDetector
import com.armada.storeapp.ui.utils.Constants
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import com.armada.storeapp.ui.utils.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.ImageViewTarget
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.custom_alert.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

@SuppressLint("StaticFieldLeak")
object AddToCartBottomSheetDialog {
    private var selectedLanguage: String = "en"
    private var selectedCurrency = "USD"
    lateinit var sharedpreferenceHandler: SharedpreferenceHandler
    lateinit var productDetailsViewModel: ProductDetailsViewModel
    private var strUserId = ""
    private var boolView = false
    private var screenWidth: Int = 0
    private var screenHeight: Int = 0
    private var arrList: ArrayList<ProductDetailsData>? = null
    private var arrListValues: ArrayList<String>? = null
    private var arrListType: ArrayList<String>? = null
    private var arrListSingleValue: ArrayList<String>? = null
    private var strConfigImage = ""
    private var strConfigType: String? = ""
    private var strProductName = ""
    private var strProductIdReusable = ""
    private var strProductId = ""
    private var strProductImage = ""
    private var strPrice = ""
    var isFirstAttSelected = true

    var scannedItem: ScannedItemDetailsResponse? = null

    //    var modelReset: ProductDetailsDataModel? = null
    private var arrListResetOption: ArrayList<ProductDetailsConfigurableOption>? = null
    private var strConfigTypeReusable = ""
    private var strProductReusable = ""
    private var colorPosition: Int = -1
    private var sizePosition: Int = -1
    var model: Related? = null
    var relLoadingBottom: RelativeLayout? = null
    var txtBottomFinalPrice: TextView? = null
    var txtBottomRegularPrice: TextView? = null
    var txtBottomDiscountPrice: TextView? = null

    private var boolSingleItem = true
    private var strAttributeId = ""
    private var strOptionId = ""
    private var globalPost: Int? = 0
    private var arrListOptionsGlobal: ArrayList<ProductDetailsConfigurableOption>? = null
    private var progressBar: ProgressBar? = null
    var btmSheetConfig: BottomSheetDialog? = null
    private var txtCart: TextView? = null
    private var txtNotifyMe: TextView? = null
    private var txtAddToCart: TextView? = null
    private var txtOverlay: TextView? = null
    private var txtSoldOut: TextView? = null
    private var linCustomOpt: LinearLayout? = null
    private var parentRootView: NestedScrollView? = null
    private var relLoading: RelativeLayout? = null
    private var txtSelect: TextView? = null
    private var txtClose: TextView? = null

    private var shakeAnim: Animation? = null
    private var strConfigProdId = ""
    private var cd: ConnectionDetector? = null
    private var intRmngQty = 1
    var strParentId = ""

    private var arrLstArrTextConfig: ArrayList<ArrayList<TextView>>? = null
    private var arrLstArrLayoutConfig: ArrayList<ArrayList<LinearLayout>>? = null
    private var arrLstArrTypeConfig: ArrayList<ArrayList<String>>? = null
    private var arrLstArrConfigBool: ArrayList<ArrayList<Boolean>>? = null
    private var mainActivity: OmniProductDetailsActivity? = null
    private var loading: Dialog? = null
    private var singleColor = false
    private var singleSize = false

    var currentSku = ""

    @SuppressLint("StaticFieldLeak")
    fun showProduct(
        activity: Activity,
        viewmodel: ProductDetailsViewModel,
        bm: Related,
        showBack: Boolean,
        nestedScrollView: NestedScrollView
    ) {
        parentRootView = nestedScrollView
        mainActivity = activity as OmniProductDetailsActivity
        productDetailsViewModel = viewmodel
        val btmSheetProduct: BottomSheetDialog?
        model = bm
        sharedpreferenceHandler = SharedpreferenceHandler(mainActivity!!)
        strUserId = sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_USER_ID, "")!!
        selectedLanguage =
            sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_SELECTED_COUNTRY, "en")!!
        selectedCurrency =
            sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_SELECTED_CURRENCY, "USD")!!
        cd = ConnectionDetector(mainActivity)
        strProductId = bm.id.toString()
        strParentId = bm.id.toString()
        strProductImage = bm.image.toString()

        colorPosition = -1
        sizePosition = -1

        if (!boolView) {
            boolView = true
            val parentView = activity.layoutInflater.inflate(R.layout.dialog_product, null)
            val btmSheetProduct = BottomSheetDialog(activity)
            btmSheetProduct.setContentView(parentView)
            btmSheetProduct.setCanceledOnTouchOutside(true)
            btmSheetProduct.setCancelable(true)

            (parentView.parent as View).setBackgroundColor(Color.TRANSPARENT)

            val mBottomSheetBehavior = BottomSheetBehavior.from(parentView.parent as View)
            mBottomSheetBehavior.peekHeight = screenHeight

            val relProduct = parentView.findViewById(R.id.relProduct) as RelativeLayout
            relProduct.visibility = View.VISIBLE
            val imageProduct = parentView.findViewById(R.id.imgProduct) as ImageView
            txtBottomFinalPrice = parentView.findViewById(R.id.txtFinalPrice) as TextView
            txtBottomRegularPrice = parentView.findViewById(R.id.txtRegularPrice) as TextView
            txtBottomDiscountPrice = parentView.findViewById(R.id.txtDiscountPrice) as TextView
            val txtProductName = parentView.findViewById(R.id.txtProductName) as TextView
            //  togWishListProduct = parentView.findViewById(R.id.togWishlist) as ToggleButton
            val imgBack = parentView.findViewById(R.id.imgBack) as ImageView
            val imgClose = parentView.findViewById(R.id.imgClose) as ImageView
//            val txtDetails = parentView.findViewById(R.id.txtDetails) as TextView


            ///setting image size
            val imgWidth = ((screenWidth / 4) + 130).toInt()
            val imgHeight = (imgWidth * 1.4).toInt()

            if (showBack) imgBack.visibility = View.VISIBLE
            else imgBack.visibility = View.INVISIBLE

            txtSoldOut = parentView.findViewById(R.id.txtSoldOut) as TextView
            txtCart = parentView.findViewById(R.id.txtCart) as TextView

            // relImage = parentView.findViewById(R.id.relImage) as RelativeLayout
            relLoading = parentView.findViewById(R.id.relLoading) as RelativeLayout

            val paramLoad = RelativeLayout.LayoutParams(screenWidth, imgHeight + 120)
            relLoading!!.layoutParams = paramLoad


            val imageSize = RelativeLayout.LayoutParams(imgWidth, imgHeight)
            imageSize.addRule(RelativeLayout.CENTER_HORIZONTAL)
            imageProduct.layoutParams = imageSize

            strProductId = bm.id.toString()
            strParentId = bm.id.toString()

            loadProductDetail(bm, bm.id.toString())

            txtProductName.text = bm.name
            txtProductName.isAllCaps = true

            txtBottomRegularPrice!!.paintFlags =
                txtBottomRegularPrice!!.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG


            Glide.with(activity)
                .load(if (bm.image != null && bm.image != "") "http:" + bm.image else Constants.strNoImage)
                .override(imgWidth, imgHeight)
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
                .into(imageProduct)


            txtCart!!.setOnClickListener { btmSheetConfig!!.show() }

            ////product details
            imageProduct.setOnClickListener {
                //                (mActivity as TimeStoryFragmentActivity).onBackPressed()
//                val intent = Intent(mActivity, ProductDetailActivity::class.java)
//                intent.putExtra("id", strParentId)
//                intent.putExtra("cat_id", "765")
//                intent.putExtra("name", strProductName)
//                intent.putExtra("size_guide", "")
//                startActivity(intent)
            }
            ///

            /*    ////product details
                txtDetails.setOnClickListener {
                    (mActivity as TimeStoryFragmentActivity).onBackPressed()
                    val intent = Intent(mActivity, ProductDetailActivity::class.java)
                    intent.putExtra("id", strParentId)
                    intent.putExtra("cat_id", "765")
                    intent.putExtra("name", strProductName)
                    intent.putExtra("size_guide", "")
                    startActivity(intent)
                }
                ///

                imgClose.setOnClickListener {
                    btmSheetProduct!!.dismiss()
                    if (btmSheetList != null && btmSheetList!!.isShowing) {
                        btmSheetList!!.dismiss()
                    }
                }*/

            imgBack.setOnClickListener {
                btmSheetProduct.dismiss()
            }
            ///////////////////////////////////////////////////////////////////////////////////

            /*      btmSheetProduct!!.setOnDismissListener {

                      boolSwiped = true
                      if (btmSheetList != null && !btmSheetList!!.isShowing) {
                          // storiesProgressView!!.resume()

                          if (storiesProgressView!!.isStarted) {
                              val now = System.currentTimeMillis()
                              storiesProgressView!!.resumeView()
                              limit < now - pressTime
                          } else {
                              storiesProgressView!!.startStories(0)
                          }

                          boolSwiped = false

                      } else if (btmSheetList == null) {
                          //storiesProgressView!!.resume()

                          if (storiesProgressView!!.isStarted) {
                              val now = System.currentTimeMillis()
                              storiesProgressView!!.resumeView()
                              limit < now - pressTime
                          } else {
                              storiesProgressView!!.startStories(0)
                          }

                          boolSwiped = false
                      }
                      bool_view = false


                      val count = lnrVideos!!.childCount
                      for (c in 0 until count) {
                          val currentView = lnrVideos!!.getChildAt(c)
                          if (currentView != null) {
                              val video = currentView.findViewById(R.id.videoTimeline) as BetterVideoPlayer
                              video?.start()
                          }
                      }

                  }*/
            //btmSheetProduct!!.show()

        }

//        println("Here i am click match 222")

//        loadProductDetail(bm.entity_id.toString(), mCompositeDisposable, globalClass, activity)
//        showConfig(bm, activity, globalClass)
    }

    private fun loadProductDetail(
        bm: Related,
        strProdId: String
    ) {
        productDetailsViewModel.getMatchProductDetails(selectedLanguage, selectedCurrency, bm.id)
        productDetailsViewModel.responseMAtchProductDetails.observe(
            mainActivity!!
        ) {
            when (it) {
                is Resource.Success -> {
                    dismissProgress()
                    if (it.data?.status == 200) {
                        val productData = it.data.data
                        currentSku = productData?.sku
                        arrListValues = ArrayList()
                        arrListType = ArrayList()
                        showConfig(
                        )

                        if ((productData?.configurable_option?.size ?: 0) > 0) {
                            arrListResetOption = productData?.configurable_option
                            strConfigTypeReusable = strConfigType!!
                            //println("Here i am set config 333")

                            if (productData != null) {
                                setConfig(
                                    mainActivity!!,
                                    productData.configurable_option as ArrayList<ProductDetailsConfigurableOption>,
                                    productData,
                                    "Config",
                                    linCustomOpt!!
                                )
                            }
                        } else {
                            Utils.showSnackbar(
                                parentRootView!!,
                                mainActivity!!.resources.getString(R.string.stock_note)
                            )
                        }
                        dismissProgress()
                    } else if (it.data?.status.toString() == "500") {

                        /*txtCart!!.visibility=View.GONE
                        txtSoldOut!!.visibility=View.VISIBLE*/

                        //println("Error :" + result[0].message)

                        Utils.showSnackbar(
                            parentRootView!!,
                            mainActivity!!.resources.getString(R.string.stock_note)
                        )

                        if (btmSheetConfig != null)
                            btmSheetConfig!!.dismiss()

                    }
                }

                is Resource.Error -> {
                    Utils.showSnackbar(parentRootView!!, "Requested product could not found")
                    dismissProgress()
                }
                is Resource.Loading -> {
                    showProgress()
                }
            }
        }
    }

    fun setConfig(
        activity: Activity,
        arrListConfig: ArrayList<ProductDetailsConfigurableOption>,
        poductModel: ProductDetailsData,
        strFrom: String,
        linCustomOpt: LinearLayout
    ) {

        if (arrListConfig.size > 0) {
            txtOverlay?.visibility = View.VISIBLE
            txtAddToCart?.visibility = View.GONE
            txtNotifyMe!!.visibility = View.GONE
            linCustomOpt.removeAllViews()

            arrLstArrTextConfig = ArrayList()
            arrLstArrTypeConfig = ArrayList()
            arrLstArrConfigBool = ArrayList()
            arrLstArrLayoutConfig = ArrayList()

            boolSingleItem = true
            for (k in 0 until arrListConfig.size) {
                if (boolSingleItem) {
                    if ((arrListConfig[k].attributes?.size
                            ?: 0) > 1
                    ) {          ///// It will check for size of attributs options if
                        ///// its 1 for all it will show selected default
                        boolSingleItem = false
                        break
                    }
                }
            }

            txtSelect?.text =
                activity.resources.getString(R.string.select) + " " + arrListConfig.joinToString("/") {
                    it.type?.trim().toString()
                }
            for (i in 0 until arrListConfig.size) {
                val inflater1 = LayoutInflater.from(activity)
                val view = inflater1.inflate(R.layout.item_custom_options_matched_products, null)
                val textCustom = view.findViewById(R.id.txtCustom) as TextView
                val linItem = view.findViewById(R.id.linItems) as LinearLayout

                textCustom.text = arrListConfig.get(i).type?.trim()


                arrListValues!!.add(arrListConfig[i].type.toString())
                arrListType!!.add(arrListConfig[i].type.toString())

                linItem.id = i
                linItem.removeAllViews()

                val arrLstTxtColor = ArrayList<TextView>()
                val arrLstTypeColor = ArrayList<String>()
                val arrLstLayoutColor = ArrayList<LinearLayout>()
                val arrLstBool = ArrayList<Boolean>()

                for (j in 0 until (arrListConfig[i].attributes?.size ?: 0)) {

                    val textView = TextView(activity)
                    var strType = ""

                    var llp = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )

                    val layout = LinearLayout(activity)
                    layout.layoutParams = llp
                    layout.removeAllViews()

                    if (!arrListConfig[i].attribute_code.equals("color", true)) {

                        if (j == 0) {
                            llp.setMargins(
                                0,
                                0,
                                activity.resources.getDimension(R.dimen.ten_dp).toInt(),
                                0
                            )
                        } else {
                            llp.setMargins(
                                0,
                                0,
                                activity.resources.getDimension(R.dimen.ten_dp).toInt(),
                                0
                            )
                        }

                        textView.text = arrListConfig.get(i).attributes?.get(j)?.value
                        //println("Here i am color name  " + arrListConfig.get(i).attributes?.get(j)?.value)
                        textView.layoutParams = llp
                        layout.layoutParams = llp
                        textView.gravity = Gravity.CENTER
                        textView.setTextSize(
                            TypedValue.COMPLEX_UNIT_PX,
                            activity.resources.getDimension(R.dimen.eleven_textsize)
                        )
                        textView.maxLines = 1

                        // layout.addView(textView)
//                        textView.setPadding(
//                            activity.resources.getDimension(R.dimen.five_dp).toInt(),
//                            activity.resources.getDimension(R.dimen.five_dp).toInt(),
//                            activity.resources.getDimension(R.dimen.five_dp).toInt(),
//                            activity.resources.getDimension(R.dimen.five_dp).toInt()
//                        )
                        textView.id = j
                        strType = "Size"


                    } else {

                        val llpLayout = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        llp = LinearLayout.LayoutParams(
                            activity.resources.getDimension(R.dimen.thirty_dp).toInt(),
                            activity.resources.getDimension(R.dimen.thirty_dp).toInt()
                        )

                        llpLayout.setMargins(
                            0,
                            0,
                            activity.resources.getDimension(R.dimen.ten_dp).toInt(),
                            0
                        )


                        val image = ImageView(activity)     //Dummy To suppo
                        image.layoutParams = llpLayout
//                        layout.setPadding(
//                            activity.resources.getDimension(R.dimen.item_image_margin).toInt(),
//                            activity.resources.getDimension(R.dimen.item_image_margin).toInt(),
//                            activity.resources.getDimension(R.dimen.item_image_margin).toInt(),
//                            activity.resources.getDimension(R.dimen.item_image_margin).toInt()
//                        )

                        //   layout.addView(textView)
                        layout.layoutParams = llpLayout
                        strType = "Color"
                        textView.layoutParams = llp

                        textView.id = j

                        if (!(activity).isFinishing) {
                            Glide.with(activity)
                                .asBitmap()
                                .load(arrListConfig[i].attributes?.get(j)?.attribute_image_url)
                                .override(
                                    activity.resources.getDimension(R.dimen.twenty_five_dp).toInt(),
                                    activity.resources.getDimension(R.dimen.twenty_five_dp).toInt()
                                )
                                .listener(object : RequestListener<Bitmap> {

                                    override fun onResourceReady(
                                        resource: Bitmap?,
                                        model: Any?,
                                        target: Target<Bitmap>?,
                                        dataSource: DataSource?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        val bitmap = getRoundedCornerBitmap(resource!!, 600)
                                        val imTarget = target as ImageViewTarget<*>
                                        val drawable: Drawable =
                                            BitmapDrawable(activity.resources, bitmap)
                                        textView.background = drawable
                                        /* return DrawableCrossFadeFactory<Drawable>()
                                                 .build(isFromMemoryCache, isFirstResource)
                                                 .animate(BitmapDrawable(imTarget.view.resources, bitmap), imTarget)*/
                                        return false
                                    }

                                    override fun onLoadFailed(
                                        e: GlideException?,
                                        model: Any?,
                                        target: Target<Bitmap>?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        return false
                                    }
                                })
                                .into(image)
                        }
                    }

                    layout.addView(textView)

                    if (!boolSingleItem || !poductModel.type.equals("configurable", true)) {

                        if (i == 0) setUnSelectedState(
                            textView,
                            strType,
                            layout
                        )
                        else setDisableState(textView, strType, layout)

                        arrLstBool.add(false)
                        txtAddToCart!!.visibility = View.GONE
                        txtOverlay!!.visibility = View.VISIBLE
                        txtNotifyMe!!.visibility = View.GONE
                    } else {
                        for (t in 0 until arrListConfig.size) {
                            if (arrListConfig[t].type.equals(arrListConfig[0].type)) {
                                arrListValues?.set(
                                    t, arrListConfig[t].attributes?.get(0)?.value
                                        ?: ""
                                )
                            }
                        }
                        arrLstBool.add(true)

                        if (!poductModel.type.equals("configurable", true)) {
                            setSelectedState(textView, strType, layout)
                            txtAddToCart!!.visibility = View.VISIBLE
                            txtOverlay!!.visibility = View.GONE
                            txtNotifyMe!!.visibility = View.GONE
                        } else {
                            setUnSelectedState(textView, strType, layout)
                        }
                    }

                    arrLstTxtColor.add(textView)
                    arrLstTypeColor.add(strType)
                    arrLstLayoutColor.add(layout)

                    //println("progress visibility" + progressBar?.visibility)
                    //println("progressS visibility" + progressBarStatic?.visibility)

                    /*else{*/
                    textView.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(v: View) {

                            if (progressBar?.visibility == View.VISIBLE) {
                                return
                            }
                            val parentPosition = linItem.id
                            val position = v.id

                            if (arrListConfig[parentPosition].attribute_code.equals(
                                    "color",
                                    true
                                )
                            ) {

                                if (position != colorPosition) {
                                    colorPosition = position
                                    sizePosition = -1

                                    setSelectedColor(
                                        parentPosition,
                                        position
                                    )

                                    if (arrListConfig[parentPosition].attribute_code.equals(
                                            "color",
                                            true
                                        )
                                    ) {

//                                        if (arrListConfig[parentPosition].attributes?.get(position)?.images != null && (arrListConfig[parentPosition].attributes?.get(
//                                                position
//                                            )?.images?.size
//                                                ?: 0) > 0
//                                        ) {
//
//                                            pager.removeAllViews()
//                                            //pager.addOnPageChangeListener(null)
//                                            pager.clearOnPageChangeListeners()
//                                            pager.adapter = ImagePagerAdapter(
//                                                arrListConfig[parentPosition].attributes?.get(
//                                                    position
//                                                )?.images as ArrayList<String>
//                                            )
//                                            pager.adapter!!.notifyDataSetChanged()
//
//                                            strConfigImage =
//                                                arrListConfig[parentPosition].attributes?.get(
//                                                    position
//                                                )?.images?.get(0)
//                                                    ?: ""
//
//
//                                            /* if (AppController.instance.isLangArebic) {
//                                                 pager.currentItem = arrListConfig.get(parent_position).attributes.get(position).images.size
//                                             } else {
//                                                 pager.currentItem = 0
//                                             }
//
//                                             pager.currentItem = 0
//                                             viewPagerIndicator.setVisibleDotCounts(7)
//                                             viewPagerIndicator.setNoOfPages(arrListConfig.get(parent_position).attributes.get(position).images.size)*/
//
////                                            if (AppController.instance.isLangArebic) {
////                                                pager.currentItem = arrListConfig[parentPosition].attributes?.get(position)?.images?.size
////                                                        ?: 0
////                                                arrListConfig[parentPosition].attributes?.get(position)?.images?.reverse()
////                                            } else {
////                                                pager.currentItem = 0
////                                            }
//                                            pager.currentItem = 0
//                                            viewPagerIndicator.resetPageNo()
//                                            viewPagerIndicator.visibleDotCounts = 7
//                                            viewPagerIndicator.noOfPages =
//                                                arrListConfig[parentPosition].attributes?.get(
//                                                    position
//                                                )?.images?.size
//                                                    ?: 0
//                                            viewPagerIndicator.onPageChange(pager.currentItem)
//
//
//                                            pager.addOnPageChangeListener(object :
//                                                ViewPager.OnPageChangeListener {
//
//                                                override fun onPageScrollStateChanged(state: Int) {
//                                                }
//
//                                                override fun onPageScrolled(
//                                                    position: Int,
//                                                    positionOffset: Float,
//                                                    positionOffsetPixels: Int
//                                                ) {
//                                                }
//
//                                                override fun onPageSelected(position: Int) {
//
//                                                    if (pager.currentItem >= 0)
//                                                        viewPagerIndicator.onPageChange(pager.currentItem)
//                                                    else viewPagerIndicator.onPageChange(0)
//
//                                                    /* if (position > viewPagerIndicator.noOfPages - 1) {
//                                                         viewPagerIndicator.onPageChange(viewPagerIndicator.noOfPages - 1)
//                                                     } else {
//                                                         viewPagerIndicator.onPageChange(position)
//                                                     }*/
//                                                }
//                                            })
//                                        }
                                    }

                                    for (t in 0 until arrListConfig.size) {
                                        if (arrListConfig[t].type == arrListConfig[parentPosition].type) {
                                            arrListValues!!.set(
                                                t,
                                                arrListConfig.get(t).attributes?.get(position)?.value
                                                    ?: ""
                                            )
                                            if (arrListConfig[t].is_last != null && arrListConfig[t].is_last != "Yes") {
                                                if (strConfigType!!.contains(",")) {
                                                    strConfigType = strConfigType?.replace(
                                                        (arrListConfig.get(t).type + ",").toRegex(),
                                                        ""
                                                    )
                                                } else {
                                                    strConfigType = strConfigType?.replace(
                                                        arrListConfig.get(t).type?.toRegex() as Regex,
                                                        ""
                                                    )
                                                }

                                                if (t == arrListConfig.size - 1) {
                                                    strConfigType =
                                                        strConfigType!!.replace(" ".toRegex(), "")
                                                }

                                            } else {
                                                strConfigType = ""
                                            }
                                        }
                                    }
                                    resetChildViewsToUnsel(
                                        parentPosition,
                                        arrListConfig
                                    )
                                    setAttributeOptions(
                                        parentPosition,
                                        arrListConfig,
                                        false
                                    )

                                }
                            } else if (arrListConfig[parentPosition].attribute_code.equals(
                                    "size",
                                    true
                                )
                            ) {

                                if (position != sizePosition) {
                                    sizePosition = position

                                    setSelectedColor(
                                        parentPosition,
                                        position
                                    )
                                    for (t in 0 until arrListConfig.size) {
                                        if (arrListConfig[t].type == arrListConfig[parentPosition].type) {
                                            arrListValues?.set(
                                                t, arrListConfig[t].attributes?.get(position)?.value
                                                    ?: ""
                                            )
                                            if (arrListConfig[t].is_last != null && arrListConfig[t].is_last != "Yes") {
                                                if (strConfigType!!.contains(",")) {
                                                    strConfigType = strConfigType?.replace(
                                                        (arrListConfig[t].type + ",").toRegex(),
                                                        ""
                                                    )
                                                } else {
                                                    strConfigType = strConfigType?.replace(
                                                        arrListConfig[t].type?.toRegex() as Regex,
                                                        ""
                                                    )
                                                }

                                                if (t == arrListConfig.size - 1) {
                                                    strConfigType =
                                                        strConfigType?.replace(" ".toRegex(), "")
                                                }
                                            } else {
                                                strConfigType = ""
                                            }
                                        }
                                    }
                                    resetChildViewsToUnsel(
                                        parentPosition,
                                        arrListConfig
                                    )
                                    setAttributeOptions(
                                        parentPosition,
                                        arrListConfig,
                                        false
                                    )
                                }
                            }
                        }
                    })
                    // }

                    linItem.addView(layout)
                }
                arrLstArrTextConfig!!.add(arrLstTxtColor)
                arrLstArrTypeConfig!!.add(arrLstTypeColor)
                arrLstArrLayoutConfig!!.add(arrLstLayoutColor)
                arrLstArrConfigBool!!.add(arrLstBool)

                linCustomOpt.addView(view)
            }
        } else {
            txtOverlay!!.visibility = View.GONE
            txtAddToCart!!.visibility = View.VISIBLE
            txtNotifyMe!!.visibility = View.GONE
        }

        if (arrListConfig.size > 0 && strFrom == "Config") {

            for (i in 0 until arrListConfig.size) {
                if (arrListConfig[i].attribute_code.equals(
                        "color",
                        true
                    ) && arrListConfig[i].attributes?.size == 1
                ) {
                    colorPosition = 0
                    sizePosition = -1
                    setSelectedColor(i, 0)
                    if (arrListConfig[0].type.equals("color", true)) {

                        if (arrListConfig[0].attributes?.get(i)?.images != null && (arrListConfig[0].attributes?.get(
                                i
                            )?.images?.size
                                ?: 0) > 0
                        ) {

//                            pager.removeAllViews()
//                            pager.clearOnPageChangeListeners()
//                            pager.adapter =
//                                ImagePagerAdapter(arrListConfig[0].attributes?.get(i)?.images as ArrayList<String>)
//                            pager.adapter!!.notifyDataSetChanged()

                            /* if (AppController.instance.isLangArebic) {
                                 pager.currentItem = arrListConfig.get(0).attributes.get(i).images.size
                             } else {
                                 pager.currentItem = 0
                             }

                             viewPagerIndicator.setNoOfPages(arrListConfig.get(0).attributes.get(i).images.size)
                             viewPagerIndicator.setVisibleDotCounts(7)
                             viewPagerIndicator.onPageChange(0)*/


//                            if (AppController.instance.isLangArebic) {
//                                pager.currentItem = arrListConfig[0].attributes?.get(i)?.images?.size
//                                        ?: 0
//                                arrListConfig[0].attributes?.get(i)?.images?.reverse()
//                            } else {
//                                pager.currentItem = 0
//                            }

//                            pager.currentItem = 0
//                            viewPagerIndicator.resetPageNo()
//                            viewPagerIndicator.noOfPages =
//                                arrListConfig[0].attributes?.get(i)?.images?.size
//                                    ?: 0
//                            viewPagerIndicator.visibleDotCounts = 7
//                            viewPagerIndicator.onPageChange(pager.currentItem)

//                            pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
//
//                                override fun onPageScrollStateChanged(state: Int) {
//                                }
//
//                                override fun onPageScrolled(
//                                    position: Int,
//                                    positionOffset: Float,
//                                    positionOffsetPixels: Int
//                                ) {
//                                }
//
//                                override fun onPageSelected(position: Int) {
//                                    //viewPagerIndicator.onPageChange(position)
//
//                                    if (pager.currentItem >= 0)
//                                        viewPagerIndicator.onPageChange(pager.currentItem)
//                                    else viewPagerIndicator.onPageChange(0)
//                                }
//                            })
                        }
                    }

                    for (t in 0 until arrListConfig.size) {

                        if (arrListConfig[t].type.equals(arrListConfig[i].type)) {
                            arrListValues?.set(t, arrListConfig[t].attributes?.get(0)?.value ?: "")
                            if (arrListConfig[t].is_last != null && !arrListConfig[t].is_last.equals(
                                    "Yes"
                                )
                            ) {
                                if (strConfigType?.contains(",") == true) {
                                    strConfigType = strConfigType?.replace(
                                        (arrListConfig[t].type + ",").toRegex(),
                                        ""
                                    )
                                } else {
                                    strConfigType = strConfigType?.replace(
                                        arrListConfig[t].type?.toRegex() as Regex,
                                        ""
                                    )
                                }

                                if (t == arrListConfig.size - 1) {
                                    strConfigType = strConfigType?.replace(" ".toRegex(), "")
                                }
                            } else {
                                strConfigType = ""
                            }
                        }
                    }
                    resetChildViewsToUnsel(i, arrListConfig)
                    setAttributeOptions(
                        i,
                        arrListConfig,
                        false
                    )


                } else if (arrListConfig[i].attribute_code.equals(
                        "color",
                        true
                    ) && (arrListConfig.get(i).attributes?.size
                        ?: 0) > 1
                ) {
                    try {
                        for (l in 0 until (arrListConfig[i].attributes?.size ?: 0)) {
                            if (arrListConfig[i].attribute_code.equals(
                                    "color",
                                    true
                                ) && (arrListConfig[i].attributes?.get(l)?.should_select == true)
                            ) {

                                val position = l
                                if (position != colorPosition) {
                                    colorPosition = position
                                    sizePosition = -1

                                    setSelectedColor(0, position)

                                    if (arrListConfig[0].attributes?.get(position)?.images != null && (arrListConfig[0].attributes?.get(
                                            position
                                        )?.images?.size
                                            ?: 0) > 0
                                    ) {

//                                        pager.removeAllViews()
//                                        //pager.addOnPageChangeListener(null!!)
//                                        pager.clearOnPageChangeListeners()
//                                        pager.adapter = ImagePagerAdapter(
//                                            arrListConfig[0].attributes?.get(position)?.images as ArrayList<String>
//                                        )
//                                        pager.adapter!!.notifyDataSetChanged()


                                        strConfigImage =
                                            arrListConfig[0].attributes?.get(position)?.images?.get(
                                                0
                                            )
                                                ?: ""


                                        //println("Here i am cart image 111 " + strConfigImage)
                                        /*if (AppController.instance.isLangArebic) {
                                            pager.currentItem = arrListConfig.get(0).attributes.get(position).images.size
                                        } else {
                                            pager.currentItem = 0
                                        }

                                        pager.currentItem = 0
                                        viewPagerIndicator.setVisibleDotCounts(7)
                                        viewPagerIndicator.setNoOfPages(arrListConfig.get(0).attributes.get(position).images.size)*/

//                                        if (AppController.instance.isLangArebic) {
//                                            pager.currentItem = arrListConfig[0].attributes?.get(position)?.images?.size
//                                                    ?: 0
//                                            arrListConfig[0].attributes?.get(position)?.images?.reverse()
//                                        } else {
//                                            pager.currentItem = 0
//                                        }

//                                        pager.currentItem = 0
//                                        viewPagerIndicator.resetPageNo()
//                                        viewPagerIndicator.noOfPages =
//                                            arrListConfig[0].attributes?.get(position)?.images?.size
//                                                ?: 0
//                                        viewPagerIndicator.visibleDotCounts = 7
//                                        viewPagerIndicator.onPageChange(pager.currentItem)

//                                        pager.addOnPageChangeListener(object :
//                                            ViewPager.OnPageChangeListener {
//
//                                            override fun onPageScrollStateChanged(state: Int) {
//                                            }
//
//                                            override fun onPageScrolled(
//                                                position: Int,
//                                                positionOffset: Float,
//                                                positionOffsetPixels: Int
//                                            ) {
//                                            }
//
//                                            override fun onPageSelected(position: Int) {
//                                                if (pager.currentItem >= 0)
//                                                    viewPagerIndicator.onPageChange(pager.currentItem)
//                                                else viewPagerIndicator.onPageChange(0)
//                                            }
//                                        })
                                    }

                                    for (t in 0 until arrListConfig.size) {
                                        if (arrListConfig.get(t).type.equals(arrListConfig.get(0).type)) {
                                            arrListValues?.set(
                                                t,
                                                arrListConfig.get(t).attributes?.get(position)?.value
                                                    ?: ""
                                            )
                                            if (arrListConfig.get(t).is_last != null && !arrListConfig.get(
                                                    t
                                                ).is_last.equals("Yes")
                                            ) {
                                                if (strConfigType?.contains(",") == true) {
                                                    strConfigType = strConfigType?.replace(
                                                        (arrListConfig.get(t).type + ",").toRegex(),
                                                        ""
                                                    )
                                                } else {
                                                    strConfigType = strConfigType?.replace(
                                                        arrListConfig.get(t).type?.toRegex() as Regex,
                                                        ""
                                                    )
                                                }
                                                if (t == arrListConfig.size - 1) {
                                                    strConfigType =
                                                        strConfigType?.replace(" ".toRegex(), "")
                                                }
                                            } else {
                                                strConfigType = ""
                                            }
                                        }
                                    }

                                    resetChildViewsToUnsel(0, arrListConfig)
                                    setAttributeOptions(
                                        0,
                                        arrListConfig,
                                        false
                                    )
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }


    private fun showConfig(
    ) {
        ///show config
        val parentView =
            mainActivity?.layoutInflater?.inflate(
                R.layout.dialog_configuration_matched_products,
                null
            )
        btmSheetConfig = BottomSheetDialog(mainActivity!!)
        btmSheetConfig?.setContentView(parentView!!)
        btmSheetConfig?.setCanceledOnTouchOutside(true)
        btmSheetConfig?.setCancelable(true)
        btmSheetConfig?.show()
        shakeAnim = AnimationUtils.loadAnimation(mainActivity, R.anim.shake_anim)

        linCustomOpt = parentView?.findViewById(R.id.linCustomOpt) as LinearLayout
        txtAddToCart = parentView?.findViewById(R.id.txtAddBag) as TextView
        txtOverlay = parentView?.findViewById(R.id.txtOverlay) as TextView
        txtNotifyMe = parentView?.findViewById(R.id.txtNotifyMe) as TextView
        progressBar = parentView?.findViewById(R.id.progressBar)
        txtSelect = parentView?.findViewById(R.id.txtSelect) as TextView
        txtClose = parentView?.findViewById(R.id.txtClose) as TextView


        txtClose?.setOnClickListener {
            btmSheetConfig?.dismiss()
        }

        txtOverlay?.setOnClickListener {
            linCustomOpt?.startAnimation(shakeAnim)
        }


        txtAddToCart?.visibility = View.GONE
        txtOverlay?.visibility = View.VISIBLE
        txtAddToCart?.setOnClickListener {
            omniScanItem(currentSku)

        }
//        txtAddToCart?.setOnClickListener {
//            //println("Here i am add to cart clicked  " + model)
//            if (model != null) {
//
//                if (model?.is_salable!!) {
//
//                    if (model?.type.equals("configurable", true))
//                        model?.id = strConfigProdId
//
//                    var strConDbOptions = ""
//                    for (i in 0 until arrListValues!!.size) {
//                        strConDbOptions =
//                            strConDbOptions + arrListType!!.get(i) + " : " + arrListValues!!.get(i) + ","
//                    }
//
//                    // model?.dbOptions = strConDbOptions
//                    //println("Here i am user id  " + strUserId)
//                    if (strUserId != "") { //If user logged In
//                        if (cd?.isConnectingToInternet == true) {
//
//                            strProductId = if (model!!.type.equals(
//                                    "configurable",
//                                    true
//                                )
//                            ) strConfigProdId else (model?.id
//                                ?: "")  //store new product id by selecting custom option, store only if has custom option else normal product id
//
//
//                            addToCart()
//
//                        } else {
//                            Snackbar.make(
//                                parentRootView!!,
//                                mainActivity!!.resources.getString(R.string.plz_chk_internet),
//                                Snackbar.LENGTH_LONG
//                            ).setBackgroundTint(
//                                ContextCompat.getColor(mainActivity!!, R.color.grey_200)
//                            ).setTextColor(
//                                ContextCompat.getColor(mainActivity!!, R.color.black)
//                            ).setAction("Action", null).show()
//                        }
//                    } else {
//
//                        val strOptionId =
//                            Utils.removeLastCharacter(strOptionId).split(",")[0].trim()
//                        val strAttributeId =
//                            Utils.removeLastCharacter(strAttributeId).split(",")[0].trim()
//
//                        //println("Here i am cart model   :::  $model")
//                        if (model?.type.equals("configurable", true))
//                            model?.id = strConfigProdId        //  offline cart
//
//                        model?.remaining_qty = intRmngQty
//                        model?.image = strConfigImage
//
//
//                        btmSheetConfig?.dismiss()
//                    }
//                } else {
//                    Snackbar.make(
//                        parentRootView!!,
//                        mainActivity!!.resources.getString(R.string.out_stock),
//                        Snackbar.LENGTH_LONG
//                    ).setBackgroundTint(
//                        ContextCompat.getColor(mainActivity!!, R.color.grey_200)
//                    ).setTextColor(
//                        ContextCompat.getColor(mainActivity!!, R.color.black)
//                    ).setAction("Action", null).show()
//                }
//            } else {
//                Snackbar.make(
//                    parentRootView!!,
//                    mainActivity!!.resources.getString(R.string.error_dialog),
//                    Snackbar.LENGTH_LONG
//                ).setBackgroundTint(
//                    ContextCompat.getColor(mainActivity!!, R.color.grey_200)
//                ).setTextColor(ContextCompat.getColor(mainActivity!!, R.color.black))
//                    .setAction("Action", null).show()
//            }
//        }

        btmSheetConfig?.setOnDismissListener {
            boolView = false
        }
    }

    private fun getRoundedCornerBitmap(bitmap: Bitmap, pixels: Int): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)
        val roundPx = pixels.toFloat()

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint)

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)

        return output
    }

    ///
    private fun setDisableState(
        textView: TextView,
        strType: String,
        layout: LinearLayout
    ) { //set to disable state

        if (strType.equals("Size")) {
//            textView.textSize =
//                Utils.dp2px(mainActivity!!.resources.getDimension(R.dimen.eleven_textsize))!!
//                    .toFloat()
            textView.setTextColor(mainActivity!!.resources.getColor(R.color.stroke_color))
            textView.setBackgroundResource(R.drawable.attributes_disable_border)
            textView.isEnabled = true

        } else {
            layout.setBackgroundResource(R.drawable.attributes_disable_color_border)
        }
    }

    ///
    private fun setUnSelectedState(
        textView: TextView,
        strType: String,
        layout: LinearLayout
    ) { //set to unselected state

        if (strType.equals("Size")) {
//            textView.textSize =
//                Utils.dp2px(mainActivity!!.resources.getDimension(R.dimen.eleven_textsize))
//                    .toFloat()
            textView.setTextColor(mainActivity!!.resources.getColor(R.color.black))
            textView.setBackgroundResource(R.drawable.attributes_unsel_border)
            textView.isEnabled = true
        } else {
            layout.setBackgroundResource(R.drawable.attributes_unsel_color_border)
        }
    }

    ///
    private fun setSelectedState(
        textView: TextView,
        strType: String,
        layout: LinearLayout
    ) { //set to selected state
        if (strType == "Size") {
//            textView.textSize =
//                Utils.dp2px(mainActivity!!.resources.getDimension(R.dimen.eleven_textsize
//                ))
//                    .toFloat()
            textView.setTextColor(mainActivity!!.resources.getColor(R.color.white))
            textView.setBackgroundResource(R.drawable.attributes_sel_border)
            textView.isEnabled = true
        } else {
            layout.setBackgroundResource(R.drawable.attribute_sel_color_border)
        }
    }

    private fun convertToString(inStream: InputStream): String {
        var Result: String = ""
        val isReader = InputStreamReader(inStream)
        val bReader = BufferedReader(isReader)
        var temp_str: String?

        try {

            while (true) {
                temp_str = bReader.readLine()
                if (temp_str == null) {
                    break
                }
                Result += temp_str
            }
        } catch (Ex: Exception) {

        }
        return Result
    }

    ///
    private fun setSelectedColor(
        parent_position: Int,
        position: Int
    ) {
        for (j in 0 until arrLstArrTextConfig!![parent_position].size) {
            val textView1 = arrLstArrTextConfig!![parent_position][j]
            val strtype = arrLstArrTypeConfig!![parent_position][j]
            val layout = arrLstArrLayoutConfig!![parent_position][j]

            if (j == position) {
                setSelectedState(textView1, strtype, layout)
                arrLstArrConfigBool!!.get(parent_position)[j] = true
            } else {
                if (textView1.currentTextColor != mainActivity!!.resources.getColor(R.color.stroke_color)) {
                    setUnSelectedState(textView1, strtype, layout)
                    arrLstArrConfigBool!!.get(parent_position)[j] = false
                }
            }
        }
    }

    ///
    private fun resetChildViewsToUnsel(
        parent_position: Int,
        arrLstCustomOptions: ArrayList<ProductDetailsConfigurableOption>
    ) { //reset child views
        var parent_position = parent_position
        var childCount = arrLstCustomOptions.size - 1 - parent_position
        while (childCount > 0) {
            parent_position++
            childCount--
            for (i in 0 until arrLstArrTextConfig!![parent_position].size) {
                val textViews = arrLstArrTextConfig!![parent_position][i]
                val type = arrLstArrTypeConfig!![parent_position][i]
                val layout = arrLstArrLayoutConfig!![parent_position][i]
                setUnSelectedState(textViews, type, layout)
                arrLstArrConfigBool!![parent_position][i] = false
            }
        }
    }

    private fun setAttributeOptions(
        post: Int,
        arrLstCustomOptions: ArrayList<ProductDetailsConfigurableOption>,
        is_size: Boolean
    ) {

        if (!is_size) {
            strAttributeId = ""
            strOptionId = ""

            for (i in 0 until (arrLstArrConfigBool?.size ?: 0)) {
                for (j in 0 until (arrLstArrConfigBool?.get(i)?.size ?: 0)) {

                    if (arrLstArrConfigBool!![i].get(j)) {

                        strAttributeId =
                            strAttributeId + arrLstCustomOptions.get(i).attribute_id + ","
                        strOptionId =
                            strOptionId + arrLstCustomOptions.get(i).attributes?.get(j)?.option_id + ","
                    }
                }
            }
        } else {
            for (i in 0 until (arrLstArrConfigBool?.size ?: 0)) {
                for (j in 0 until (arrLstArrConfigBool?.get(i)?.size ?: 0)) {

                    if (arrLstArrConfigBool?.get(i)?.get(j) == true) {

                        strAttributeId =
                            strAttributeId + arrLstCustomOptions.get(i).attribute_id + ","
                        strOptionId =
                            strOptionId + arrLstCustomOptions.get(i).attributes?.get(0)?.option_id + ","
                    }
                }
            }
        }

        if (cd!!.isConnectingToInternet) {
            globalPost = post
            arrListOptionsGlobal = arrLstCustomOptions
            changeConfig(strAttributeId, strOptionId)
        }
    }

    private fun changeConfig(
        attributeId: String,
        optionId: String
    ) {
        val configAttr = ConfigRequestModel(
            strProductId,
            if (attributeId.endsWith(",")) {
                Utils.removeLastCharacter(attributeId)
            } else {
                attributeId
            },
            if (optionId.endsWith(",")) {
                Utils.removeLastCharacter(optionId)
            } else {
                optionId
            },
            "kuwait_en"
        )
        productDetailsViewModel.matchWithChangeConfig(selectedLanguage, configAttr)
        productDetailsViewModel.responseChangeMatchWithConfig.observe(
            mainActivity!!
        ) {
            when (it) {
                is Resource.Success -> {
                    dismissProgress()
                    handleConfigResponse(it.data!!)
                    dismissProgress()
                }

                is Resource.Error -> {

                    dismissProgress()
                }
                is Resource.Loading -> {
                    showProgress()
                }
            }
        }


    }

    private fun handleConfigResponse(result: ConfigResponseModel) {
        txtCart!!.isEnabled = true
        txtOverlay!!.isEnabled = true

        txtCart!!.visibility = View.VISIBLE
        txtSoldOut!!.visibility = View.GONE

        //println("Response config  " + result)
        try {
            if (result != null && result.status == "200") {

                try {
                    val configData = result.data?.get(0)
                    val arrLstCompareConfig =
                        ArrayList<ProductDetailsConfigurableOption>()
                    val arrListAttributes = configData?.attributes
                    //val jArrCustom = jobj.getJSONArray("attributes")

                    strConfigProdId = ""  // to reset each time when user seclect config options
                    try {
                        currentSku = configData?.sku!!
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                    if (arrListAttributes != null) {
                        for (j in 0 until arrListAttributes.size) {

                            if (!configData.type.isNullOrEmpty()) {

                                val arrLstAttributes =
                                    ArrayList<ProductDetailsAttribute>()
                                for (k in 0 until configData.attributes.size) {

                                    var arrListImages = ArrayList<String>()
                                    if (!configData.attributes[k].images.isNullOrEmpty()
                                    ) {
                                        val imagesArray =
                                            configData.attributes[k].images
                                        arrListImages = ArrayList()
                                        for (m in 0 until (imagesArray?.size ?: 0)) {
                                            arrListImages.add(imagesArray?.get(m).toString())
                                        }
                                    }

                                    val modelAttribute =
                                        ProductDetailsAttribute(
                                            "",
                                            "",
                                            arrListImages,
                                            configData.attributes[k].option_id!!,
                                            "",
                                            false,
                                            configData.attributes[k].value!!,
                                            false,
                                            true
                                        )
                                    // modelAttribute.setSelected(false)
                                    arrLstAttributes.add(modelAttribute)


                                }
                                val modelColor = ProductDetailsConfigurableOption(
                                    configData.attribute_code!!,
                                    configData.attribute_id!!.toInt(),
                                    arrLstAttributes,
                                    configData.type,
                                    "No"
                                )
                                arrLstCompareConfig.add(modelColor)
                            } else {
                                strConfigProdId =
                                    if (!configData.entity_id.isNullOrEmpty()) configData.entity_id else ""
                                intRmngQty =
                                    configData.quantity ?: 0
                            }
                        }
                    }

                    if (arrListAttributes != null) {
                        if (arrListAttributes.size == 0) {
                            strConfigProdId =
                                if (!configData.entity_id.isNullOrEmpty()) configData.entity_id else ""
                            intRmngQty =
                                configData.quantity ?: 0
                            strConfigImage = if (!configData.image.isNullOrEmpty()) {
                                configData.image[0]
                                    ?: strProductImage
                            } else {
                                strProductImage
                            }
                        }
                    }

                    if (!configData?.entity_id.isNullOrEmpty() && configData?.attributes.isNullOrEmpty() && configData?.quantity!! > 0) {
                        //println("Here i am out of stock 111")
                        model?.is_salable = true
                        txtOverlay!!.visibility = View.GONE
                        txtNotifyMe!!.visibility = View.GONE
                        txtAddToCart!!.visibility = View.VISIBLE
                        progressBar?.visibility = View.GONE
                        txtAddToCart!!.isEnabled = true
                        txtOverlay!!.isEnabled = false
                        txtNotifyMe!!.isEnabled = false
                    } else if (strConfigProdId == "") {
                        model?.is_salable = false
                        //println("Here i am out of stock 222")
                        txtOverlay!!.visibility = View.VISIBLE
                        txtAddToCart!!.visibility = View.GONE
                        txtNotifyMe!!.visibility = View.GONE
                        progressBar?.visibility = View.GONE
                        txtAddToCart!!.isEnabled = false
                        txtOverlay!!.isEnabled = true
                        txtNotifyMe!!.isEnabled = false
                    } else if (configData?.quantity == 0) {
                        model?.is_salable = false
                        //println("Here i am out of stock 333")
                        txtOverlay!!.visibility = View.GONE
                        txtAddToCart!!.visibility = View.GONE
                        txtNotifyMe!!.visibility = View.VISIBLE
                        progressBar?.visibility = View.GONE
                        txtAddToCart!!.isEnabled = false
                        txtOverlay!!.isEnabled = false
                        txtNotifyMe!!.isEnabled = true
                    } else {
                        //println("Here i am out of stock 444")
                        // Log.i("StoryFragment","Add to cart to green")
                        model?.is_salable = false
                        strConfigType = strConfigTypeReusable
                        txtNotifyMe!!.visibility = View.VISIBLE
                        txtOverlay!!.visibility = View.GONE
                        txtAddToCart!!.visibility = View.GONE
                        progressBar?.visibility = View.GONE
                        txtAddToCart!!.isEnabled = false
                        txtOverlay!!.isEnabled = false
                        txtNotifyMe!!.isEnabled = true
                    }

                    if (arrLstCompareConfig.size > 0) updateConfigOpts(
                        arrLstCompareConfig

                    )
                    else resetChildViewsToDisable(
                        globalPost!!,
                        arrListOptionsGlobal!!
                    )


                } catch (e: Exception) {
                    progressBar?.visibility = View.GONE
                    e.printStackTrace()
                    resetChildViewsToDisable(
                        globalPost!!,
                        arrListOptionsGlobal!!
                    )
                    // System.out.println("Exception " + e.toString())
                }
            } else if (result != null && result.status == "500") {
                txtOverlay!!.visibility = View.VISIBLE
                txtAddToCart!!.visibility = View.GONE
                txtNotifyMe!!.visibility = View.VISIBLE
                txtNotifyMe!!.isEnabled = true
                colorPosition = -1
                sizePosition = -1
                strConfigType = strConfigTypeReusable

                resetChildViewsToDisable(
                    globalPost!!,
                    arrListOptionsGlobal!!
                )
                progressBar?.visibility = View.GONE
            } else {

                colorPosition = -1
                sizePosition = -1
                strConfigType = strConfigTypeReusable

                resetChildViewsToDisable(
                    globalPost!!,
                    arrListOptionsGlobal!!
                )
                progressBar?.visibility = View.GONE
                ///out of stock
                /*if (result != null && result.has("status") && result.getInt("status") == 500){

                    txtCart!!.visibility=View.GONE
                    txtSoldOut!!.visibility=View.VISIBLE
                    btmSheetConfig!!.dismiss()
                }*/
                //setConfig(arrListResetOption!!, modelReset!!, "Cart")
            }

        } catch (e: JSONException) {
            progressBar?.visibility = View.GONE
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: Exception) {
            progressBar?.visibility = View.GONE
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }

    ////update
    private fun updateConfigOpts(
        arrLstCompareConfig: ArrayList<ProductDetailsConfigurableOption>
    ) { //manage custom options UI

        var arrLstConfig = ArrayList<ProductDetailsConfigurableOption>()
        if (arrLstCompareConfig.size > 0) {

            for (i in 0 until arrLstCompareConfig.size) {

                for (j in 0 until (arrList?.get(0)?.configurable_option?.size ?: 0)) {

                    if (arrLstCompareConfig[i].attribute_id.equals(
                            arrList?.get(0)?.configurable_option?.get(
                                j
                            )?.attribute_id
                        )
                    ) {

                        val arrLstProductBase =
                            arrList?.get(0)?.configurable_option?.get(j)?.attributes
                        resetToDisableConfigViews(
                            arrLstArrTextConfig!![j],
                            arrLstArrTypeConfig!![j],
                            arrLstArrLayoutConfig!![j]
                        )

                        for (k in 0 until (arrLstCompareConfig[i].attributes?.size ?: 0)) {

                            for (l in 0 until (arrLstProductBase?.size ?: 0)) {
                                if (arrLstCompareConfig[i].attributes?.get(k)?.option_id.equals(
                                        (arrLstProductBase?.get(
                                            l
                                        )?.option_id)
                                    )
                                ) {
                                    if (arrLstCompareConfig[i].attributes?.size == 1) {
                                        arrLstConfig =
                                            arrList?.get(0)?.configurable_option as ArrayList<ProductDetailsConfigurableOption>
                                    }
                                    setUnSelectedState(
                                        arrLstArrTextConfig!![j][l],
                                        arrLstArrTypeConfig!![j][l],
                                        arrLstArrLayoutConfig!![j][l]
                                    )
                                    break
                                }
                            }
                        }
                        break
                    }
                }
            }
        }

        if (arrLstCompareConfig.size > 0) {

            for (i in 0 until arrLstCompareConfig.size) {

                if (arrLstCompareConfig[i].attributes?.size == 1) {

                    for (t in 0 until arrLstCompareConfig.size) {

                        if (arrLstCompareConfig[t].type.equals(arrLstCompareConfig[i].type)) {

                            //arrListValues!!.set(t, arrLstCompareConfig[t].attributes[0].value)
                            if (arrLstCompareConfig[t].type.equals("size", true))
                                arrListValues?.set(
                                    1, arrLstCompareConfig[t].attributes?.get(i)?.value
                                        ?: ""
                                )
                            else arrListValues?.set(
                                0, arrLstCompareConfig[t].attributes?.get(i)?.value
                                    ?: ""
                            )

                            if (arrLstCompareConfig[t].is_last != null && !arrLstCompareConfig[t].is_last.equals(
                                    "Yes"
                                )
                            ) {

                                if (strConfigType!!.contains(",")) {
                                    strConfigType = strConfigType!!.replace(
                                        (arrLstCompareConfig[t].type + ",").toRegex(),
                                        ""
                                    )
                                } else {
                                    strConfigType = strConfigType!!.replace(
                                        arrLstCompareConfig[t].type?.toRegex() as Regex,
                                        ""
                                    )
                                }

                                if (t == arrLstCompareConfig.size - 1) {
                                    strConfigType = strConfigType!!.replace(" ".toRegex(), "")
                                }

                            } else {
                                // txtCongigType.setText("")
                                strConfigType = ""
                            }

                            if (arrListResetOption?.size == 2) {
                                for (k in 0 until (arrListResetOption?.get(1)?.attributes?.size
                                    ?: 0)) {

                                    for (j in 0 until arrLstCompareConfig.size) {
                                        if (arrListResetOption?.get(1)?.attributes?.get(k)?.value.equals(
                                                arrLstCompareConfig[j].attributes?.get(0)?.value,
                                                true
                                            )
                                        ) {

                                            sizePosition = k
                                            resetChildViewsToUnsel(
                                                i,
                                                arrLstCompareConfig
                                            )
                                            setAttributeOptions(
                                                1,
                                                arrLstCompareConfig,
                                                true
                                            )
                                            setSelectedColor(1, k)

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showProgress() {
        try {
            if (loading == null) {
                loading = Dialog(
                    mainActivity!!,
                    R.style.TranslucentDialog
                )
                loading!!.setContentView(R.layout.custom_loading_view)
                loading!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                loading!!.setCanceledOnTouchOutside(false)
                if (!loading!!.isShowing)
                    loading!!.show()
            }

            if (loading != null && !loading!!.isShowing)
                loading!!.show()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    ///dismiss loading
    private fun dismissProgress() {

        try {
            if (loading != null)
                loading!!.dismiss()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun resetChildViewsToDisable(
        parent_position: Int,
        arrLstCustomOptions: ArrayList<ProductDetailsConfigurableOption>
    ) { //reset child views
        var parent_position = parent_position
        var childCount = arrLstCustomOptions.size - 1 - parent_position
        // System.out.println("has child no.: " + childCount)
        while (childCount > 0) {
            parent_position++
            childCount--
            for (i in 0 until arrLstArrTextConfig!![parent_position].size) {
                val textViews = arrLstArrTextConfig!![parent_position][i]
                val type = arrLstArrTypeConfig!![parent_position][i]
                val layout = arrLstArrLayoutConfig!![parent_position][i]
                setDisableState(textViews, type, layout)
                arrLstArrConfigBool!![parent_position][i] = false
            }
        }
    }

    private fun resetToDisableConfigViews(
        textViews: ArrayList<TextView>,
        types: ArrayList<String>,
        layout: ArrayList<LinearLayout>
    ) { //reset UI of custom opt
        for (i in 0 until textViews.size) {
            setDisableState(textViews[i], types[i], layout[i])
        }
    }

    private fun addToCart(

    ) {

        val cartModel = AddToCartRequest.CartData(
            strUserId,
            strProductId,
            strParentId,
            "1",
            ""
        )
        val reqModel = AddToCartRequest(cartModel)


        productDetailsViewModel.addToCart(selectedLanguage, selectedCurrency, reqModel)
        productDetailsViewModel.responseAddToCart.observe(
            mainActivity!!
        ) {
            when (it) {
                is Resource.Success -> {
                    dismissProgress()
                    handleAddToCartResponse(it.data!!)

                }

                is Resource.Error -> {
                    dismissProgress()
                }
                is Resource.Loading -> {
                    showProgress()
                }
            }
        }
    }

    @SuppressLint("ResourceType")
    private fun handleAddToCartResponse(cartModel: AddToCartResponse) {
        if (btmSheetConfig != null) {
            btmSheetConfig?.dismiss()
        }

        ///////////////////////////////////////////////////////////////////////////////////////////

        dismissProgress()
        try {

            if (cartModel.status.equals("200")) {

                if (model!!.type.equals("configurable", true))
                    model!!.id = strConfigProdId   // For offline cart

                model!!.remaining_qty = intRmngQty
                val cartCount =
                    sharedpreferenceHandler.getData(SharedpreferenceHandler.CART_COUNT, 0)
                sharedpreferenceHandler.saveData(SharedpreferenceHandler.CART_COUNT, cartCount + 1)
                mainActivity?.showProductAddedView()
            } else {
                Snackbar.make(parentRootView!!, cartModel.message, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        } catch (e: JSONException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }


    ///handle error
    fun handleError(error: Throwable) {

        if (loading != null)
            loading?.hide()

        dismissProgress()
        if (error.localizedMessage != null && !error.localizedMessage.isEmpty())
            Utils.showErrorSnackbar(parentRootView!!)
    }

    private fun continueShopping() {
        if (!mainActivity!!.isFinishing) {
            val dialogView =
                LayoutInflater.from(mainActivity!!).inflate(R.layout.custom_alert, null)

            val builder = androidx.appcompat.app.AlertDialog.Builder(mainActivity!!)
                .setView(dialogView)
            val dialog = builder.show()
            dialog.setCancelable(false)

//            dialog.txtAlertMsg.text =
//                mainActivity!!.resources.getString(R.string.the_item_is_now_in_your_bag)
            dialog.buttonAction.text =
                mainActivity!!.resources.getString(R.string.continue_shopping)
//            dialog.btnContinue.text = mainActivity!!.resources.getString(R.string.view_cart)


            dialog.buttonAction.setOnClickListener {
                dialog.dismiss()
                //mainActivity!!.finish()
            }

            dialog.btnContinue.setOnClickListener {
                val intent =
                    Intent(
                        mainActivity!!,
                        BagActivity::class.java
                    )
                mainActivity!!.startActivity(intent)
                dialog.dismiss()
            }
        }
    }


    fun omniScanItem(skuCode: String) {
        val sharedpreferenceHandler = SharedpreferenceHandler(mainActivity!!)
        val priceListId = sharedpreferenceHandler.getData(SharedpreferenceHandler.PRICE_LIST_ID, 0)
        val storeId = sharedpreferenceHandler.getData(SharedpreferenceHandler.STORE_ID, 0)
        productDetailsViewModel.omniScanAddToCartItem(
            skuCode,
            storeId!!.toString(),
            priceListId!!.toString()
        )
        productDetailsViewModel.responseScanAddToCartItemOmni.observe(
            mainActivity!!
        ) {
            when (it) {
                is Resource.Success -> {
                    dismissProgress()
                    btmSheetConfig?.dismiss()
                    if (it.data?.statusCode == 1) {
                        scannedItem = it.data
                        addProductToDb()
                        mainActivity?.showProductAddedView()
                    } else {
                        Utils.showSnackbar(parentRootView!!, it.data?.displayMessage!!)
                    }
                 productDetailsViewModel.responseScanAddToCartItemOmni.value=null

                }

                is Resource.Error -> {
                    btmSheetConfig?.dismiss()
                    dismissProgress()
                    try {
                        val jsonObj = JSONObject(it.message)
                        val message = jsonObj.getString("message")
                        Utils.showSnackbar(
                            parentRootView!!, message
                        )
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                }
                is Resource.Loading -> {
                    showProgress()
                }
            }
        }
    }


    private fun addProductToDb() {
        try {
            scannedItem?.skuMasterTypesList?.get(0)?.imageUrl =
                strConfigImage
            scannedItem?.skuMasterTypesList?.get(0)?.fromRiva = true
            scannedItem?.skuMasterTypesList?.get(0)?.productId= strParentId
            productDetailsViewModel.addOmniProductToPrefs(
                scannedItem?.skuMasterTypesList?.get(0)!!, mainActivity!!
            )
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }


}