package com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.armada.storeapp.ui.home.riva.riva_look_book.bag.BagActivity
import com.armada.riva.ProductDetail.adapter.ConfigAdapter
import com.armada.storeapp.R
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.local.model.RecentProduct
import com.armada.storeapp.data.model.request.*
import com.armada.storeapp.data.model.response.*
import com.armada.storeapp.databinding.ActivityOmniProductDetailsBinding
import com.armada.storeapp.ui.base.BaseActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.bag.OmniBagActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.adapter.*
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.interfaces.OnAttributeSelectListener
import com.armada.storeapp.ui.utils.Constants
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import com.armada.storeapp.ui.utils.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class OmniProductDetailsActivity : BaseActivity() {

    private var selectedLanguage: String = "en"
    private var selectedCurrency = "USD"
    private var imageWidth: Int = 0
    private var imageHeight: Int = 0
    private var productheight: Int = 0
    private var productWidthHalf: Int = 0
    private var productHeightHalf: Int = 0
    private var recentHeight: Int = 0
    private var viewPgHeight: Double = 0.0
    private var viewPagerWidth: Double = 0.0
    private var margin5 = 0
    private var margin10 = 0
    private var margin7 = 0
    private var recentWidth: Int = 0
    private var viewPagerHeight: Double = 0.0
    private var productWidth: Int = 0
    private var viewPgWidth: Double = 0.0
    private var constant: Int = 0
    private var screenWidth: Int = 0
    private var strUserId: String? = null
    private var strFirstImage: String? = null
    private var strParentId: String? = null
    private var productId: String? = null
    private var strCategoryId: String? = null
    lateinit var binding: ActivityOmniProductDetailsBinding
    lateinit var productDetailsViewModel: ProductDetailsViewModel
    lateinit var sharedpreferenceHandler: SharedpreferenceHandler

    var productData: ProductDetailsData? = null
    private var shakeAnime: Animation? = null

    private var arrListAllImages: ArrayList<String>? = null
    private var arrListSingleValue: ArrayList<String>? = null
    private var arrListValues: ArrayList<String>? = null
    private var arrListType: ArrayList<String>? = null

    ///Free shipping
    var strShippingMsg = ""
    var strDiscountPercentage = ""
    var strShippingPrice = "0"
    var cartAmount = 0.0

    private var strProductName = ""
    private var strPrice = ""
    private var modelReset: ProductDetailsData? = null

    private var strImageurl = ""
    private var strConfigImage = ""
    private var strImageReusable = ""
    var arrListImg = ArrayList<String>()
    private var strSizeGuide = ""
    private var intRemainingQty = 1

    private var strConfigType: String? = ""
    private var strConfigTypeReusable = ""

    private var singleColor = false
    private var singleSize = false
    private var strProductId = ""
    private var strProductIdReusable = ""
    var strVideoUrl = ""
    private var arrListResetOption: ArrayList<ProductDetailsConfigurableOption>? = null
    var isFirstAttSelected = true

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var productAddedSheetBehaviour: BottomSheetBehavior<ConstraintLayout>

    private var loading: Dialog? = null
    var arrListRecent = ArrayList<RecentProduct>()
    var isLoggedIn = false

    private var attributeId1 = ""
    private var attributeId2 = ""
    private var attributeIdType1 = ""
    private var attributeIdType2 = ""
    private var optionId1 = ""
    private var optionId2 = ""
    private var optionValue1 = ""
    private var optionValue2 = ""
    private var arrListAttr1: ArrayList<ProductDetailsAttribute>? = null
    private var arrListAttr2: ArrayList<ProductDetailsAttribute>? = null
    private var configSize = 0
    var configAdapter1: ConfigAdapter? = null
    var configAdapter2: ConfigAdapter? = null
    private var strConfigProdId = ""

    var height = 0
    var isMatchedWithAvailable = false
    var isRecentSearchAvailable = false
    private var isAlertVisible = false
    var omniScannedItem: ScannedItemDetailsResponse? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOmniProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        productDetailsViewModel =
            ViewModelProvider(this).get(ProductDetailsViewModel::class.java)
        init()

    }

    fun init() {
        sharedpreferenceHandler = SharedpreferenceHandler(this)
        val metrics = resources.displayMetrics
        screenWidth = (metrics.widthPixels)

        constant = screenWidth / 320
        viewPgWidth = screenWidth.toDouble()
        viewPagerWidth = (260 * constant).toDouble()
        viewPagerHeight = (420 * constant).toDouble()
        //viewPgeHeight = viewPgeWidth / 0.687
        viewPgHeight = (constant * 510).toDouble()
        productWidth = (screenWidth / 2) - (resources.getDimension(R.dimen.ten_dp)).toInt()
        recentWidth = ((screenWidth / 2) - resources.getDimension(R.dimen.seven_dp)).toInt()
        recentHeight = (recentWidth / 0.687).toInt()
        productWidthHalf = ((screenWidth / 2) - resources.getDimension(R.dimen.seven_dp)).toInt()
        productHeightHalf = (productWidthHalf / 0.687).toInt()
        productheight = (productWidth / 0.687).toInt()

        margin5 = resources.getDimension(R.dimen.five_dp).toInt()
        margin7 = resources.getDimension(R.dimen.seven_dp).toInt()
        margin10 = resources.getDimension(R.dimen.ten_dp).toInt()

        imageWidth = (viewPgWidth - resources.getDimension(R.dimen.thirty_dp)).toInt()
        imageHeight = (imageWidth / 0.687).toInt()
        strUserId = sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_USER_ID, "")
        selectedLanguage =
            sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_SELECTED_COUNTRY, "en")!!
        selectedCurrency =
            sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_SELECTED_CURRENCY, "USD")!!
        isLoggedIn =
            sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_USER_LOGGED_IN, false)
        arrListAllImages = ArrayList()
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        productAddedSheetBehaviour = BottomSheetBehavior.from(binding.bottomSheetProductAdded)
        setupToolbar()
        intent?.let {
            if (it.hasExtra("id") && it.getStringExtra("id") != null) {
                strParentId = intent.getStringExtra("id")
                productId = intent.getStringExtra("id")
                getProductDetails(productId!!)
            } else {
                productId = "0"
            }

            if (intent.getStringExtra("cat_id") != null) {
                strCategoryId = intent.getStringExtra("cat_id").toString()
            }

            //Loading image initially
            if (intent.hasExtra("image") && !intent.getStringExtra("image").isNullOrEmpty()) {
                strFirstImage = intent.getStringExtra("image")!!
                if (!this.isFinishing) {
                    Glide
                        .with(this)
                        .asBitmap()
                        .load(if (strFirstImage != null && strFirstImage != "") strFirstImage else Constants.strNoImage)
                        .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                        .override(
                            Utils.getDeviceWidth(this),
                            (Utils.getDeviceWidth(this) * 1.4562).toInt()
                        )
                        .listener(object : RequestListener<Bitmap?> {
                            override fun onLoadFailed(
                                @Nullable e: GlideException?,
                                model: Any,
                                target: Target<Bitmap?>,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }

                            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                            override fun onResourceReady(
                                resource: Bitmap?,
                                model: Any,
                                target: Target<Bitmap?>,
                                dataSource: DataSource,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }
                        })
                        .into(binding.imageProduct)
                    startPostponedEnterTransition()
                }
            } else {
                binding.imageProduct.visibility = View.VISIBLE
            }
        }

        updateBadgeToCart(false)

        val rect = Rect()
        binding.nestedScrollView.getHitRect(rect)

        binding.nestedScrollView?.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (binding.viewPagerIndicator.getLocalVisibleRect(rect)) {
                binding.toolbar1.relDetailToolbar.visibility = View.GONE
                binding.toolbar1.relCartImage.visibility = View.VISIBLE
                binding.toolbar1.relCheckoutBtn.visibility = View.GONE
                try {
                    if (productData?.is_salable == true) {
                        binding.btnAddToBag.visibility = View.VISIBLE
                        binding.lvAnchor.visibility = View.GONE
                        binding.btnAddToBagAnchor.visibility = View.GONE
                        binding.txtOutOFStock.visibility = View.GONE
                        binding.txtOutOFStockAnchor.visibility = View.GONE
                    } else {
                        binding.txtOutOFStock.visibility = View.VISIBLE
                        binding.lvAnchor.visibility = View.GONE
                        binding.txtOutOFStockAnchor.visibility = View.GONE
                        binding.btnAddToBag.visibility = View.GONE
                        binding.btnAddToBagAnchor.visibility = View.GONE
                    }
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }

                setStatusBarColorForDetail(
                    this@OmniProductDetailsActivity,
                    false,
                    binding.toolbar1.root!!
                )
            } else {
                binding.toolbar1.relDetailToolbar.visibility = View.VISIBLE
                binding.toolbar1.relCartImage.visibility = View.GONE
                binding.toolbar1.relCheckoutBtn.visibility = View.VISIBLE
                updateBadgeToCart(true)
//                binding.toolbar1?.imageViewShare.visibil,mity = View.INVISIBLE
                try {
                    if (productData?.is_salable == true) {
                        binding.txtOutOFStock.visibility = View.GONE
                        binding.txtOutOFStockAnchor.visibility = View.GONE
                        binding.btnAddToBag.visibility = View.GONE
                        binding.lvAnchor.visibility = View.VISIBLE
                        binding.btnAddToBagAnchor.visibility = View.VISIBLE
                    } else {
                        binding.btnAddToBag.visibility = View.GONE
                        binding.btnAddToBagAnchor.visibility = View.GONE
                        binding.txtOutOFStock.visibility = View.GONE
                        binding.lvAnchor.visibility = View.VISIBLE
                        binding.txtOutOFStockAnchor.visibility = View.VISIBLE
                    }
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
                setStatusBarColorForDetail(
                    this@OmniProductDetailsActivity,
                    true,
                    binding.toolbar1.root!!
                )
            }
//            isVisible(binding.dummyView)
        })

        binding.toolbar1.relCartImage.visibility = View.VISIBLE
        setListeners()

