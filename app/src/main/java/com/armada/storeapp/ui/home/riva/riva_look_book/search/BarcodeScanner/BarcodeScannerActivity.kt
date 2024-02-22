package com.armada.storeapp.ui.home.riva.riva_look_book.search.BarcodeScanner

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
import com.armada.storeapp.data.onError
import com.armada.storeapp.data.onLoading
import com.armada.storeapp.data.onSuccess
import com.armada.storeapp.databinding.ActivityBarcodeScannerBinding
import com.armada.storeapp.ui.base.BaseActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.OmniProductDetailsActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.ProductDetailsActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.search.SearchViewModel
import com.armada.storeapp.ui.utils.Utils
import com.google.zxing.Result
import dagger.hilt.android.AndroidEntryPoint
import me.dm7.barcodescanner.zxing.ZXingScannerView
import kotlin.collections.ArrayList

@AndroidEntryPoint
class BarcodeScannerActivity : BaseActivity(), ZXingScannerView.ResultHandler {

    lateinit var activityBarcodeScannerBinding: ActivityBarcodeScannerBinding
    lateinit var searchViewModel: SearchViewModel
    private var mScannerView: ZXingScannerView? = null
    private var mToolbar: Toolbar? = null
    private var strBarCode = ""
    private var loading: Dialog? = null
    private var isCamera = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBarcodeScannerBinding = ActivityBarcodeScannerBinding.inflate(layoutInflater)
        setContentView(activityBarcodeScannerBinding.root)
        searchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        //println("Here i am barcode scanner")
        initToolbar()
        init()
        setOnClickListener()
    }

    ///
    private fun initToolbar() {
        mToolbar = activityBarcodeScannerBinding.toolbarActionbar.root
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        val upArrow: Drawable? =
            ContextCompat.getDrawable(
                this@BarcodeScannerActivity,
                R.drawable.ic_baseline_arrow_back_24
            )

        upArrow?.setColorFilter(
            ContextCompat.getColor(this@BarcodeScannerActivity, R.color.black),
            PorterDuff.Mode.SRC_ATOP
        )
        upArrow?.setVisible(true, true)
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        activityBarcodeScannerBinding.toolbarActionbar.txtHead.text =
            resources.getString(R.string.scan_barcode)
    }

    ///
    private fun init() {

        mScannerView = ZXingScannerView(this@BarcodeScannerActivity)
        activityBarcodeScannerBinding?.contentFrame?.addView(mScannerView!!)
        mScannerView!!.setBorderColor(
            ContextCompat.getColor(
                this@BarcodeScannerActivity,
                R.color.white
            )
        )
        activityBarcodeScannerBinding?.imgKeyboard.setColorFilter(
            ContextCompat.getColor(this@BarcodeScannerActivity, R.color.brand_border_color),
            PorterDuff.Mode.SRC_ATOP
        )


/*For Action search*/
//        activityBarcodeScannerBinding?.edtBardcode.setOnEditorActionListener { v, actionId, event ->
//            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//
//                if (!activityBarcodeScannerBinding?.edtBardcode.text.isNullOrEmpty() && !activityBarcodeScannerBinding?.edtBardcode.text.equals(
//                        ""
//                    )
//                ) {
//                    setResultData(activityBarcodeScannerBinding?.edtBardcode.text.toString())
////                    getScannedProduct(activityBarcodeScannerBinding?.edtBardcode.text.toString())
//                } else {
//                    Utils.showSnackbarWithAction(
//                        activityBarcodeScannerBinding.relContainer,
//                        resources.getString(R.string.all_field_required),
//                        "Action"
//                    )
//                }
//                true
//            } else {
//                false
//            }
//        }

        activityBarcodeScannerBinding?.edtBardcode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s!!.length == 13 || s!!.length==6) {
                    setResultData(activityBarcodeScannerBinding?.edtBardcode.text.toString())
//                    getScannedProduct(activityBarcodeScannerBinding?.edtBardcode.text.toString())
                }
            }
        })

        activityBarcodeScannerBinding?.edtArticleNoPart1.addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s!!.length == 13 || s!!.length==6) {
                    activityBarcodeScannerBinding.edtArticleNoPart2.requestFocus()
                }
            }
        })

        activityBarcodeScannerBinding?.edtArticleNoPart2.addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s!!.toString().length == 5 && activityBarcodeScannerBinding.edtArticleNoPart1.text.toString().length == 6) {
                    val code = activityBarcodeScannerBinding.edtArticleNoPart1.text.toString() +
                            "-" + s.toString()
                    setResultData(code)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


            }
        })
    }

    ///
    private fun setOnClickListener() {

        activityBarcodeScannerBinding.radionBtnBarcode.setOnCheckedChangeListener { button, isChecked ->
            if (isChecked) {
                activityBarcodeScannerBinding.edtBardcode.visibility = View.VISIBLE
                activityBarcodeScannerBinding.lvArticle.visibility = View.GONE
            } else {
                activityBarcodeScannerBinding.edtBardcode.visibility = View.GONE
                activityBarcodeScannerBinding.lvArticle.visibility = View.VISIBLE
            }
        }

        activityBarcodeScannerBinding.radioBtnArticleCode.setOnCheckedChangeListener { button, isChecked ->
            if (isChecked) {
                activityBarcodeScannerBinding.edtBardcode.visibility = View.GONE
                activityBarcodeScannerBinding.lvArticle.visibility = View.VISIBLE
            } else {
                activityBarcodeScannerBinding.edtBardcode.visibility = View.VISIBLE
                activityBarcodeScannerBinding.lvArticle.visibility = View.GONE
            }
        }


        activityBarcodeScannerBinding?.imgKeyboard.setOnClickListener {
            if (activityBarcodeScannerBinding?.contentFrame.visibility != View.GONE) {
                activityBarcodeScannerBinding?.imgCamera.setColorFilter(
                    ContextCompat.getColor(this@BarcodeScannerActivity, R.color.brand_border_color),
                    PorterDuff.Mode.SRC_ATOP
                )
                activityBarcodeScannerBinding?.imgKeyboard.setColorFilter(
                    ContextCompat.getColor(this@BarcodeScannerActivity, R.color.black),
                    PorterDuff.Mode.SRC_ATOP
                )

//                activityBarcodeScannerBinding?.imagBardcode.visibility = View.VISIBLE
//                activityBarcodeScannerBinding?.edtBardcode.visibility = View.VISIBLE
                activityBarcodeScannerBinding.cvKeyboard.visibility = View.VISIBLE

                isCamera = false
                activityBarcodeScannerBinding?.contentFrame.visibility = View.GONE
                activityBarcodeScannerBinding?.dummyFrame.visibility = View.GONE
                activityBarcodeScannerBinding?.imgFlash.visibility = View.GONE
                activityBarcodeScannerBinding?.relBottom.visibility = View.GONE

                val w = activityBarcodeScannerBinding?.cvKeyboard.width
                val h = activityBarcodeScannerBinding?.cvKeyboard.height

                val endRadius = Math.hypot(w.toDouble(), h.toDouble()).toFloat()

                val cx =
                    (activityBarcodeScannerBinding?.cvKeyboard.left + activityBarcodeScannerBinding?.cvKeyboard.right)
                val cy = 0

                val revealAnimator =
                    ViewAnimationUtils.createCircularReveal(
                        activityBarcodeScannerBinding?.cvKeyboard,
                        cx,
                        cy,
                        0f,
                        endRadius
                    )

                revealAnimator.duration = 500
                revealAnimator.start()
            }
        }

        activityBarcodeScannerBinding?.relScanHistory.setOnClickListener {
            val i = Intent(this@BarcodeScannerActivity, ScanHistoryActivity::class.java)
            startActivity(i)
        }

        activityBarcodeScannerBinding?.imgCamera.setOnClickListener {
            hideSoftKeyboard()
            //if barcode image is visible it means its keyboard selected
            if (activityBarcodeScannerBinding?.imagBardcode.visibility != View.GONE) {
                activityBarcodeScannerBinding?.imgCamera.setColorFilter(
                    ContextCompat.getColor(this@BarcodeScannerActivity, R.color.black),
                    PorterDuff.Mode.SRC_ATOP
                )
                activityBarcodeScannerBinding?.imgKeyboard.setColorFilter(
                    ContextCompat.getColor(this@BarcodeScannerActivity, R.color.brand_border_color),
                    PorterDuff.Mode.SRC_ATOP
                )
//                activityBarcodeScannerBinding?.imagBardcode.visibility = View.GONE
//                activityBarcodeScannerBinding?.edtBardcode.visibility = View.GONE
                activityBarcodeScannerBinding.cvKeyboard.visibility = View.GONE

                isCamera = true
                activityBarcodeScannerBinding?.contentFrame.visibility = View.VISIBLE
                activityBarcodeScannerBinding?.imgFlash.visibility = View.VISIBLE
                activityBarcodeScannerBinding?.relBottom.visibility = View.VISIBLE
                activityBarcodeScannerBinding?.dummyFrame.visibility = View.VISIBLE

                val w = activityBarcodeScannerBinding?.contentFrame.width
                val h = activityBarcodeScannerBinding?.contentFrame.height

                val endRadius = Math.hypot(w.toDouble(), h.toDouble()).toFloat()

                val cx = 0
                val cy = 0

                val revealAnimator =
                    ViewAnimationUtils.createCircularReveal(
                        activityBarcodeScannerBinding?.dummyFrame,
                        cx,
                        cy,
                        endRadius,
                        0f
                    )

                revealAnimator.duration = 500
                revealAnimator.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation!!)
                        activityBarcodeScannerBinding?.dummyFrame.visibility = View.GONE
                    }

                    override fun onAnimationStart(animation: Animator) {
                        super.onAnimationStart(animation!!)
                        activityBarcodeScannerBinding?.dummyFrame.visibility = View.VISIBLE
                    }
                })
                revealAnimator.start()
            }
        }

        activityBarcodeScannerBinding?.imgFlash.setOnClickListener {
            if (this.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {

                try {
                    mScannerView!!.flash = !mScannerView!!.flash
                    if (mScannerView!!.flash) {
                        activityBarcodeScannerBinding?.imgFlash.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@BarcodeScannerActivity,
                                R.drawable.ic_flash_on
                            )
                        )
                    } else {
                        activityBarcodeScannerBinding?.imgFlash.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@BarcodeScannerActivity,
                                R.drawable.ic_flash_off
                            )
                        )
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    ///
    private fun playSoundOnCapture() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.beep)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener { mediaPlayer.release() }
    }

