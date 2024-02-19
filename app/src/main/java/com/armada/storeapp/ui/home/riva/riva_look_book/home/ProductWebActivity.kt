package com.armada.riva.HOME


import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.Toolbar
import com.armada.storeapp.data.model.response.CollectionListItemModel
import com.armada.storeapp.databinding.ActivityProductWebBinding
import com.armada.storeapp.ui.base.BaseActivity

class ProductWebActivity : BaseActivity() {

    private var mToolbar: Toolbar? = null
    private var strContent: String? = null
    internal lateinit var head: String
    private lateinit var htmlData: String
    private var imgHeight: Int = 0
    private var model: CollectionListItemModel? = null
    private var binding: ActivityProductWebBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductWebBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        init()
        initToolbar()
//        setOnClickListener()
    }

    private fun initToolbar() {
//        mToolbar = toolbar_actionbar as Toolbar?
//        setSupportActionBar(mToolbar)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)
//
//        val upArrow: Drawable? = if (AppController.instance.isLangArebic) {
//            ContextCompat.getDrawable(this@ProductWebActivity, R.drawable.ic_arrow_right)
//        } else {
//            ContextCompat.getDrawable(this@ProductWebActivity, R.drawable.ic_arrow_left)
//        }
//
//        upArrow?.setColorFilter(
//            resources.getColor(R.color.primary_text_color),
//            PorterDuff.Mode.SRC_ATOP
//        )
//        upArrow?.setVisible(true, true)
//        supportActionBar!!.setHomeAsUpIndicator(upArrow)
//        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
//
//        txtHead.text = model!!.title
    }

    private fun init() {

//        mCompositeDisposable = CompositeDisposable()
//        appController = applicationContext as AppController?
//
//        cd = ConnectionDetector(this)
//
//        model = intent.getSerializableExtra("model") as CollectionListItemModel
//        imgHeight = intent.getIntExtra("banner_height", 0)
//
//        if (cd!!.isConnectingToInternet) getCMS()
//
//        txtHead.typeface = appController!!.boldFont
//        btnShop.typeface = AppController.instance.boldFont

    }


    ///
//    private fun setOnClickListener() {
//        btnShop.setOnClickListener() {
//
//            if (model!!.type.equals("C", false)) {
//                val intent = Intent(this@ProductWebActivity, ProductListGraphqlActivity::class.java)
//                intent.putExtra("banner_height", imgHeight)
//                intent.putExtra("model", model)
//                startActivity(intent)
//
//            } else if (model!!.type.equals("P", false)) {
//
//                val intent = Intent(this@ProductWebActivity, ProductDetailActivity::class.java)
//                intent.putExtra("id", model!!.type_id)
//                intent.putExtra("name", model!!.title)
//                intent.putExtra("image", model!!.image)
//                startActivity(intent)
//            }
//        }
//    }

    ///
//    private fun getCMS() {
//
//        loadingView = LoadingView.Builder(this@ProductWebActivity, false)
//            .setProgressColorResource(R.color.black)
//            .setBackgroundColorRes(R.color.bg_color)
//            .setProgressStyle(LoadingView.ProgressStyle.CYCLIC)
//            .setCustomMargins(0, 0, 0, 0)
//            .attachTo(this@ProductWebActivity)
//        loadingView!!.show()
//
//        val requestInterface = RequestInterface.create()
//
//        var strUrl: String? = ""
//        strUrl =
//            globalClass!!.SECONDARY_DOMAIN + "cms?page=" + model!!.term_cms_id + "&lang=" + AppController.instance.lang + Global.MAGENTO_TOKEN_HEADER;
//
//        mCompositeDisposable?.add(
//            requestInterface.getAboutUs(Global.MAGENTO_TOKEN_HEADER, strUrl)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe({ result ->
//                    //println("CMS response   " + Gson().toJson(result.data))
//                    if (result != null) {
//                        if (result.active == true) {
//
//                            strContent = result.content
//                            head =
//                                "<head><style>@font-face {font-family: 'Cairo-Regular';src: url('file:///android_asset/Cairo-Regular.ttf');}body {font-family: 'Cairo-Regular';text-align:right;color:#141414;}a{color:#141414}</style></head>"
//                            htmlData =
//                                "<html>$head<body style=\"font-family: Cairo-Regular\">$strContent</body></html>"
//
//
//                            val arrList = ArrayList<String>()
//                            arrList.add(htmlData)
//                            val adapter = WebAdapter(arrList, this@ProductWebActivity)
//                            val layoutManager = androidx.recyclerview.widget.GridLayoutManager(
//                                this@ProductWebActivity,
//                                1
//                            )
//
//                            rcyWeb.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
//                            rcyWeb.layoutManager = layoutManager
//                            rcyWeb.adapter = adapter
//
//                            scrollNested.visibility = View.VISIBLE
//                            loadingView!!.hide()
//
//                        } else {
//                            relNoResult.visibility = View.VISIBLE
//                        }
//                    } else {
//                        relNoResult.visibility = View.VISIBLE
//                    }
//
//                    loadingView!!.hide()
//                }, { error ->
//                    //println("Error in loading cms :   " + error.localizedMessage)
//                    loadingView!!.hide()
//                    relNoResult.visibility = View.VISIBLE
//                })
//        )
//
//        if (model!!.type.equals("H", true)) {
//            btnShop.visibility = View.GONE
//        } else {
//            btnShop.visibility = View.VISIBLE
//        }
//    }


//    class WebAdapter(val stringList: ArrayList<String>, private val context: Context?) :
//        androidx.recyclerview.widget.RecyclerView.Adapter<WebAdapter.MyViewHolder>() {
//        private var loading: Dialog? = null
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
//            val v = LayoutInflater.from(parent.context)
//                .inflate(R.layout.item_product_web, parent, false)
//            return MyViewHolder(v)
//        }
//
//        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//
//            val bm = stringList[position]
//            holder.itemView.webProduct.loadDataWithBaseURL(
//                "http://nada",
//                "<style>img{display: inline;height: auto;max-width: 100%;}</style>$bm",
//                "text/html",
//                "utf-8",
//                ""
//            )
//
//            holder.itemView.webProduct.webViewClient = object : WebViewClient() {
//                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
//                    showProgress()
//                    view.loadUrl(url)
//                    return true
//                }
//
//                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
//                    super.onPageStarted(view, url, favicon)
//                    //showProgress()
//                }
//
//                override fun onPageFinished(view: WebView, url: String) {
//                    super.onPageFinished(view, url)
//                    dismissProgress()
//                }
//
//                override fun onPageCommitVisible(view: WebView?, url: String?) {
//                    super.onPageCommitVisible(view, url)
//                    dismissProgress()
//                }
//            }
//
//        }
//
//        override fun getItemCount(): Int {
//            return 1
//        }
//
//        class MyViewHolder(itemView: View) :
//            androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {}
//
//        //////Show PROGRESS DIALOG
//        private fun showProgress() {
//            if (!(context as Activity).isFinishing) {
//                loading = Dialog(context, R.style.TranslucentDialog)
//                //loading!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
//                loading!!.setContentView(R.layout.custom_loading_view)
//                loading!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//                loading!!.show()
//
//                loading!!.setCanceledOnTouchOutside(false)
//            }
//        }
//
//        ////HIDE DIALOG
//        private fun dismissProgress() {
//            if (!(context as Activity).isFinishing) {
//                if (loading != null && loading!!.isShowing) {
//                    loading!!.dismiss()
//                }
//            }
//        }
//
//    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            val view = this.currentFocus
            if (view != null) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
            this.finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

}