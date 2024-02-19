package com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import com.armada.storeapp.R
import com.armada.storeapp.databinding.ActivityWebviewBinding
import com.armada.storeapp.ui.base.BaseActivity
import com.armada.storeapp.ui.utils.LoadingView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.toolbar_default.*
import java.util.*

@AndroidEntryPoint
class SizeGuideActivity : BaseActivity() {
    private var loadingView: LoadingView?=null
    lateinit var binding: ActivityWebviewBinding
    private var link = ""
    private var first_load = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {

        link = intent.getStringExtra("url").toString()
        txtHead.text = resources.getString(R.string.size_guide)

        binding.webView.settings.builtInZoomControls = false
        binding.webView.settings.useWideViewPort = true
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.setSupportMultipleWindows(true)
        binding.webView.settings.javaScriptCanOpenWindowsAutomatically = true
        binding.webView.settings.loadsImagesAutomatically = true
        binding.webView.settings.domStorageEnabled = true
        binding.webView.settings.loadWithOverviewMode = true
        binding.webView.loadUrl(link)

        handleWebView()
    }


    private fun handleWebView() {
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                if (first_load) {
                    first_load = false
                    showProgress()
                }
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                if (!first_load)
                    dismissProgress()

            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

    }

    private fun showProgress() {
        loadingView = LoadingView.Builder(this@SizeGuideActivity, false)
            .setProgressColorResource(R.color.black)
            .setBackgroundColorRes(R.color.bg_color)
            .setProgressStyle(LoadingView.ProgressStyle.CYCLIC)
            .setCustomMargins(0, 0, 0, 0)
            .attachTo(this@SizeGuideActivity);
        loadingView!!.show();
    }

    private fun dismissProgress() {
        loadingView!!.hide();
    }

}