//        binding.lvAnchor.visibility = View.GONE
//        binding.btnAddToBag.visibility=View.GONE

    }

    private fun setListeners() {
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }
        })
        binding.lnrProductDetails.setOnClickListener {
            if (binding.expandableLayoutStatus.isExpanded) {
                binding.expandableLayoutStatus.collapse()
                binding.ivProductDetailsExp.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@OmniProductDetailsActivity,
                        R.drawable.ic_plus
                    )
                )
            } else {
                binding.expandableLayoutStatus.expand()
                binding.ivProductDetailsExp.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@OmniProductDetailsActivity,
                        R.drawable.substract
                    )
                )

            }
        }

        binding.txtClose.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.btnAddToBagBottom.setOnClickListener {
            binding.btnAddToBag.performClick()
        }


        binding.btnAddToBagAnchor.setOnClickListener {
            binding.btnAddToBag.performClick()
        }

        binding.lnrProductDetails.setOnClickListener {

            if (binding.expandableLayoutStatus.isExpanded) {
                binding.expandableLayoutStatus.collapse()
                binding.ivProductDetailsExp.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@OmniProductDetailsActivity,
                        R.drawable.ic_plus
                    )
                )
            } else {
                binding.expandableLayoutStatus.expand()
                binding.ivProductDetailsExp.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@OmniProductDetailsActivity,
                        R.drawable.substract
                    )
                )

            }
        }

        ///add to cart
        binding.btnAddToBag.setOnClickListener()
        {
            binding.lvAnchor.visibility = View.GONE
            if (productData != null) {
                if (productData?.type.equals("configurable", true)) {
                    if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                        changeConfig(
                            strParentId,
                            "$attributeId1,$attributeId2",
                            "$optionId1,$optionId2"
                        )
                        return@setOnClickListener
                    }
                }
                if (configSize == 2) {
                    if (optionValue1.isNullOrEmpty()) {
                        Utils.showSnackbar(
                            binding.bottomSheet,
                            String.format(
                                resources.getString(R.string.please_select_string),
                                "Color"
                            )
                        )
                        return@setOnClickListener
                    }
                    if (optionValue2.isNullOrEmpty()) {
                        Utils.showSnackbar(
                            binding.bottomSheet,
                            String.format(
                                resources.getString(R.string.please_select_string),
                                "Size"
                            )
                        )
                        return@setOnClickListener
                    }

                }

                if (configSize == 1) {
                    if (optionValue1.isNullOrEmpty()) {
                        Utils.showSnackbar(
                            binding.bottomSheet,
                            String.format(
                                resources.getString(R.string.please_select_string),
                                "Color"
                            )
                        )
                        return@setOnClickListener
                    }
                }

                if (productData?.type.equals("configurable", true) && strConfigProdId.isNotEmpty())
                    productData?.id = strConfigProdId

//                var strConDbOptions = ""
//                if (!attributeId1.isNullOrEmpty() && !attributeId2.isNullOrEmpty()) {
//                    strConDbOptions =
//                        attributeIdType1 + " : " + optionValue1 + "," + attributeIdType2 + " : " + optionValue2
//                }
//
//                productData!!.dbOptions = strConDbOptions

//                if (!strUserId.equals("")) { //If user logged In
                if (Utils.hasInternetConnection(this)) {

//                    strProductId = if (productData!!.type.equals(
//                            "configurable",
//                            true
//                        )
//                    ) strConfigProdId else productData?.id.toString() //store new product id by selecting custom option, store only if has custom option else normal product id
//                    if (strProductId.isNullOrEmpty())
//                        strProductId = productData?.id!!
//                        addToCart()
                    var sku = binding.tvSku.text.toString().replace("SKU:", "").trim()
                    if (sku.isNullOrEmpty())
                        sku = productData?.sku!!
                    var noOfSizeSelected = 0
                    try {
                        arrListAttr2?.forEach {
                            if (it.isSelected == true)
                                noOfSizeSelected++
                        }
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                    if (noOfSizeSelected == 0)
                        Utils.showSnackbar(binding.root, "Please select any size to proceed")
                    else
                        omniScanItem(sku)

                } else {
                    Snackbar.make(
                        binding.nestedScrollView,
                        resources.getString(R.string.plz_chk_internet),
                        Snackbar.LENGTH_LONG
                    ).setAction("Action", null).show()
                }
//                }
//                else {

//                    val strOptionIdConfig = optionId1
//                    val strAttributeIdConfig = attributeId1
//
//                    if (productData!!.type.equals("configurable", true))
//                        productData!!.id = strConfigProdId        // FOo offline cart
//
//                    productData!!.remaining_qty = intRemainingQty
//                    productData!!.image = strConfigImage
//                    val cartItem = ShoppingCart(
//                        1,
//                        "",
//                        productData!!.id,
//                        productData!!.name,
//                        productData!!.short_description,
//                        productData!!.price.toString(),
//                        productData!!.final_price.toString(),
//                        productData!!.is_salable.toString(),
//                        productData!!.image,
//                        1,
//                        productData!!.dbOptions,
//                        strParentId,
//                        "$strOptionIdConfig,$strAttributeIdConfig",
//                        productData!!.remaining_qty
//                            ?: 0,
//                        "",
//                        "",
//                        0
//                    )
//                    productDetailsViewModel.insertItemToCart(cartItem)
//
//                    updateBadgeToCart(false)
//
//                    if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
//                        val a = bottomSheetBehavior
//                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
//                        a.addBottomSheetCallback(object :
//                            BottomSheetBehavior.BottomSheetCallback() {
//                            override fun onSlide(bottomSheet: View, slideOffset: Float) {
//                            }
//
//                            override fun onStateChanged(bottomSheet: View, newState: Int) {
//                                when (newState) {
//                                    BottomSheetBehavior.STATE_HIDDEN ->
//                                        if (!isAlertVisible) {
//                                            isAlertVisible = true
//                                            Handler().postDelayed({
//                                                 showProductAddedView()
//                                            }, 100)
//                                        }
//                                    BottomSheetBehavior.STATE_COLLAPSED -> {
//
//                                    }
//                                    BottomSheetBehavior.STATE_DRAGGING -> {
//
//                                    }
//                                    BottomSheetBehavior.STATE_EXPANDED -> {
//
//                                    }
//                                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
//
//                                    }
//                                    BottomSheetBehavior.STATE_SETTLING -> {
//
//                                    }
//                                }
//                            }
//                        })
//                    } else {
//                        if (!isAlertVisible) {
//                            showProductAddedView()
//                        }
//                    }
//                }
            } else {
                Snackbar.make(
                    binding.nestedScrollView,
                    resources.getString(R.string.error_dialog),
                    Snackbar.LENGTH_LONG
                ).setAction("Action", null).show()
            }
        }

        ///size guide
        binding.tvSizeGuide.setOnClickListener()
        {

            val intent = Intent(this, SizeGuideActivity::class.java)
            intent.putExtra("url", strSizeGuide)
            startActivity(intent)

        }

        binding.lvColors.setOnClickListener {
            val state =
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                    BottomSheetBehavior.STATE_COLLAPSED
                else
                    BottomSheetBehavior.STATE_EXPANDED
            bottomSheetBehavior.state = state
            setConfig(productData?.configurable_option!!, "Config")
        }

        binding.btnContinueShopping.setOnClickListener {
            productAddedSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
            this.finish()
        }
        binding.btnViewBag.setOnClickListener {
            productAddedSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
            val intent = Intent(this, OmniBagActivity::class.java)
            startActivity(intent)
        }

        binding.toolbar1.relCartImage.setOnClickListener {
            val intent = Intent(this, OmniBagActivity::class.java)
            startActivity(intent)
        }

        binding.toolbar1.relCheckoutBtn.setOnClickListener {
            val intent = Intent(this, OmniBagActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding?.toolbar1?.root)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        val upArrow: Drawable? =
            ContextCompat.getDrawable(this, R.drawable.ic_baseline_arrow_back_24)
        upArrow?.setColorFilter(
            ContextCompat.getColor(this, R.color.black),
            PorterDuff.Mode.SRC_ATOP
        )
        upArrow?.setVisible(true, true)
        supportActionBar?.setHomeAsUpIndicator(upArrow)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        supportActionBar?.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this,
                    R.color.transparent
                )
            )
        )
        binding?.toolbar1?.root?.background?.alpha = 0
        binding?.toolbar1?.txtHead.text = productData?.name
        try {
            changeToolbarImage(productData?.images!![0])
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setStatusBarColorForDetail(
        activity: Activity,
        isWhite: Boolean,
        relToolbar: Toolbar
    ) {
        val window = activity.window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
        if (isWhite) {
            relToolbar.setBackgroundResource(R.color.white)
        } else {
            relToolbar.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent))
        }
    }

    private fun changeToolbarImage(strImage: String) {
        if (strImage != null && strImage.isNotEmpty()) {
            if (!this.isFinishing) {
                Utils.loadImagesUsingCoil(this, strImage, binding?.toolbar1?.imgToolbarImage)
                try {
                    binding.toolbar1.txtHead.text = productData?.name
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            }
        }
    }

    fun getProductDetails(productId: String) {
        productDetailsViewModel.getProductDetails(selectedLanguage, selectedCurrency, productId)
        productDetailsViewModel.responseProductDetails.observe(this) {
            when (it) {
                is Resource.Success -> {
                    dismissProgress()
                    handleProductDetailsResponse(it.data!!)
                }

                is Resource.Error -> {
                    Utils.showSnackbar(binding.root, "Requested product could not found")
                    dismissProgress()

                }
                is Resource.Loading -> {
                    showProgress()
                }
            }
        }
    }

    private fun handleProductDetailsResponse(response: ProductDetailsResponse) {
        productData = response.data!!
        productData?.is_wishlist =
            productData?.wishlist_item_id != null && productData?.wishlist_item_id != 0
        val arrListConfig =
            ArrayList<ProductDetailsConfigurableOption>()

        arrListValues = ArrayList()
        arrListType = ArrayList()
        arrListSingleValue = ArrayList()

        //Images
        if (!productData?.images.isNullOrEmpty()) {
            strImageurl = productData?.images!![0]
            strConfigImage = productData?.images!![0]
            strImageReusable = productData?.images!![0]
            arrListImg = productData?.images!!
        }

        /////////////////////////Size Guide/////////////////////////////////////////////
        if (!productData?.size_chart.isNullOrEmpty()) {
            strSizeGuide = productData?.size_chart ?: ""
            if (strSizeGuide != null && !strSizeGuide.equals("")) {
                binding.tvSizeGuide.visibility = View.VISIBLE
            } else {
                binding.tvSizeGuide.visibility = View.GONE
            }
        }

        try {
            changeToolbarImage(productData?.images!![0])
        } catch (e: Exception) {
            //  e.printStackTrace()
        }

        if (!productData?.configurable_option.isNullOrEmpty()) {
            val configOptions = productData?.configurable_option
            for ((index, value) in configOptions!!.withIndex()) {
                if (!value.attributes.isNullOrEmpty()) {
                    val arrListAttribute =
                        ArrayList<ProductDetailsAttribute>()
                    val attributes = value.attributes!!
                    attributes.forEach {
                        arrListSingleValue!!.add(it.value!!)
                        var arrListImages = ArrayList<String>()
                        var attributeImage = ""
                        if (!it.images.isNullOrEmpty()) {
                            arrListImages = it.images!!
                            attributeImage = it.images!![0]
                        }

                        val modelAttribute =
                            ProductDetailsAttribute(
                                it.attribute_image_url!!,
                                it.color_code ?: "",
                                arrListImages,
                                it.option_id!!,
                                it.price!!,
                                should_select = true,
                                it.value!!,
                                isSelected = false,
                                isAvailable = true
                            )

                        arrListAttribute.add(modelAttribute)
                    }

                    strConfigType = if (strConfigType != null && strConfigType == "")
                        value.type
                    else
                        strConfigType + ", " + value.type


                    var strLast: String
                    strLast = if (index == configOptions.size - 1)
                        "Yes"
                    else "No"

                    arrListValues!!.add(value.type.toString())
                    arrListType!!.add(value.type.toString())

                    if (value.type
                            .equals(resources.getString(R.string.color), true)
                    ) {
                        if (arrListAttribute.size == 1) {
                            singleColor = true
                        }
                    }

                    if (value.type
                            .equals(resources.getString(R.string.size), true)
                    ) {
                        if (arrListAttribute.size == 1) {
                            singleSize = true
                            isFirstAttSelected = false
                        }
                    }

                    val modelConfig =
                        ProductDetailsConfigurableOption(
                            value.attribute_code,
                            value.attribute_id,
                            arrListAttribute,
                            value.type,
                            strLast
                        )

                    arrListConfig.add(modelConfig)

                    strProductId = productData?.id.toString()


                    strVideoUrl = productData?.video_url!!
                    strProductName = this.productData?.name ?: ""
                    strPrice = this.productData?.final_price.toString()
                    ///TO reset After adding Cart
                    modelReset = this.productData
                    strProductIdReusable = this.productData?.id ?: ""
                }
            }
        }
        var strImage = ""
        var isActive = 0
        var bannerIpadHeight = 0


//        binding.lnrMain.animate()
//            .alpha(1f)
//            .setDuration(1)
//            .setListener(object : AnimatorListener() {
//                override fun onAnimationEnd(animator: Animator) {
//                    binding.lnrMain.visibility = View.VISIBLE
//                    binding.nestedScrollView.visibility = View.VISIBLE
////                    binding.viewAboveAnchor.visibility = View.GONE
//                }
//            })


        if (Utils.getPageBanner() != null && Utils.getPageBanner().isNotEmpty()) {
            val arrListBanner = Utils.getPageBanner()
            for (i in 0 until arrListBanner.size) {
                if (arrListBanner[i].type.equals("product_details")) {
                    strImage = arrListBanner[i].banner_image as String
                    isActive = arrListBanner[i].is_active
                    bannerIpadHeight = arrListBanner[i].banner_height
                    break
                }
            }
        }

        if (isActive == 1) {
            if (!this.isFinishing) {
                val deviceMultiplier = Utils.getDeviceWidth(this) / 320
                val height = bannerIpadHeight.times(deviceMultiplier)
                val params = LinearLayout.LayoutParams(
                    (Utils.getDeviceWidth(this) - resources.getDimension(
                        R.dimen.twenty_dp
                    )).toInt(), height
                )
                params.setMargins(
                    resources.getDimension(R.dimen.ten_dp).toInt(),
                    resources.getDimension(R.dimen.ten_dp).toInt(),
                    resources.getDimension(R.dimen.ten_dp).toInt(),
                    resources.getDimension(R.dimen.ten_dp).toInt()
                )
                binding.imgBanner.layoutParams = params
                if (!this.isFinishing) {
                    Utils.loadImagesUsingCoil(this, strImage, binding.imgBanner)
                }
            }
        }

        setImageAdapter()


        handleOutOfStock()

        if (strVideoUrl.isNotEmpty()) {
            binding.relVideo.visibility = View.VISIBLE

        } else {
            binding.relVideo.visibility = View.GONE
        }

        val param = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        if (strVideoUrl.isNotEmpty()) {
            param.setMargins(0, resources.getDimension(R.dimen.twenty_dp).toInt(), 0, 0)
        } else {
            param.setMargins(0, 0, 0, 0)
        }
//        lnrProductInfo.layoutParams = param

        setProductDetails()
        setLikedProducts()
        setMatchWithProducts()


        //TO reset After adding Cart
        arrListResetOption = productData?.configurable_option
        strConfigTypeReusable = strConfigType!!

//        binding.relNoResult.visibility = View.GONE
//        binding.lnrMain.visibility = View.VISIBLE
        binding.imageProduct.visibility = View.GONE

//        binding.lvAnchor.visibility = View.VISIBLE
//        binding.btnAddToBag.visibility=View.GONE
    }

    fun setImageAdapter() {
        /////////////////////////////////////////////////////////////////////////////////////
        val width = Utils.getDeviceWidth(this)
        val pagerSize = RelativeLayout.LayoutParams(width, (width * 1.4562).toInt())
        binding.pager.layoutParams = pagerSize
        binding.imageProduct.layoutParams = pagerSize
        val arrListGalleryImages = productData?.images
        productData?.images?.let { arrListAllImages?.addAll(it) }
        if (!arrListGalleryImages.isNullOrEmpty() && !strFirstImage.isNullOrEmpty()) {
            arrListGalleryImages.removeAt(0)
            arrListGalleryImages.add(0, strFirstImage!!)
        }

        binding.pager.visibility = View.VISIBLE
        binding.pager.adapter = OmniImagePagerAdapter(this, arrListGalleryImages!!)
        binding.pager.currentItem = 0
        binding.pager.clipToPadding = false
        binding.pager.setPadding(0, 0, 0, 0)
        binding.pager.setPageTransformer(
            true
        ) { page, position -> /*if (imageAdapter.count == 1) {
                            if (AppController.instance.isLangArebic)
                                page.translationX = 140f

                        } else {
                            if (pager.currentItem == imageAdapter.count - 1) {
                                page.translationX = 140f
                            } else {
                                page.translationX = 0f
                            }
                        }*/
        }
        binding.viewPagerIndicator.noOfPages = arrListGalleryImages!!.size
        binding.viewPagerIndicator.visibleDotCounts = 7
        binding.viewPagerIndicator.onPageChange(binding.pager.currentItem)

        binding.pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                /* if(position == result!!.gallery_images.size-1)
                 {
                     pager.setPadding(resources.getDimension(R.dimen.fifty_dp).toInt(), 0, 0, 0)
                 }else
                 {
                     pager.setPadding(0, 0, resources.getDimension(R.dimen.fifty_dp).toInt(), 0)
                 }*/
            }

            override fun onPageSelected(position: Int) {

                if (binding.pager.currentItem >= 0)
                    binding.viewPagerIndicator.onPageChange(binding.pager.currentItem)
                else binding.viewPagerIndicator.onPageChange(0)
            }
        })

        val animFadeIn = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in)
        val animFadeOut = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out)

        binding.imageProduct.startAnimation(animFadeOut)
        binding.pager.startAnimation(animFadeIn)

        binding.imageProduct.visibility = View.GONE
        binding.pager.visibility = View.VISIBLE

    }

    fun setLikedProducts() {
        if (productData?.upsell != null && productData?.upsell!!.size <= 0) {
            binding.relLikeProducts.visibility = View.GONE
        } else {
            binding.relLikeProducts.visibility = View.VISIBLE
            binding.likeNote.visibility = View.VISIBLE
            binding.pagerLikeProducts.adapter =
                OmniLikeImagePagerAdapter(
                    selectedCurrency,
                    imageWidth,
                    imageHeight,
                    this@OmniProductDetailsActivity,
                    productData?.upsell!!
                )
            binding.pagerLikeProducts.currentItem = 0
        }
    }

    fun setMatchWithProducts() {

        if (productData?.related != null && productData?.related!!.size > 0) {
            binding.lnrRelated.visibility = View.VISIBLE
            val adapter = OmniMatchedProductAdapter(
                selectedCurrency,
                productheight,
                screenWidth,
                this@OmniProductDetailsActivity,
                productData?.related
            )
            adapter.onItemAddedToCart = { item ->
                strParentId = item.id.toString()
                AddToCartBottomSheetDialog.showProduct(
                    this@OmniProductDetailsActivity,
                    productDetailsViewModel,
                    item,
                    false,
                    binding.nestedScrollView
                )
            }
            val layoutManager = LinearLayoutManager(
                this@OmniProductDetailsActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            binding.rcyRelatedProducts.itemAnimator = DefaultItemAnimator()
            binding.rcyRelatedProducts.layoutManager = layoutManager
            binding.rcyRelatedProducts.adapter = adapter

            val layoutManagerMatchWith = LinearLayoutManager(
                this@OmniProductDetailsActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            binding.recyclerViewMatchProducts.itemAnimator = DefaultItemAnimator()
            binding.recyclerViewMatchProducts.layoutManager = layoutManagerMatchWith
            binding.recyclerViewMatchProducts.adapter = adapter
        } else {
            binding.lnrRelated.visibility = View.GONE
            binding.txtCompleteLook.visibility = View.GONE
        }
    }

    fun setProductDetails() {
        binding.tvProductName.text = (productData?.name?.split(' ')?.joinToString(" ") {
            it.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }
        })

        if (productData?.final_price != null && productData?.price != null) {
            if ((productData?.final_price)!!.toDouble() < (productData?.price)!!.toDouble()) {


                try {
                    val discount = 100 - Math.ceil(
                        (java.lang.Float.parseFloat(productData?.final_price!!.toString()) / java.lang.Float.parseFloat(
                            productData?.price!!
                        ) * 100).toDouble()
                    )

                    if (String.format(Locale.ENGLISH, "%.0f", discount) == "0") {
                        binding.lvDiscountView.visibility = View.GONE
                    } else {

                        binding.tvDiscount.text =
                            "-" + String.format(Locale.ENGLISH, "%.0f", discount) + "%"
                        binding.lvDiscountView.visibility = View.VISIBLE
                        binding.tvDiscountedPrice.text =
                            (Utils.getPriceFormatted(productData?.final_price, selectedCurrency))
                        binding.tvRegularPrice.text =
                            (Utils.getPriceFormatted(productData?.price, selectedCurrency))
                        binding.tvRegularPrice.paintFlags =
                            binding.tvRegularPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    }
                } catch (e: Exception) {
                    binding.lvDiscountView.visibility = View.GONE
                    binding.tvRegularPrice.text =
                        (Utils.getPriceFormatted(productData?.price, selectedCurrency))
                }


            } else {
                binding.lvDiscountView.visibility = View.GONE
                binding.tvRegularPrice.text =
                    Utils.getPriceFormatted(productData?.price, selectedCurrency)
            }

            if (productData?.final_price!!.toDouble() <= Utils.getAmazingPrice()) {
                binding.tvRegularPrice.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.final_price
                    )
                )
            }

        }
        binding.tvSku.text = (resources.getString(R.string.sku_colon) + " " + productData?.sku)
        val head =
            "<head><meta name='viewport' content='target-densityDpi=device-dpi'/><style>@font-face {font-family: 'Cairo-Regular';src: url('file:///android_asset/Cairo-Regular.ttf');}body {font-family: 'Cairo-Regular';padding:10;max-width:100%;text-align:right}</style></head>"
        val htmlData =
            "<html>$head<body style=\"font-family: Cairo-Regular;text-align: justify\">${productData!!.description}</body></html>"


        val ss = productData?.description?.replace(
            "span style=\"text-decoration: underline;",
            "<u>"
        )?.replace(";\"", "")?.replace(">\"", "")?.replace("</span>", "</u>")
            ?.replace("\n", "<br>")?.replace("</li>", "")?.replace("</h2>", "")
            ?.replace("<h2><strong>", "")?.replace("<li><strong>", "")
            ?.replace("</strong></h2>", "")?.replace("</strong></li>", "")
        binding.txtDescription.text = (Html.fromHtml(ss, null, MyTagHandler()))

        setColorSizeConfig()

    }

    private fun setColorSizeConfig() {

        if (productData?.is_salable == false) {
//            binding.txtOutOFStock.visibility = View.VISIBLE
            binding.btnAddToBag.visibility = View.GONE

            binding.txtOutOFStockBottom.visibility = View.VISIBLE
            binding.btnAddToBagBottom.visibility = View.GONE

            binding.txtOutOFStockAnchor.visibility = View.VISIBLE
            binding.btnAddToBagAnchor.visibility = View.GONE

        } else {
            binding.txtOutOFStock.visibility = View.GONE
//            binding.btnAddToBag.visibility = View.VISIBLE

            binding.txtOutOFStockBottom.visibility = View.GONE
            binding.btnAddToBagBottom.visibility = View.VISIBLE

            binding.txtOutOFStockAnchor.visibility = View.GONE
            binding.btnAddToBagAnchor.visibility = View.VISIBLE

        }
        try {
            setConfig(productData?.configurable_option!!, "Config")
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

    }

    fun setPagerImages(images: ArrayList<String>?) {
        if (images != null && images.size > 0) {
            binding.pager.removeAllViews()
            binding.pager.clearOnPageChangeListeners()
            arrListAllImages?.clear()
            arrListAllImages?.addAll(images!!)

            binding.pager.adapter = OmniImagePagerAdapter(
                this@OmniProductDetailsActivity,
                arrListAllImages!!
            )
            binding.pager.adapter!!.notifyDataSetChanged()

            if (binding.pager.adapter?.count!!.toInt() > 1) {

                binding.pager.setPageTransformer(
                    true
                ) { page, position -> /*  if (pager.currentItem == (pager.adapter?.count)?.minus(1) ?: 0) {
                                                                                                  page.translationX = 140f
                                                                                              } else {
                                                                                                  page.translationX = 0f
                                                                                              }*/
                }
            }


            strImageurl = images[0]
            strConfigImage = images[0]

            changeToolbarImage(images[0])
            binding.pager.currentItem = 0
            binding.viewPagerIndicator.resetPageNo()
            binding.viewPagerIndicator.visibleDotCounts = 7
            binding.viewPagerIndicator.noOfPages = images.size
            binding.viewPagerIndicator.onPageChange(binding.pager.currentItem)


            binding.pager.addOnPageChangeListener(object :
                ViewPager.OnPageChangeListener {

                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {


                    if (binding.pager.currentItem >= 0)
                        binding.viewPagerIndicator.onPageChange(binding.pager.currentItem)
                    else binding.viewPagerIndicator.onPageChange(0)
                }
            })
        }
    }

    fun setImageSwatch(imageUrl: String) {
        Glide.with(this)
            .asBitmap()
            .load(imageUrl)
            .override(
                resources.getDimension(R.dimen.fifteen_dp).toInt(),
                resources.getDimension(R.dimen.fifteen_dp).toInt()
            )
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false

                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }


            })
            .into(binding.imgSwatch)
    }