//    fun searchQuery(
//        searchQuery: String
//    ) {
//        searchViewModel.searchProducts(this,searchQuery)
//        searchViewModel.responseSearchProduct.observe(this) {
//            it.onSuccess { data ->
//                dismissProgress()
//                handleSearchResponse(data)
//            }.onError { error ->
//                dismissProgress()
//                when (error.messageResource) {
//                    is Int -> Toast.makeText(
//                       this,
//                        getString(error.messageResource),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    is Error? -> {
//                        error.messageResource?.let { errorMessage ->
//                            Toast.makeText(
//                                this,
//                                errorMessage.message,
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    }
//                }
//            }.onLoading {
//                showProgress()
//            }
//        }
//    }

//    fun getScannedProduct(
//        barcode: String
//    ) {
//        searchViewModel.getScannedProduct(this,barcode)
//        searchViewModel.responseScannedProduct.observe(this) {
//            it.onSuccess { response ->
//                handleBarcodeScanResponse(response)
//                dismissProgress()
//            }.onError { error ->
//                dismissProgress()
//                when (error.messageResource) {
//                    is Int -> Toast.makeText(
//                        this,
//                        getString(error.messageResource),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    is Error? -> {
//                        error.messageResource?.let { errorMessage ->
//                            Toast.makeText(
//                                this,
//                                errorMessage.message,
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    }
//                }
//            }.onLoading {
//                showProgress()
//            }
//        }
//    }


    private fun handleBarcodeScanResponse(result: BarcodeSearchQuery.Data) {
        dismissProgress()
        if (!result?.products?.items.isNullOrEmpty()) {
            val product = result?.products?.items?.get(0)

            if (isCamera) {
                val handler = Handler()
                handler.postDelayed(
                    { mScannerView!!.resumeCameraPreview(this@BarcodeScannerActivity) },
                    2000
                )
            }


            var arrListScan = Utils.getArrayList(this, "scan_history")
            if (arrListScan == null) {
                arrListScan = ArrayList()
            }

            arrListScan.add(strBarCode + "$" + (product?.id ?: 0))
            //  Log.i("BarcodeScannerActvity","Arrau list: "+arrListScan.toString())
            Utils.saveArrayList(this, arrListScan, "scan_history")
            activityBarcodeScannerBinding?.edtBardcode.text.clear()
            val intent = Intent(this, OmniProductDetailsActivity::class.java)
            intent.putExtra("id", product?.id.toString())
            intent.putExtra("cat_id", "-1")
            intent.putExtra("name", product?.name)
            startActivity(intent)

        } else {
            Utils.showSnackbarWithAction(
                activityBarcodeScannerBinding?.relContainer,
                resources.getString(R.string.no_result_found),
                "Action"
            )
        }
    }

    fun handleError(error: Throwable) {
        if (isCamera) {
            val handler = Handler()
            handler.postDelayed(
                { mScannerView!!.resumeCameraPreview(this@BarcodeScannerActivity) },
                2000
            )
        }
        dismissProgress()
        // Log.d(ContentValues.TAG, error.localizedMessage)
        Utils.showErrorSnackbar(activityBarcodeScannerBinding.relContainer)
        // Toast.makeText(this, "Error ${error.localizedMessage}", Toast.LENGTH_SHORT).show()
    }

    ///loading dialog
    private fun showProgress() {

        if (!this@BarcodeScannerActivity.isFinishing) {
            if (loading == null) {
                loading = Dialog(this@BarcodeScannerActivity, R.style.TranslucentDialog)
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
//            getScannedProduct(strBarCode)
            setResultData(strBarCode)
        }
    }

    fun setResultData(barcode: String) {
        println(" barcode -----   "+barcode)
        val returnIntent = Intent()
        returnIntent.putExtra("barcode", barcode)
        returnIntent.putExtra("article", barcode)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }
}
