package com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing.match_with

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.armada.riva.ProductDetail.adapter.ConfigAdapter
import com.armada.storeapp.R
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.request.AddToCartRequest
import com.armada.storeapp.data.model.request.ConfigRequestModel
import com.armada.storeapp.data.model.response.*
import com.armada.storeapp.databinding.ActivityMatchWithBinding
import com.armada.storeapp.ui.base.BaseActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.AddToCartBottomSheetDialog
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.interfaces.OnAttributeSelectListener
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing.match_with.adapter.MatchWithAdapter
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

@AndroidEntryPoint
class MatchWithActivity : BaseActivity() {

    private var selectedLanguage: String = "en"
    private var selectedCurrency = "USD"
    private var strSizeGuide: String? = null
    lateinit var matchWithAdapter: MatchWithAdapter
    private var selectedLookToBuy: Related? = null
    private var matchWithProducts: ArrayList<Related>? = null
    private var strFirstImage: String = ""
    private var strParentId: String? = null
    lateinit var activityMatchWithBinding: ActivityMatchWithBinding
    lateinit var matchWithViewModel: MatchWithViewModel
    lateinit var sharedpreferenceHandler: SharedpreferenceHandler
    private var strUserId = ""
    private var isLoggedIn = false
    private var productId: String? = null
    private var loading: Dialog? = null
    private var productData: ProductDetailsData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMatchWithBinding = ActivityMatchWithBinding.inflate(layoutInflater)
        setContentView(activityMatchWithBinding?.root)
        matchWithViewModel =
            ViewModelProvider(this).get(MatchWithViewModel::class.java)
        sharedpreferenceHandler = SharedpreferenceHandler(this)
        setupToolbar()
        init()
        setBagCount()
    }


    fun init() {

        strUserId = sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_USER_ID, "")!!
        selectedLanguage =
            sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_SELECTED_COUNTRY, "en")!!
        selectedCurrency =
            sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_SELECTED_CURRENCY, "USD")!!
        isLoggedIn =
            sharedpreferenceHandler.getData(
                SharedpreferenceHandler.RIVA_USER_LOGGED_IN, false
            )

        intent?.let {
            if (it.hasExtra("id") && it.getStringExtra("id") != null) {
                strParentId = intent.getStringExtra("id").toString()
                productId = intent.getStringExtra("id").toString()
                getProductDetails(productId!!)
            } else {
                productId = "0"
            }

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
                        .into(activityMatchWithBinding.imageProduct)
                    startPostponedEnterTransition()
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        getProductDetails(productId!!)
    }

    private fun setupToolbar() {
        setSupportActionBar(activityMatchWithBinding?.toolbar1?.root)
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
    }

    fun getProductDetails(productId: String) {
        matchWithViewModel.getProductDetails(selectedLanguage, selectedCurrency, productId)
        matchWithViewModel.responseProductDetails.observe(this) {
            when (it) {
                is Resource.Success -> {
                    handleProductDetailsResponse(it.data!!)
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

    private fun handleProductDetailsResponse(data: ProductDetailsResponse) {
        //Images
        productData = data?.data
        if (!productData?.size_chart.isNullOrEmpty()) {
            strSizeGuide = productData?.size_chart ?: ""
        }
        if (productData?.related != null && productData?.related!!.size > 0) {
            activityMatchWithBinding.cvNoResult.visibility = View.GONE
            activityMatchWithBinding.rvMatchItems.visibility = View.VISIBLE
            matchWithProducts = productData?.related
            matchWithAdapter = MatchWithAdapter(
                selectedCurrency,
                this@MatchWithActivity,
                matchWithProducts
            )
            matchWithAdapter.onProductAddedToCart = { matchProduct ->
                selectedLookToBuy = matchProduct
//                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
//                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
//                }
                MatchWithAddToCartBottomSheetDialog.showProduct(
                    this@MatchWithActivity,
                    matchWithViewModel,
                    matchProduct,
                    false,
                    activityMatchWithBinding.nestedScrollView
                )
            }
            val layoutManager = LinearLayoutManager(
                this@MatchWithActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            activityMatchWithBinding.rvMatchItems.itemAnimator = DefaultItemAnimator()
            activityMatchWithBinding.rvMatchItems.layoutManager = layoutManager
            activityMatchWithBinding.rvMatchItems.adapter = matchWithAdapter
        } else {
            activityMatchWithBinding.cvNoResult.visibility = View.VISIBLE
            activityMatchWithBinding.rvMatchItems.visibility = View.GONE
        }
    }

    fun updateList() {
        matchWithProducts?.forEach {
            if (selectedLookToBuy?.id == it?.id)
                it?.is_added_to_cart = true
        }
        matchWithAdapter?.notifyDataSetChanged()

        val cartCount = sharedpreferenceHandler.getData(SharedpreferenceHandler.CART_COUNT, 0)
        sharedpreferenceHandler.saveData(SharedpreferenceHandler.CART_COUNT, cartCount + 1)
        setBagCount()
    }


    ///loading dialog
    private fun showProgress() {

        if (!this@MatchWithActivity.isFinishing) {
            if (loading == null) {
                loading = Dialog(this@MatchWithActivity, R.style.TranslucentDialog)
                loading?.setContentView(R.layout.custom_loading_view)
                loading?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                loading?.setCanceledOnTouchOutside(false)
                loading?.show()
            } else {
                if (!!loading?.isShowing!!) {
                    loading?.show()
                }
            }
        }
    }

    private fun dismissProgress() {
        if (loading != null && loading?.isShowing == true)
            loading?.dismiss()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun setBagCount() {
        val cartCount = sharedpreferenceHandler.getData(SharedpreferenceHandler.CART_COUNT, 0)
        if (cartCount > 0) {
            activityMatchWithBinding.toolbar1.txtCartCount.text = cartCount.toString()
            activityMatchWithBinding.toolbar1.txtCartCount.visibility = View.VISIBLE
        } else {
            activityMatchWithBinding.toolbar1.txtCartCount.visibility = View.INVISIBLE
        }
    }

}