//    fun setColorAttributes(arrListConfig: ArrayList<ProductDetailsConfigurableOption>) {
//        configSize = arrListConfig.size
//        arrListAttr1 = ArrayList()
//        binding.rvAttribute1.layoutManager = LinearLayoutManager(
//            this,
//            LinearLayoutManager.HORIZONTAL,
//            false
//        )
//        binding.txtAttribute1.text = arrListConfig[0].type
//        arrListAttr1 =
//            arrListConfig[0].attributes as ArrayList<ProductDetailsAttribute>
//        var shouldSelect = false
//        attributeId1 = arrListConfig[0].attribute_id?.toString() ?: ""
//        attributeIdType1 = arrListConfig[0].type ?: ""
//        if (arrListAttr1!!.size == 1) {
//            arrListAttr1!![0].should_select = true
//        }
//        if (!arrListAttr1.isNullOrEmpty()) {
//
//            arrListAttr1?.forEach {
//                it.isSelected = it.should_select
//                if (it.should_select == true) {
//                    optionId1 = it.option_id ?: ""
//                    optionValue1 = it.value ?: ""
//                    binding.tvColorName.text =
//                        Utils.getDynamicStringFromApi(this, it.value)
//                    binding.imgSwatch.visibility = View.VISIBLE
//                    if (!this.isFinishing) {
//                        setImageSwatch(it.attribute_image_url)
//                    }
//                    shouldSelect = true
//                }
//            }
//
//            //settting adapter
//
//            val attributeSelectListener = object : OnAttributeSelectListener {
//                override fun onAttributeSelected(
//                    attribute: ProductDetailsAttribute,
//                    position: Int
//                ) {
//                    if (arrListAttr1!![position].isSelected == false) {
//                        //println("Here i am pager imagess 222   " + attribute.images + "  size   " + attribute.images!!.size)
//                        if (attribute.images != null && attribute.images.size > 0) {
//
//                            setPagerImages(attribute.images)
//
//                        }
//                        optionId1 = attribute.option_id ?: ""
//                        optionValue1 = attribute.value ?: ""
//                        optionId2 = ""
//                        optionValue2 = ""
//                        for (i in 0 until arrListAttr1!!.size) {
//                            arrListAttr1!![i].isSelected = position == i
//                        }
//                        binding.tvColorName.text = Utils.getDynamicStringFromApi(
//                            this@ProductDetailsActivity,
//                            attribute.value
//                        )
//                        binding.imgSwatch.visibility = View.VISIBLE
//                        if (!this@ProductDetailsActivity.isFinishing) {
//                            setImageSwatch(attribute.attribute_image_url)
//                        }
//
//                        changeConfig(strParentId, attributeId1, optionId1)
//                        configAdapter1?.notifyDataSetChanged()
//                    }
//                }
//
//
//            }
//            configAdapter1 = ConfigAdapter(
//                this,
//                arrListAttr1 as ArrayList<ProductDetailsAttribute>,
//                arrListConfig[0].attribute_code
//                    ?: "", attributeSelectListener
//            )
//            binding.rvAttribute1.adapter = configAdapter1
//            configAdapter1?.notifyDataSetChanged()
//        }
//    }
//
//    fun setSizeAttributes(arrListConfig: ArrayList<ProductDetailsConfigurableOption>) {
//        arrListAttr2 = ArrayList()
//        binding.rvAttribute2.layoutManager = LinearLayoutManager(
//            this,
//            LinearLayoutManager.HORIZONTAL,
//            false
//        )
//        attributeId2 = arrListConfig[1].attribute_id.toString()
//        attributeIdType2 = arrListConfig[1].type ?: ""
//        arrListAttr2 =
//            arrListConfig[1].attributes as ArrayList<ProductDetailsAttribute>
//        if (!arrListAttr2.isNullOrEmpty()) {
//            binding.txtAttribute2.visibility = View.VISIBLE
//            binding.rvAttribute2.visibility = View.VISIBLE
//            configAdapter2 = ConfigAdapter(this,
//                arrListAttr2!!,
//                arrListConfig[1].attribute_code
//                    ?: "",
//                object : OnAttributeSelectListener {
//                    override fun onAttributeSelected(
//                        attribute: ProductDetailsAttribute,
//                        position: Int
//                    ) {
//                        if (attribute.isSelected == false && attribute.isAvailable == true && optionId1.isNotEmpty()) {
//                            binding.txtOutOFStockBottom.visibility = View.GONE
//                            binding.btnAddToBagBottom.visibility = View.VISIBLE
//                            for (i in 0 until arrListAttr2?.size!!) {
//                                arrListAttr2!![i].isSelected = position == i
//                            }
//                            optionId2 = arrListAttr2?.get(position)?.option_id.toString()
//                            optionValue2 = arrListAttr2?.get(position)?.value.toString()
//                            configAdapter2?.notifyDataSetChanged()
//                            binding.pager.adapter = ImagePagerAdapter(
//                                this@ProductDetailsActivity,
//                                arrListAllImages!!
//                            )
//                            binding.pager.adapter!!.notifyDataSetChanged()
//                            changeConfig(
//                                strParentId,
//                                "$attributeId1,$attributeId2",
//                                "$optionId1,$optionId2"
//                            )
//                        } else if (attribute.isAvailable == false) {
//                            binding.txtOutOFStockBottom.visibility = View.VISIBLE
//                            binding.btnAddToBagBottom.visibility = View.GONE
//                            for (i in 0 until arrListAttr2?.size!!) {
//                                arrListAttr2!![i].isSelected = position == i
//                            }
//                            optionId2 = arrListAttr2?.get(position)?.option_id.toString()
//                            optionValue2 = arrListAttr2?.get(position)?.value.toString()
//                            configAdapter2?.notifyDataSetChanged()
//                        }
//                    }
//                })
//        }
////            else {
////                binding.txtAttribute2.visibility = View.GONE
////                binding.rvAttribute2.visibility = View.GONE
////            }
//
//        binding.rvAttribute2.adapter = configAdapter2
//        configAdapter2?.notifyDataSetChanged()
//    }

    ///set configurations
    private fun setConfig(
        arrListConfig: ArrayList<ProductDetailsConfigurableOption>,
        strFrom: String
    ) {
        arrListAttr1 = ArrayList()
        arrListAttr2 = ArrayList()
        configSize = arrListConfig.size
        binding.rvAttribute1.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.rvAttribute2.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        if (arrListConfig.size == 2) {
            binding.txtAttribute1.text = arrListConfig[0].type
            binding.txtAttribute2.text = arrListConfig[1].type


            arrListAttr1 =
                arrListConfig[0].attributes as ArrayList<ProductDetailsAttribute>
            var shouldSelect = false
            attributeId1 = arrListConfig[0].attribute_id?.toString() ?: ""
            attributeIdType1 = arrListConfig[0].type ?: ""
            attributeId2 = arrListConfig[1].attribute_id.toString()
            attributeIdType2 = arrListConfig[1].type ?: ""
//            binding.txtSelect.text =
//                resources.getString(R.string.select) + " " + attributeIdType1 + "/" + attributeIdType2
            /**
             *
             * Check if  first attribute should be selected
             */

            if (arrListAttr1!!.size == 1) {
                arrListAttr1!![0].should_select = true
            }
            if (!arrListAttr1.isNullOrEmpty()) {

                for (attr1 in arrListAttr1 as ArrayList<ProductDetailsAttribute>) {

                    attr1.isSelected = attr1.should_select
                    if (attr1.should_select == true) {
                        optionId1 = attr1.option_id ?: ""
                        optionValue1 = attr1.value ?: ""
                        binding.tvColorName.text =
                            Utils.getDynamicStringFromApi(this, attr1.value)
                        binding.imgSwatch.visibility = View.VISIBLE
                        if (!this.isFinishing) {
                            setImageSwatch(attr1.attribute_image_url)

                        }

                        shouldSelect = true
                    }
                }
            }

            /**
             * On first attribute click
             */
            if (!arrListAttr1.isNullOrEmpty()) {
//                binding.txtAttribute1.visibility = View.VISIBLE
//                binding.rvAttribute1.visibility = View.VISIBLE
                configAdapter1 = ConfigAdapter(this,
                    arrListAttr1 as ArrayList<ProductDetailsAttribute>,
                    arrListConfig[0].attribute_code
                        ?: "",
                    object : OnAttributeSelectListener {
                        override fun onAttributeSelected(
                            attribute: ProductDetailsAttribute,
                            position: Int
                        ) {
                            if (arrListAttr1!![position].isSelected == false) {
                                //println("Here i am pager imagess 222   " + attribute.images + "  size   " + attribute.images!!.size)
                                if (attribute.images != null && attribute.images.size > 0) {

                                    setPagerImages(attribute.images)

                                }
                                optionId1 = attribute.option_id ?: ""
                                optionValue1 = attribute.value ?: ""
                                optionId2 = ""
                                optionValue2 = ""
                                for (i in 0 until arrListAttr1!!.size) {
                                    arrListAttr1!![i].isSelected = position == i
                                }
                                binding.tvColorName.text = Utils.getDynamicStringFromApi(
                                    this@OmniProductDetailsActivity,
                                    attribute.value
                                )
                                binding.imgSwatch.visibility = View.VISIBLE
                                if (!this@OmniProductDetailsActivity.isFinishing) {
                                    setImageSwatch(attribute.attribute_image_url)
                                }

                                changeConfig(strParentId, attributeId1, optionId1)
                                configAdapter1?.notifyDataSetChanged()
                            }

                        }
                    })

            }
//            else {
//                binding.txtAttribute1.visibility = View.GONE
//                binding.rvAttribute1.visibility = View.GONE
//            }
            binding.rvAttribute1.adapter = configAdapter1
            configAdapter1?.notifyDataSetChanged()

            /**
             * On second attribute click
             */
            arrListAttr2 =
                arrListConfig[1].attributes as ArrayList<ProductDetailsAttribute>
            if (!arrListAttr2.isNullOrEmpty()) {
                binding.txtAttribute2.visibility = View.VISIBLE
                binding.rvAttribute2.visibility = View.VISIBLE

                try {
                    arrListAttr2?.get(0)?.isSelected = true
                    optionId2 = arrListAttr2?.get(0)?.option_id.toString()
                    optionValue2 = arrListAttr2?.get(0)?.value.toString()
                    if (arrListAttr2?.get(0)?.images != null && arrListAttr2?.get(0)?.images?.size!! > 0) {
                        binding.pager.adapter = OmniImagePagerAdapter(
                            this@OmniProductDetailsActivity,
                            arrListAllImages!!
                        )
                        binding.pager.adapter!!.notifyDataSetChanged()
                    }
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
                configAdapter2 = ConfigAdapter(this,
                    arrListAttr2!!,
                    arrListConfig[1].attribute_code
                        ?: "",
                    object : OnAttributeSelectListener {
                        override fun onAttributeSelected(
                            attribute: ProductDetailsAttribute,
                            position: Int
                        ) {
                            if (attribute.isSelected == false && attribute.isAvailable == true && optionId1.isNotEmpty()) {
                                binding.txtOutOFStockBottom.visibility = View.GONE
                                binding.btnAddToBagBottom.visibility = View.VISIBLE
                                for (i in 0 until arrListAttr2?.size!!) {
                                    arrListAttr2!![i].isSelected = position == i
                                }
                                optionId2 = arrListAttr2?.get(position)?.option_id.toString()
                                optionValue2 = arrListAttr2?.get(position)?.value.toString()
                                configAdapter2?.notifyDataSetChanged()
                                if (attribute.images != null && attribute.images.size > 0) {
                                    binding.pager.adapter = OmniImagePagerAdapter(
                                        this@OmniProductDetailsActivity,
                                        arrListAllImages!!
                                    )
                                    binding.pager.adapter!!.notifyDataSetChanged()
                                }
                                changeConfig(
                                    strParentId,
                                    "$attributeId1,$attributeId2",
                                    "$optionId1,$optionId2"
                                )
                            } else if (attribute.isAvailable == false) {
                                binding.txtOutOFStockBottom.visibility = View.VISIBLE
                                binding.btnAddToBagBottom.visibility = View.GONE
                                for (i in 0 until arrListAttr2?.size!!) {
                                    arrListAttr2!![i].isSelected = position == i
                                }
                                optionId2 = arrListAttr2?.get(position)?.option_id.toString()
                                optionValue2 = arrListAttr2?.get(position)?.value.toString()
                                configAdapter2?.notifyDataSetChanged()
                            }
                        }
                    })
            }
//            else {
//                binding.txtAttribute2.visibility = View.GONE
//                binding.rvAttribute2.visibility = View.GONE
//            }

            binding.rvAttribute2.adapter = configAdapter2
            configAdapter2?.notifyDataSetChanged()
            //if first attribute has single product or selected call config change api
            if (shouldSelect) {
                changeConfig(productData?.id, attributeId1, optionId1)
            }

        } else {

            if (arrListConfig.size == 1) {
                arrListAttr1 = ArrayList()
//                binding.rvAttribute1.layoutManager = LinearLayoutManager(
//                    this,
//                    LinearLayoutManager.HORIZONTAL,
//                    false
//                )
//                binding.rvAttribute2.layoutManager = LinearLayoutManager(
//                    this@ProductDetailsActivity,
//                    LinearLayoutManager.HORIZONTAL,
//                    false
//                )
                arrListAttr1 =
                    arrListConfig[0].attributes as ArrayList<ProductDetailsAttribute>
                attributeId1 = (arrListConfig[0].attribute_id.toString() ?: "")
                attributeIdType1 = arrListConfig[0].type ?: ""
//                binding.txtSelect.text =
//                    resources.getString(R.string.select) + " " + attributeIdType1
                var shouldSelect = false
                if (!arrListAttr1.isNullOrEmpty()) {
                    for (attr1 in arrListAttr1 as ArrayList<ProductDetailsAttribute>) {
                        attr1.isSelected = attr1.should_select
                        if (attr1.should_select == true) {
                            optionId1 = attr1.option_id ?: ""
                            optionValue1 = attr1.value ?: ""
                            binding.tvColorName.text = Utils.getDynamicStringFromApi(
                                this@OmniProductDetailsActivity,
                                attr1.value
                            )
                            binding.imgSwatch.visibility = View.VISIBLE
                            if (!this@OmniProductDetailsActivity.isFinishing) {
                                setImageSwatch(attr1.attribute_image_url)
                            }
                            shouldSelect = true
                        }
                    }
                }

                if (!arrListAttr1.isNullOrEmpty()) {
                    binding.txtAttribute1.visibility = View.VISIBLE
                    binding.rvAttribute1.visibility = View.VISIBLE
                    configAdapter1 = ConfigAdapter(this@OmniProductDetailsActivity,
                        arrListAttr1 as ArrayList<ProductDetailsAttribute>,
                        arrListConfig[0].attribute_code
                            ?: "",
                        object : OnAttributeSelectListener {
                            override fun onAttributeSelected(
                                attribute: ProductDetailsAttribute,
                                position: Int
                            ) {
                                if (arrListAttr1!![position].isSelected == false) {
                                    if (attribute.images != null && attribute.images.size > 0) {
                                        setPagerImages(attribute.images)

                                    }
                                    optionId1 = attribute.option_id ?: ""
                                    optionValue1 = attribute.value ?: ""
                                    optionId2 = ""
                                    optionValue2 = ""
                                    for (i in 0 until arrListAttr1!!.size) {
                                        arrListAttr1!![i].isSelected = position == i
                                    }
                                    binding.tvColorName.text = Utils.getDynamicStringFromApi(
                                        this@OmniProductDetailsActivity,
                                        attribute.value
                                    )
                                    binding.imgSwatch.visibility = View.VISIBLE
                                    if (!this@OmniProductDetailsActivity.isFinishing) {
                                        setImageSwatch(attribute.attribute_image_url)
                                    }
                                    changeConfig(strParentId, attributeId1, optionId1)
                                    configAdapter1?.notifyDataSetChanged()
                                }

                            }
                        })

                }
//                else {
//                    binding.txtAttribute1.visibility = View.GONE
//                    binding.rvAttribute1.visibility = View.GONE
//                }
                binding.rvAttribute1.adapter = configAdapter1
                configAdapter1?.notifyDataSetChanged()
                if (shouldSelect) {
                    changeConfig(productData?.id, attributeId1, optionId1)
                }
            }
        }


    }

    fun showProductAddedView() {
        val state =
            if (productAddedSheetBehaviour.state == BottomSheetBehavior.STATE_EXPANDED)
                BottomSheetBehavior.STATE_EXPANDED
            else
                BottomSheetBehavior.STATE_EXPANDED
        binding.lvAnchor.visibility = View.GONE
        productAddedSheetBehaviour.state = state
        updateBadgeToCart(true)
    }


    private fun changeConfig(entityId: String?, attributeId: String, optionId: String) {

        val configAttr = ConfigRequestModel(
            strProductId,
            attributeId,
            optionId,
            selectedLanguage
        )
        productDetailsViewModel.changeConfig(selectedLanguage, configAttr)
        productDetailsViewModel.responseChangeConfig.observe(this) {
            when (it) {
                is Resource.Success -> {
                    handleConfigResponse(it.data!!)
                    dismissProgress()
                }

                is Resource.Error -> {
                    binding.txtOutOFStock.visibility = View.VISIBLE
                    binding.btnAddToBag.visibility = View.GONE
                    binding.txtOutOFStockBottom.visibility = View.VISIBLE
                    binding.btnAddToBagBottom.visibility = View.GONE

                    binding.txtOutOFStockAnchor.visibility = View.VISIBLE
                    binding.btnAddToBagAnchor.visibility = View.GONE
                    for (att2 in this.arrListAttr2!!) {
                        if (optionId2.isNullOrEmpty()) {
                            att2.isSelected = false
                            att2.isAvailable = false
                        }

                    }
                    configAdapter2?.notifyDataSetChanged()
                    dismissProgress()
                }
                is Resource.Loading -> {
                    showProgress()
                }
            }
        }

    }

    private fun handleConfigResponse(configResponseModel: ConfigResponseModel) {
        if (configResponseModel.status == "200") {
            if (!configResponseModel.data.isNullOrEmpty()) {
                if (!configResponseModel.data[0].attributes.isNullOrEmpty()) {
                    val availableAttributes =
                        configResponseModel.data[0].attributes //get available sizes


                    if (!arrListAttr2.isNullOrEmpty()) {
                        for (att2 in this.arrListAttr2 as ArrayList<ProductDetailsAttribute>) {
                            att2.isAvailable = false
                            att2.isSelected = false
                            if (!availableAttributes.isNullOrEmpty()) {
                                availableAttributes.forEach {
                                    if (it.option_id == att2.option_id) {
                                        att2.isAvailable = true

                                        //if single size available mark it selected
                                        if (availableAttributes.size == 1) {
                                            att2.isSelected = true
                                        }
//                                        else {
//                                            arrListAttr2?.get(0)?.isSelected = true
//                                        }
//                                        optionId2 = it.option_id
//                                        optionValue2 = it.value!!
                                    }
                                }
                            }

                        }

                        //if single size available call api

                        try {
                            if (availableAttributes?.size == 1) {
                                optionId2 = availableAttributes[0].option_id.toString()
                                optionValue2 = availableAttributes[0].value.toString()


                            }
                        } catch (exception: Exception) {
                            exception.printStackTrace()
                        }

//                        changeConfig(
//                            strParentId,
//                            "$attributeId1,$attributeId2",
//                            "$optionId1,$optionId2"
//                        )

                    }
                    try {
                        if (!configResponseModel.data[0].sku.isNullOrEmpty()) {
                            binding.tvSku.visibility = View.VISIBLE
                            binding.tvSku.text =
                                (resources.getString(R.string.sku_colon) + " " + configResponseModel.data[0].sku)
                        }
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                    configAdapter2?.notifyDataSetChanged()
                } else {

                    arrListAttr2?.forEach {
                        if (it.value.equals(optionValue2)) {
                            it.isSelected = true
                            configAdapter2?.notifyDataSetChanged()
                            binding.tvSku.visibility = View.VISIBLE
                            binding.tvSku.text =
                                (resources.getString(R.string.sku_colon) + " " + configResponseModel.data[0].sku)
                        }
                    }

                    strConfigProdId =
                        if (!configResponseModel.data[0].entity_id.isNullOrEmpty()) configResponseModel.data[0].entity_id!! else ""
                    intRemainingQty = configResponseModel.data[0].quantity!!
                    strConfigImage = if (!configResponseModel.data[0].image.isNullOrEmpty()) {
                        configResponseModel.data[0].image?.get(0).toString()
                    } else {
                        productData?.image ?: ""
                    }
                    if (!configResponseModel.data[0].image.isNullOrEmpty()) {
                        setPagerImages(configResponseModel.data[0].image!!)
//                    binding.pager.removeAllViews()
//                    binding.pager.clearOnPageChangeListeners()
//                    arrListAllImages?.clear()
//                    for (i in 0 until configResponseModel.data[0].image!!.size) {
//                        configResponseModel.data[0].image!![i]?.let { arrListAllImages?.add(it) }
//                    }
//                    binding.pager.adapter = ImagePagerAdapter(this, arrListAllImages!!)
//                    binding.pager.adapter!!.notifyDataSetChanged()
//                    if (binding.pager.adapter?.count!!.toInt() > 1) {
//                        binding.pager.setPageTransformer(
//                            true
//                        ) { page, position ->
//                        }
//                    }
//
//                    changeToolbarImage(arrListAllImages!![0])
//                    binding.pager.currentItem = 0
//                    binding.viewPagerIndicator.resetPageNo()
//                    binding.viewPagerIndicator.visibleDotCounts = 7
//                    binding.viewPagerIndicator.noOfPages = arrListAllImages!!.size
//                    binding.viewPagerIndicator.onPageChange(binding.pager.currentItem)
//
//                    binding.pager.addOnPageChangeListener(object :
//                        ViewPager.OnPageChangeListener {
//
//                        override fun onPageScrollStateChanged(state: Int) {
//                        }
//
//                        override fun onPageScrolled(
//                            position: Int,
//                            positionOffset: Float,
//                            positionOffsetPixels: Int
//                        ) {
//                        }
//
//                        override fun onPageSelected(position: Int) {
//
//
//                            if (binding.pager.currentItem >= 0)
//                                binding.viewPagerIndicator.onPageChange(binding.pager.currentItem)
//                            else binding.viewPagerIndicator.onPageChange(0)
//                        }
//                    })

                    }
                }

                if (!configResponseModel.data[0].sku.isNullOrEmpty()) {
                    binding.tvSku.visibility = View.VISIBLE
                    binding.tvSku.text =
                        (resources.getString(R.string.sku_colon) + " " + configResponseModel.data[0].sku)
                }
            }
            if (intRemainingQty > 0) {
                binding.txtOutOFStock.visibility = View.GONE
                binding.btnAddToBag.visibility = View.VISIBLE
                binding.txtOutOFStockBottom.visibility = View.GONE
                binding.btnAddToBagBottom.visibility = View.VISIBLE
                binding.txtOutOFStockAnchor.visibility = View.GONE
                binding.btnAddToBagAnchor.visibility = View.VISIBLE
            } else {
                binding.txtOutOFStock.visibility = View.VISIBLE
                binding.btnAddToBag.visibility = View.GONE
                binding.txtOutOFStockBottom.visibility = View.VISIBLE
                binding.btnAddToBagBottom.visibility = View.GONE
                binding.txtOutOFStockAnchor.visibility = View.VISIBLE
                binding.btnAddToBagAnchor.visibility = View.GONE
            }
        } else {
            binding.txtOutOFStock.visibility = View.VISIBLE
            binding.btnAddToBag.visibility = View.GONE
            binding.txtOutOFStockBottom.visibility = View.VISIBLE
            binding.btnAddToBagBottom.visibility = View.GONE

            binding.txtOutOFStockAnchor.visibility = View.VISIBLE
            binding.btnAddToBagAnchor.visibility = View.GONE
            for (att2 in this.arrListAttr2!!) {
                if (optionId2.isNullOrEmpty()) {
                    att2.isSelected = false
                    att2.isAvailable = false
                }

            }
            configAdapter2?.notifyDataSetChanged()
        }

    }


    fun handleOutOfStock() {
        if (productData?.is_salable == false) {
            binding.btnAddToBag.visibility = View.GONE
            binding.btnAddToBagBottom.visibility = View.GONE
            binding.btnAddToBagAnchor.visibility = View.GONE
        } else {
            binding.btnAddToBag.visibility = View.VISIBLE
            binding.btnAddToBagBottom.visibility = View.VISIBLE
            binding.btnAddToBagAnchor.visibility = View.VISIBLE

            if (productData?.type.equals("single", true)) {
                binding.btnAddToBag.visibility = View.VISIBLE
                binding.btnAddToBagBottom.visibility = View.VISIBLE
                binding.btnAddToBagAnchor.visibility = View.VISIBLE
            } else {
                binding.btnAddToBag.visibility = View.GONE
                binding.btnAddToBagBottom.visibility = View.GONE
                binding.btnAddToBagAnchor.visibility = View.GONE
            }
        }
    }

    ///update badge of product
    private fun updateBadgeToCart(show_btn: Boolean) {
        val cartCount = sharedpreferenceHandler.getData(SharedpreferenceHandler.CART_COUNT, 0)
        if (cartCount > 0) {
            if (show_btn) {
                binding.toolbar1.txtCheckoutBtn.visibility = View.VISIBLE
            } else {
                binding.toolbar1.txtCheckoutBtn.visibility = View.GONE
            }

            binding.toolbar1.txtCartCount.visibility = View.VISIBLE
        } else {
            binding.toolbar1.txtCheckoutBtn.visibility = View.GONE
            binding.toolbar1.txtCartCount.visibility = View.INVISIBLE
        }
        binding.toolbar1.txtCartCount.text = cartCount.toString()
    }

    fun getAllRecentProducts() {

        productDetailsViewModel.getRecentProductsFromDb().observe(this) {
            it.forEach {
                arrListRecent = ArrayList<RecentProduct>()
                arrListRecent!!.add(it)
                if (it.item_id == strParentId) {
                    arrListRecent.remove(it)
                }
                arrListRecent.reverse()
                setRelatedProducts()
            }
        }
    }

    private fun setRelatedProducts() {
        if (!arrListRecent.isNullOrEmpty() && arrListRecent.size > 0) {
            //arrListRecent.reverse()
            val adapter = OmniRecentProductAdapter(
                selectedCurrency,
                recentWidth,
                recentHeight,
                productWidthHalf,
                this@OmniProductDetailsActivity,
                arrListRecent!!
            )
            val layoutManager = LinearLayoutManager(
                this@OmniProductDetailsActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            binding.rcyRecentView.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
            binding.rcyRecentView.layoutManager = layoutManager
            binding.rcyRecentView.adapter = adapter
            binding.lnrRecent.visibility = View.VISIBLE
            isRecentSearchAvailable = true
            if (isMatchedWithAvailable && isRecentSearchAvailable) {
                binding.viewDivider.visibility = View.VISIBLE
            } else {
                binding.viewDivider.visibility = View.GONE
            }
        } else {
            isRecentSearchAvailable = false
            binding.lnrRecent.visibility = View.GONE
        }
    }

    fun omniScanItem(skuCode: String) {
        val priceListId = sharedpreferenceHandler.getData(SharedpreferenceHandler.PRICE_LIST_ID, 0)
        val storeId = sharedpreferenceHandler.getData(SharedpreferenceHandler.STORE_ID, 0)
        productDetailsViewModel.omniScanItem(
            skuCode,
            storeId!!.toString(),
            priceListId!!.toString()
        )
        productDetailsViewModel.responseScanItemOmni.observe(this) {
            when (it) {
                is Resource.Success -> {
                    dismissProgress()
                    if (it.data?.statusCode == 1) {
                        omniScannedItem = it.data
                        addProductToDb()
                        updateBadgeToCart(false)
                        showProductAddedView()
                    } else {
                        Utils.showSnackbar(binding.nestedScrollView, it.data?.displayMessage!!)
                    }

                   productDetailsViewModel.responseScanItemOmni.value=null
                }

                is Resource.Error -> {
                    dismissProgress()
                    try {
                        val jsonObj = JSONObject(it.message)
                        val message = jsonObj.getString("message")
                        Utils.showSnackbar(binding.root, message)
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                        Utils.showSnackbar(binding.root, it.message.toString())
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
            omniScannedItem?.skuMasterTypesList?.get(0)?.imageUrl = strConfigImage
            omniScannedItem?.skuMasterTypesList?.get(0)?.fromRiva = true
            omniScannedItem?.skuMasterTypesList?.get(0)?.productId=
                strParentId!!
//            omniScannedItem?.skuMasterTypesList?.get(0)?.id = (0..100).shuffled().last()
            productDetailsViewModel.addOmniProductToPrefs(omniScannedItem?.skuMasterTypesList?.get(0)!!,this)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    ///add to cart api call
    private fun addToCart() {
        showProgress()
        if (strProductId.isEmpty() || strParentId!!.isEmpty())
            strProductId = productData?.id!!

        val addToCartRequest = AddToCartRequest(
            AddToCartRequest.CartData(
                strUserId,
                strProductId,
                strParentId,
                "1",
                ""
            )
        )
        productDetailsViewModel.addToCart(selectedLanguage, selectedCurrency, addToCartRequest)
        productDetailsViewModel.responseAddToCart.observe(this) {
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

    private fun handleAddToCartResponse(cartModel: AddToCartResponse) {

        strProductId = strProductIdReusable
        dismissProgress()
        try {

            if (cartModel.status == "200") {

                showProductAddedView()
                if (productData!!.type.equals("configurable", true))
                    productData!!.id = strConfigProdId   // For offline cart

                productData!!.remaining_qty = intRemainingQty
                var cartCount =
                    sharedpreferenceHandler.getData(SharedpreferenceHandler.CART_COUNT, 0)
                cartCount++
                sharedpreferenceHandler.saveData(SharedpreferenceHandler.CART_COUNT, cartCount)
                updateBadgeToCart(false)

                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                    val a = bottomSheetBehavior
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    a.addBottomSheetCallback(object :
                        BottomSheetBehavior.BottomSheetCallback() {
                        override fun onSlide(bottomSheet: View, slideOffset: Float) {
                        }

                        override fun onStateChanged(bottomSheet: View, newState: Int) {
                            when (newState) {
                                BottomSheetBehavior.STATE_HIDDEN ->
                                    if (!isAlertVisible) {
                                        isAlertVisible = true
                                        Handler().postDelayed({
//                                            continueShopping()
                                        }, 100)
                                    }
                            }
                        }
                    })
                } else {
                    if (!isAlertVisible) {
//                        continueShopping()
                    }
                }

            } else {
                Snackbar.make(binding.root, cartModel.message, Snackbar.LENGTH_LONG)
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


//    /// add and delete wishlist api call
//    private fun wishListToggle(
//        strType: String,
//        name: String,
//        strWishProductID: String,
//        strWishFinalPrice: String
//    ) {
//        if (strType == "Add") {
//
//            val addData = WishlistRequest.AddToWishList(
//                strUserId!!, strWishProductID
//            )
//            addToWishlistApi(addData)
//        } else {
//
//            val removeData = WishlistRequest.RemoveFromWishList(
//                strUserId!!,
//                strWishItemID
//            )
//            removeFromWishlistApi(removeData)
//        }
//    }
//
//    fun addToWishlistApi(addToWishlistRequest: WishlistRequest.AddToWishList) {
//        productDetailsViewModel.addToWishlist(addToWishlistRequest)
//        productDetailsViewModel.responseAddToWishlist.observe(this) {
//            when (it) {
//                is Resource.Success -> {
//                    rivaLookBookActivity?.showProgressBar(false)
//                    dismissProgress()
//                    it.data.let {
//                        it?.data?.forEach {
//                            productDetailsViewModel.addProductToWishlistDb(
//                                Wishlist(
//                                    it.wishlist_item_id!!,
//                                    it.product_id!!,
//                                    it.name,
//                                    it.description,
//                                    it.regular_price,
//                                    it.final_price,
//                                    it.is_saleable,
//                                    it.image_url,
//                                    it.product?.sku
//                                )
//                            )
//                        }
//                        getWishListIds()
//                    }
//                }
//                is Resource.Error -> {
//                    rivaLookBookActivity?.showProgressBar(false)
//                }
//                is Resource.Loading -> {
//                    rivaLookBookActivity?.showProgressBar(true)
//                }
//            }
//        }
//    }
//
//    fun removeFromWishlistApi(removeFromWishlist: WishlistRequest.RemoveFromWishList) {
//        productDetailsViewModel.removeFromWishlist(removeFromWishlist)
//        productDetailsViewModel.responseRemoveFromWishlist.observe(this) {
//            when (it) {
//                is Resource.Success -> {
//                    rivaLookBookActivity?.showProgressBar(false)
//                    dismissProgress()
//                    productDetailsViewModel.deleteAllWishlistItemsDb()
//                    it.data.let {
//                        it?.data?.forEach {
//                            productDetailsViewModel.addProductToWishlistDb(
//                                Wishlist(
//                                    it.wishlist_item_id!!,
//                                    it.product_id!!,
//                                    it.name,
//                                    it.description,
//                                    it.regular_price,
//                                    it.final_price,
//                                    it.is_saleable,
//                                    it.image_url,
//                                    it.product?.sku
//                                )
//                            )
//                        }
//                        getWishListIds()
//                    }
//                    getWishListIds()
//                }
//
//                is Resource.Error -> {
//                    rivaLookBookActivity?.showProgressBar(false)
//                }
//                is Resource.Loading -> {
//                    rivaLookBookActivity?.showProgressBar(true)
//                }
//            }
//        }
//    }
//
//    fun getWishListIds() {
//        productDetailsViewModel.getWishlistProductIdsFromDb().observe(this) {
//            arrListWishlist = ArrayList()
//            it.forEach {
//                arrListWishlist!!.add(it)
//            }
//        }
//    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        init()
    }

    ///loading dialog
    private fun showProgress() {

        if (!this@OmniProductDetailsActivity.isFinishing) {
            if (loading == null) {
                loading = Dialog(this@OmniProductDetailsActivity, R.style.AppTheme)
                loading?.setContentView(R.layout.custom_loading_view)
                loading?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                loading?.setCanceledOnTouchOutside(false)
                loading?.show()
            } else {
                if (loading?.isShowing == true) {

                } else {
                    loading?.show()
                }
            }
        }
    }

    private fun dismissProgress() {
        if (loading != null && loading?.isShowing == true)
            loading?.dismiss()
    }

    override fun onResume() {
        super.onResume()
        updateBadgeToCart(true)
//        getAllRecentProducts()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (loading != null && loading!!.isShowing) {
            loading?.dismiss()
        }

        if (productData != null) {
            val recentModel = RecentlyViewModel(
                strParentId!!,
                productData?.id ?: "",
                productData?.name
                    ?: "",
                productData?.image ?: "",
                productData?.price.toString(),
                productData?.final_price.toString(),
                "yes",
                productData?.is_wishlist
                    ?: false
            )
            addRecentProducttoDb()
        }
    }

    fun addRecentProducttoDb() {
        try {
            val recentProduct = RecentProduct(
                1,
                productData!!.id,
                productData!!.name,
                productData!!.description,
                productData!!.price,
                productData!!.final_price,
                productData!!.is_salable?.toString(),
                productData!!.image,
                productData!!.sku
            )
            productDetailsViewModel.addProductToRecentDb(recentProduct)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                } else if (productAddedSheetBehaviour.state == BottomSheetBehavior.STATE_EXPANDED) {
                    productAddedSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
                } else {
                    finish()
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onStop() {
        super.onStop()
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        if (productAddedSheetBehaviour.state == BottomSheetBehavior.STATE_EXPANDED) {
            productAddedSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }


    override fun onBackPressed() {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else if (productAddedSheetBehaviour.state == BottomSheetBehavior.STATE_EXPANDED) {
            productAddedSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (intent.hasExtra("image")) {
                    binding.imageProduct.visibility = View.VISIBLE
                    supportFinishAfterTransition()
                } else {
                    finish()
                }

            } else {
                super.onBackPressed()

            }
            super.onBackPressed()
        }

    }

    private fun isVisible(view: View?): Boolean {
//        if (view == null) {
//            return false
//        }
//        if (!view.isShown) {
//            return false
//        }
//        val actualPosition = Rect()
//        binding.dummyView.getGlobalVisibleRect(actualPosition)
//        Rect(
//            0,
//            0,
//            Utils.getDeviceWidth(this),
//            Utils.getDeviceHeight(this)
//        )
//        if (binding.dummyView.getLocalVisibleRect(actualPosition)) {
//            // Any portion of the imageView, even a single pixel, is within the visible window
//            //println("Dummy visible")
//            binding.lnrSizeAnchor.visibility = View.GONE
//
//        } else {
//            //println("Dummy invisible")
//            binding.lnrSizeAnchor.visibility = View.VISIBLE
//
//            // NONE of the imageView is within the visible window
//        }
        return true
    }

}