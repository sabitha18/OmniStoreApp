package com.armada.storeapp.ui.home.riva.riva_look_book.omni_item_scan

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.ViewAnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.armada.storeapp.BarcodeSearchQuery
import com.armada.storeapp.R
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.response.ScannedItemDetailsResponse
import com.armada.storeapp.data.onError
import com.armada.storeapp.data.onLoading
import com.armada.storeapp.data.onSuccess
import com.armada.storeapp.databinding.ActivityBarcodeScannerBinding
import com.armada.storeapp.databinding.ActivityOmniItemScanBinding
import com.armada.storeapp.databinding.FragmentItemScanBinding
import com.armada.storeapp.ui.base.BaseActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.bag.BagViewModel
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.AddToCartBottomSheetDialog
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.ProductDetailsActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.search.SearchViewModel
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import com.armada.storeapp.ui.utils.Utils
import com.google.zxing.Result
import dagger.hilt.android.AndroidEntryPoint
import me.dm7.barcodescanner.zxing.ZXingScannerView
import kotlin.collections.ArrayList
import kotlin.random.Random

@AndroidEntryPoint
class OmniItemScannerActivity : BaseActivity(), ZXingScannerView.ResultHandler {

    private var omniScannedItem: ScannedItemDetailsResponse? = null
    lateinit var activityOmniItemScanBinding: ActivityOmniItemScanBinding
    lateinit var omniItemScanViewModel: OmniItemScanViewModel
    lateinit var searchViewModel: SearchViewModel
    private var mScannerView: ZXingScannerView? = null
    private var mToolbar: Toolbar? = null
    private var strBarCode = ""
    private var loading: Dialog? = null
    private var isCamera = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityOmniItemScanBinding = ActivityOmniItemScanBinding.inflate(layoutInflater)
        setContentView(activityOmniItemScanBinding.root)
        omniItemScanViewModel =
            ViewModelProvider(this).get(OmniItemScanViewModel::class.java)

        init()
        setOnClickListener()
    }

    private fun init() {

        mScannerView = ZXingScannerView(this@OmniItemScannerActivity)
        activityOmniItemScanBinding?.contentFrame?.addView(mScannerView!!)
        mScannerView!!.setBorderColor(
            ContextCompat.getColor(
                this@OmniItemScannerActivity,
                R.color.white
            )
        )

    }


    private fun setOnClickListener() {

        activityOmniItemScanBinding?.imgFlash.setOnClickListener {
            if (this.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {

                try {
                    mScannerView!!.flash = !mScannerView!!.flash
                    if (mScannerView!!.flash) {
                        activityOmniItemScanBinding?.imgFlash.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@OmniItemScannerActivity,
                                R.drawable.ic_flash_on
                            )
                        )
                    } else {
                        activityOmniItemScanBinding?.imgFlash.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@OmniItemScannerActivity,
                                R.drawable.ic_flash_off
                            )
                        )
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        activityOmniItemScanBinding.imageView8.setOnClickListener {
            this.finish()
        }
    }

    ///
    private fun playSoundOnCapture() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.beep)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener { mediaPlayer.release() }
    }

    fun omniScanItem(skuCode: String) {
        val sharedpreferenceHandler = SharedpreferenceHandler(this)
        val priceListId = sharedpreferenceHandler.getData(SharedpreferenceHandler.PRICE_LIST_ID, 0)
        val storeId = sharedpreferenceHandler.getData(SharedpreferenceHandler.STORE_ID, 0)
        omniItemScanViewModel.omniScanItem(
            skuCode,
            storeId!!.toString(),
            priceListId!!.toString()
        )
        omniItemScanViewModel.responseScanItemOmni.observe(this) {
            when (it) {
                is Resource.Success -> {
                    dismissProgress()
                    if (it.data?.statusCode == 1) {
                        omniScannedItem = it.data
                        addOmniProduct()
//                        addProductToDb()
                    } else {
                        Utils.showSnackbar(
                            activityOmniItemScanBinding.root,
                            it.data?.displayMessage!!
                        )
                    }


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

    private fun addOmniProduct() {
        try {
            omniScannedItem?.skuMasterTypesList?.get(0)?.quantity = 1
            omniScannedItem?.skuMasterTypesList?.get(0)?.fromRiva=false
            omniScannedItem?.skuMasterTypesList?.get(0)?.productId=
               ""
            omniItemScanViewModel.addOmniProductToPrefs(
                omniScannedItem?.skuMasterTypesList?.get(0)!!,
                this
            )
            finishActivity()
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    fun finishActivity() {
        val intent = Intent()
        setResult(Activity.RESULT_OK, intent)
        finish()
        overridePendingTransition(R.anim.nothing, R.anim.bottom_down)
    }

    private fun addProductToDb() {
        try {
            omniScannedItem?.skuMasterTypesList?.get(0)?.quantity = 1
            omniScannedItem?.skuMasterTypesList?.get(0)?.id = (0..100).shuffled().last()
            omniItemScanViewModel.addOmniProduct(omniScannedItem?.skuMasterTypesList?.get(0)!!)
            finishActivity()
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }


    ///loading dialog
    private fun showProgress() {

        if (!this@OmniItemScannerActivity.isFinishing) {
            if (loading == null) {
                loading = Dialog(this@OmniItemScannerActivity, R.style.TranslucentDialog)
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

    public override fun onResume() {
        super.onResume()
        mScannerView!!.setResultHandler(this)
        mScannerView!!.startCamera()
    }

    public override fun onPause() {
        super.onPause()
        mScannerView!!.stopCamera()
    }


    ///
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                this.finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun handleResult(rawResult: Result) {
        strBarCode = rawResult.text
        if (isCamera) {
            playSoundOnCapture()
            omniScanItem(strBarCode)
        }
    }
}
