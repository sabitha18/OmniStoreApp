package com.armada.storeapp.ui.home.riva.riva_look_book.search

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.armada.riva.HOME.Adapter.SearchAdapter
import com.armada.storeapp.R
import com.armada.storeapp.SearchQuery
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.local.model.RecentSearch
import com.armada.storeapp.data.model.response.SearchBannerModel
import com.armada.storeapp.data.model.response.SearchList
import com.armada.storeapp.data.model.response.Stock
import com.armada.storeapp.data.onError
import com.armada.storeapp.data.onLoading
import com.armada.storeapp.data.onSuccess
import com.armada.storeapp.databinding.FragmentSearchBinding
import com.armada.storeapp.ui.home.riva.riva_look_book.RivaLookBookActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.search.BarcodeScanner.BarcodeScannerActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.search.Stores.StoreListingActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.search.adapter.*
import com.armada.storeapp.ui.utils.GPSTracker
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import com.armada.storeapp.ui.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject


@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var selectedLanguage = "en"
    private var selectedCurrency = "USD"
    lateinit var fragmentSearchBinding: FragmentSearchBinding
    private var rivaLookBookActivity: RivaLookBookActivity? = null
    private var searchLength = 0
    private var strQuery = ""
    private lateinit var listener: OnItemClickListener
    private var model: SearchBannerModel? = null

    val MY_PERMISSIONS_REQUEST_CAMERA = 0x2
    val MY_PERMISSIONS_REQUEST_LOCATION = 99
    var gps: GPSTracker? = null
    private var screenWidth: Int = 0
    private var viewPgHeight: Double = 0.0
    private var viewPgWidth: Double = 0.0
    private var arrListRecentSearch = ArrayList<RecentSearch>()
    private var margin5: Int = 0
    private var margin7: Int = 0
    private var margin10: Int = 0
    private var isApiCallingAutomatically = false

    private var arrProductSearchList: ArrayList<SearchQuery.Item1?>? = ArrayList(0)
    private var arrCategorySearchList: ArrayList<SearchQuery.Item?>? = ArrayList(0)
    private var arrBrandsSearchList: ArrayList<SearchList> = ArrayList(0)
    lateinit var searchViewModel: SearchViewModel

    private var productWidth = (screenWidth / 2).toInt()
    private var productheight = (productWidth / 0.687).toInt()
    lateinit var sharedpreferenceHandler: SharedpreferenceHandler
    var startForResult: ActivityResultLauncher<Intent>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    val barcode = intent?.getStringExtra("barcode")
                    if (intent?.hasExtra("article") == true) {
                        val article = intent.getStringExtra("article")
                        getArticleProducts(article!!)
                    }
                    searchQuery(barcode!!)
                }
            }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentSearchBinding = FragmentSearchBinding.inflate(layoutInflater)
        searchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        sharedpreferenceHandler = SharedpreferenceHandler(requireContext())
        selectedLanguage =
            sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_SELECTED_COUNTRY, "en")!!
        selectedCurrency =
            sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_SELECTED_CURRENCY, "USD")!!
        rivaLookBookActivity = activity as RivaLookBookActivity
        initializeFields()
        onClickListerners()
        fragmentSearchBinding.edtSearch.setIconifiedByDefault(false)
        return fragmentSearchBinding.root
    }

    @SuppressLint("MissingPermission")
    private fun onClickListerners() {
        fragmentSearchBinding.lvScan.setOnClickListener {
            rivaLookBookActivity?.hideSoftKeyboard()
            checkCameraPermission()
        }

        fragmentSearchBinding.txtClearRecent.setOnClickListener {
            sharedpreferenceHandler.saveData(SharedpreferenceHandler.RECENT_SEARCH, "")
            fragmentSearchBinding.lnrRecent.visibility = View.GONE
        }


        fragmentSearchBinding.edtSearch.setOnCloseListener {
            rivaLookBookActivity?.hideSoftKeyboard()
            fragmentSearchBinding.rcySearch.visibility = View.GONE
            fragmentSearchBinding.lnrSearch.visibility = View.GONE
            fragmentSearchBinding.lnrCategory.visibility = View.GONE
            return@setOnCloseListener false
        }

//        fragmentSearchBinding.tvClear.setOnClickListener {
//            rivaLookBookActivity?.hideSoftKeyboard()
////            fragmentSearchBinding.edtSearch.setText("") // todo change
//            fragmentSearchBinding.rcySearch.visibility = View.GONE
//            fragmentSearchBinding.lnrSearch.visibility = View.GONE
//            fragmentSearchBinding.lnrCategory.visibility = View.GONE
//
//            hideKeyboard()
//        }

        fragmentSearchBinding.lvStore.setOnClickListener {
            hideKeyboard()
            checkLocationPermission()
        }

        fragmentSearchBinding.txtSeeAll.setOnClickListener {
            hideKeyboard()
            val bundle = Bundle()
            bundle.putString("from_search", "1")
            bundle.putString("searchKeyword", strQuery)
//            bundle.putString(Constants.SELECTED_CATEGORY_ID,rivaLookBookActivity?.getParentCategory()?.toString())
            rivaLookBookActivity?.navController?.navigate(R.id.navigation_product_listing, bundle)

        }


        fragmentSearchBinding.edtSearch.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isEmpty())
                    hideKeyboard()
                searchLength = newText.length
                strQuery = newText.trim()
                if (strQuery.isDigitsOnly()) {
                    if (Utils.hasInternetConnection(requireContext())) {
                        isApiCallingAutomatically = true
                        searchQuery(strQuery)
//                        fragmentSearchBinding.tvClear.visibility = View.VISIBLE
                    }
                }

                    //below code is commented because hakkeem asked to search only values is 6 or 13 only now 3 or 4 something like that.

//                else if (strQuery.length > 2) {
//                    strQuery = newText
//                    if (Utils.hasInternetConnection(requireContext())) {
////                        progressBar.visibility = View.VISIBLE
//                        // Send a first item to do the initial load else the list will stay empty forever
//                        searchQuery(strQuery)
////                        fragmentSearchBinding.tvClear.visibility = View.VISIBLE
//                    }
//                }
                else {
                    fragmentSearchBinding.rcySearch.adapter = null
//                    fragmentSearchBinding.rcyTop.visibility = View.VISIBLE
//                    fragmentSearchBinding.tvClear.visibility = View.GONE
                    fragmentSearchBinding.rcySearch.visibility = View.GONE

                    fragmentSearchBinding.lnrSearch.visibility = View.GONE
                    fragmentSearchBinding.lnrCategory.visibility = View.GONE

                    if (fragmentSearchBinding.rcyPopularSearch.visibility == View.GONE) {
                        if (model != null && model!!.popular_banners != null && model!!.popular_banners?.size ?: 0 == 0)
                            fragmentSearchBinding.cvNoResult.visibility = View.VISIBLE
                        else fragmentSearchBinding.cvNoResult.visibility = View.GONE
                    }
                }
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                hideKeyboard()
                strQuery = query.trim()

                if (strQuery.length > 2) {
                    fragmentSearchBinding.cvNoResult.visibility = View.GONE

                        if (Utils.hasInternetConnection(requireContext())) {
                            isApiCallingAutomatically = true
                            searchQuery(strQuery)
//                        fragmentSearchBinding.tvClear.visibility = View.VISIBLE
                        }



                } else {
//                    fragmentSearchBinding.tvClear.visibility = View.GONE
                    fragmentSearchBinding.rcySearch.visibility = View.GONE

                    fragmentSearchBinding.lnrSearch.visibility = View.GONE
                    fragmentSearchBinding.lnrCategory.visibility = View.GONE

                    if (fragmentSearchBinding.rcyPopularSearch.visibility == View.GONE) {
                        if (model != null && model!!.popular_banners != null && model!!.popular_banners?.size ?: 0 == 0)
                            fragmentSearchBinding.cvNoResult.visibility = View.VISIBLE
                        else fragmentSearchBinding.cvNoResult.visibility = View.GONE
                    }

//                    fragmentSearchBinding.rcyTop.visibility = View.VISIBLE
                }

                return false
            }

        })

//        fragmentSearchBinding.edtSearch.addTextChangedListener(object : TextWatcher {
//
//            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//                searchLength = count
//                strQuery = s.toString()
//                if (strQuery.isDigitsOnly() && strQuery.length == 13) {
//                    if (Utils.hasInternetConnection(requireContext())) {
//                        searchQuery(strQuery)
//                        fragmentSearchBinding.tvClear.visibility = View.VISIBLE
//                    }
//                } else if (strQuery.length > 2) {
//                    strQuery = s.toString()
//                    if (Utils.hasInternetConnection(requireContext())) {
////                        progressBar.visibility = View.VISIBLE
//                        // Send a first item to do the initial load else the list will stay empty forever
//                        searchQuery(strQuery)
//                        fragmentSearchBinding.tvClear.visibility = View.VISIBLE
//                    }
//                } else {
//                    fragmentSearchBinding.rcySearch.adapter = null
//                    fragmentSearchBinding.rcyTop.visibility = View.VISIBLE
//                    fragmentSearchBinding.tvClear.visibility = View.GONE
//                    fragmentSearchBinding.rcySearch.visibility = View.GONE
//
//                    fragmentSearchBinding.lnrSearch.visibility = View.GONE
//                    fragmentSearchBinding.lnrCategory.visibility = View.GONE
//
//                    if (fragmentSearchBinding.rcyPopularSearch.visibility == View.GONE) {
//                        if (model != null && model!!.popular_banners != null && model!!.popular_banners?.size ?: 0 == 0)
//                            fragmentSearchBinding.cvNoResult.visibility = View.VISIBLE
//                        else fragmentSearchBinding.cvNoResult.visibility = View.GONE
//                    }
//                }
//            }
//
//            override fun beforeTextChanged(
//                s: CharSequence, start: Int, count: Int,
//                after: Int
//            ) {
//            }
//
//            override fun afterTextChanged(s: Editable) {
//                strQuery = s.toString()
//
//                if (strQuery.length > 2) {
//                    fragmentSearchBinding.tvClear.visibility = View.VISIBLE
//                    fragmentSearchBinding.cvNoResult.visibility = View.GONE
//                    fragmentSearchBinding.rcyTop.visibility = View.GONE
//                } else {
//                    fragmentSearchBinding.tvClear.visibility = View.GONE
//                    fragmentSearchBinding.rcySearch.visibility = View.GONE
//
//                    fragmentSearchBinding.lnrSearch.visibility = View.GONE
//                    fragmentSearchBinding.lnrCategory.visibility = View.GONE
//
//                    if (fragmentSearchBinding.rcyPopularSearch.visibility == View.GONE) {
//                        if (model != null && model!!.popular_banners != null && model!!.popular_banners?.size ?: 0 == 0)
//                            fragmentSearchBinding.cvNoResult.visibility = View.VISIBLE
//                        else fragmentSearchBinding.cvNoResult.visibility = View.GONE
//                    }
//
//                    fragmentSearchBinding.rcyTop.visibility = View.VISIBLE
//                }
//
//            }
//        })
//
//        fragmentSearchBinding.edtSearch.setOnEditorActionListener { v, actionId, event ->
//            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                if (strQuery.length > 2) {
//
//                    val bundle = Bundle()
//                    bundle.putString("from_search", "1")
//                    bundle.putString("searchKeyword", v.text.toString())
//                    rivaLookBookActivity?.navController?.navigate(
//                        R.id.navigation_product_listing,
//                        bundle
//                    )
//                }
//                true
//            } else false
//
//        }

    }

    fun searchQuery(
        searchQuery: String
    ) {
        Log.e("rivasearch", searchQuery)
        searchViewModel.searchProducts(activity!!, searchQuery)
        searchViewModel.responseSearchProduct.observe(requireActivity()) {
            it.onSuccess { data ->
                rivaLookBookActivity?.dismissProgress()
                if (isApiCallingAutomatically)
                    rivaLookBookActivity?.hideSoftKeyboard()
                isApiCallingAutomatically = false
                handleSearchResponse(data)
            }.onError { error ->
                isApiCallingAutomatically = false
                rivaLookBookActivity?.dismissProgress()
                when (error.messageResource) {
                    is Int -> Toast.makeText(
                        requireContext(),
                        getString(error.messageResource),
                        Toast.LENGTH_SHORT
                    ).show()
                    is Error? -> {
                        error.messageResource?.let { errorMessage ->
                            Toast.makeText(
                                requireContext(),
                                errorMessage.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }.onLoading {
                rivaLookBookActivity?.showProgress()
            }
        }
    }


    fun getArticleProducts(searchValue: String) {
        val storeId = sharedpreferenceHandler.getData(SharedpreferenceHandler.STORE_ID, 0)
        searchViewModel.getProductsByArticleNumber(searchValue, storeId!!.toString(), "stock")
        searchViewModel.responseArticle.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    rivaLookBookActivity?.dismissProgress()
                    if (it.statusCode == 1) {

                        setArticleAdapter(it.data?.stockList!!)

                    }

                }
                is Resource.Error -> {

                    rivaLookBookActivity?.dismissProgress()
                    try {
                        it.message?.let { message ->
                            if (message.contains("message")) {
                                val jsonObj = JSONObject(message)
                                rivaLookBookActivity?.showMessage(jsonObj.get("message").toString())
                            } else
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        }
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                }
                is Resource.Loading -> {
                    rivaLookBookActivity?.showProgress()
                }
            }
        }
    }

    private fun setArticleAdapter(stockList: ArrayList<Stock>) {
        val adapter = ArticleProductAdapter(
            rivaLookBookActivity!!,
            selectedCurrency,
           stockList       )
        val layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        fragmentSearchBinding.rcyArticleProducts.visibility=View.VISIBLE
        fragmentSearchBinding.rcyArticleProducts.itemAnimator = DefaultItemAnimator()
        fragmentSearchBinding.rcyArticleProducts.layoutManager = layoutManager
        fragmentSearchBinding.rcyArticleProducts.adapter = adapter
    }

    private fun handleSearchResponse(result: SearchQuery.Data) {
        if (result != null) {
//            fragmentSearchBinding.progressBar.visibility = View.GONE
            fragmentSearchBinding.edtSearch.isFocusableInTouchMode = true
            fragmentSearchBinding.edtSearch.isFocusable = true
            fragmentSearchBinding.edtSearch.requestFocus()
            arrProductSearchList?.clear()
            arrCategorySearchList?.clear()



            try {
                result.products?.items?.let {
                    arrProductSearchList?.addAll(it)
                }
                if (result.products?.items?.size!! > 0)
                    fragmentSearchBinding.edtSearch.setQuery("", false)
            } catch (exception: Exception) {
                exception.printStackTrace()
            }

            try {
                result.categories?.items?.let { arrCategorySearchList?.addAll(it) }
                if (result.categories?.items?.size!! > 0)
                    fragmentSearchBinding.edtSearch.setQuery("", false)
            } catch (exception: Exception) {
                exception.printStackTrace()
            }

            //println("Here i am products :: $result")
            setData()
        } else {
            Utils.showErrorSnackbar(fragmentSearchBinding.cvMain)
        }
    }

    private fun setData() {
//        fragmentSearchBinding.tvClear.visibility = View.VISIBLE
        fragmentSearchBinding.cvNoResult.visibility = View.GONE

        /** set Search result by products*/
        if (arrProductSearchList!!.size > 0) {
            fragmentSearchBinding.rcySearch.visibility = View.VISIBLE
            fragmentSearchBinding.lnrSearch.visibility = View.VISIBLE
            val adapter = SearchAdapter(
                selectedCurrency,
                arrProductSearchList!!,
                rivaLookBookActivity!!,
                strQuery
            )
            adapter.productWidth = this.productWidth
            adapter.productheight = this.productheight
            val layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )

            fragmentSearchBinding.rcySearch.itemAnimator = DefaultItemAnimator()
            fragmentSearchBinding.rcySearch.layoutManager = layoutManager
            fragmentSearchBinding.rcySearch.adapter = adapter
        } else {
            fragmentSearchBinding.rcySearch.visibility = View.GONE
            fragmentSearchBinding.lnrSearch.visibility = View.GONE
        }

        /** set Search result by Categories*/
        if (arrCategorySearchList!!.size > 0) {
            val adapterCategory =
                CategoryAdapter(arrCategorySearchList, rivaLookBookActivity!!, strQuery)
            val layoutManagerCategory = GridLayoutManager(requireContext(), 1)

            fragmentSearchBinding.rcyCategories.itemAnimator = DefaultItemAnimator()
            fragmentSearchBinding.rcyCategories.layoutManager = layoutManagerCategory
            fragmentSearchBinding.rcyCategories.adapter = adapterCategory
            fragmentSearchBinding.lnrCategory.visibility = View.VISIBLE

        } else {
            fragmentSearchBinding.lnrCategory.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        fragmentSearchBinding.edtSearch.clearFocus()
        rivaLookBookActivity?.binding?.imageViewRivaLogo?.visibility = View.VISIBLE
    }

    private fun initializeFields() {
        val metrics = resources.displayMetrics
        screenWidth = (metrics.widthPixels)
        val constant = screenWidth / 320
        viewPgWidth = (constant * 300).toDouble()
        viewPgHeight = (constant * 150).toDouble()
        ///Dimensions
        productWidth = (screenWidth / 2).toInt()
        productheight = (productWidth / 0.687).toInt()
        margin5 = resources.getDimension(R.dimen.five_dp).toInt()
        margin7 = resources.getDimension(R.dimen.seven_dp).toInt()
        margin10 = resources.getDimension(R.dimen.ten_dp).toInt()

        try {
            model = Utils.tempSearchModel
        } catch (e: Exception) {
            e.printStackTrace()
        }

        getRecentSearchList()

        if (model != null && model?.popular_top_scroll != null && model?.popular_top_scroll?.size ?: 0 > 0) {

//            fragmentSearchBinding.rcyTop.visibility = View.VISIBLE
            fragmentSearchBinding.cvNoResult.visibility = View.GONE

            val adapter =
                TopProductAdapter(model!!.popular_top_scroll!!, rivaLookBookActivity!!)
            adapter.productWidth = this.productWidth
            adapter.productheight = this.productheight
            val layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL, false
            )

//            fragmentSearchBinding.rcyTop.itemAnimator =
//                androidx.recyclerview.widget.DefaultItemAnimator()
//            fragmentSearchBinding.rcyTop.layoutManager = layoutManager
//            fragmentSearchBinding.rcyTop.adapter = adapter

        } else {

//            fragmentSearchBinding.rcyTop.visibility = View.GONE
            fragmentSearchBinding.cvNoResult.visibility = View.VISIBLE
        }

        if (model != null && model?.popular_searches != null && model?.popular_searches?.size ?: 0 > 0) {

//            fragmentSearchBinding.txtPopular.visibility = View.VISIBLE
            fragmentSearchBinding.rcyPopularSearch.visibility = View.VISIBLE
            fragmentSearchBinding.cvNoResult.visibility = View.GONE

            val adapter =
                PopularSearchAdapter(model!!.popular_searches!!, rivaLookBookActivity!!)
            val layoutManager = GridLayoutManager(rivaLookBookActivity, 1)

            fragmentSearchBinding.rcyPopularSearch.itemAnimator =
                androidx.recyclerview.widget.DefaultItemAnimator()
            fragmentSearchBinding.rcyPopularSearch.layoutManager = layoutManager
            fragmentSearchBinding.rcyPopularSearch.adapter = adapter

        } else {

//            fragmentSearchBinding.txtPopular.visibility = View.GONE
            fragmentSearchBinding.cvNoResult.visibility = View.VISIBLE
            fragmentSearchBinding.rcyPopularSearch.visibility = View.GONE
        }

//        if (model != null && model?.popular_banners != null && model?.popular_banners?.size ?: 0 > 0) {
//
////            fragmentSearchBinding.relImage.visibility = View.VISIBLE
//
//            val margin = resources.getDimension(R.dimen.twenty_dp).toInt()
//
//            fragmentSearchBinding.popularPager.adapter = SearchImagePagerAdapter(
//                model!!.popular_banners as java.util.ArrayList<HomeDataModel.PopularBanner>,
//                rivaLookBookActivity!!,
//                screenWidth,
//                layoutInflater
//            )
//            fragmentSearchBinding.popularPager.currentItem = 0
//            fragmentSearchBinding.viewPagerIndicator.noOfPages = model!!.popular_banners?.size ?: 0
//            fragmentSearchBinding.viewPagerIndicator.visibleDotCounts = 7
//            fragmentSearchBinding.viewPagerIndicator.onPageChange(0)
//            fragmentSearchBinding.popularPager.isCycle = true
//            fragmentSearchBinding.popularPager.startAutoScroll(5000)
//
//            fragmentSearchBinding.popularPager.addOnPageChangeListener(object :
//                ViewPager.OnPageChangeListener {
//
//                override fun onPageScrollStateChanged(state: Int) {
//                }
//
//                override fun onPageScrolled(
//                    position: Int,
//                    positionOffset: Float,
//                    positionOffsetPixels: Int
//                ) {
//                }
//
//                override fun onPageSelected(position: Int) {
//
//                    if (position > fragmentSearchBinding.viewPagerIndicator.noOfPages - 1) {
//                        fragmentSearchBinding.viewPagerIndicator.onPageChange(fragmentSearchBinding.viewPagerIndicator.noOfPages - 1)
//                    } else {
//                        fragmentSearchBinding.viewPagerIndicator.onPageChange(position)
//                    }
//                }
//            })
//
//        } else {
//            fragmentSearchBinding.relImage.visibility = View.GONE
//        }
    }

    fun hideKeyboard() {
        val view: View =
            if (activity?.currentFocus == null) View(requireContext()) else activity?.currentFocus!!
        val inputMethodManager =
            rivaLookBookActivity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                rivaLookBookActivity!!,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED
        ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    rivaLookBookActivity!!,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {

                ActivityCompat.requestPermissions(
                    rivaLookBookActivity!!,
                    arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    rivaLookBookActivity!!,
                    arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
            return false
        } else {
            val intent = Intent(rivaLookBookActivity, StoreListingActivity::class.java)
            rivaLookBookActivity?.startActivity(intent)
            // showVideo()
            return true
        }
    }

    fun checkCameraPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                rivaLookBookActivity!!,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {


            if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.CAMERA),
                    MY_PERMISSIONS_REQUEST_CAMERA
                )

            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(
                    arrayOf(android.Manifest.permission.CAMERA),
                    MY_PERMISSIONS_REQUEST_CAMERA
                )
            }
            return false
        } else {
            getCamera()
            return true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA) {

            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCamera()

            } else {

                if (permissions != null && permissions.size > 0) {

                    val showRationale = shouldShowRequestPermissionRationale(permissions[0])
                    if (!showRationale) {

                        // user also CHECKED "never ask again"
                        // you can either enable some fall back,
                        // disable features of your app
                        // or open another dialog explaining
                        // again the permission and directing to
                        // the app setting


                        val alertBuilder = AlertDialog.Builder(rivaLookBookActivity)
                        alertBuilder
                            .setMessage("Please allow camera permission for StoreApp from setting page to scan barcode.")
                            .setCancelable(false)
                            .setNegativeButton("Ok") { dialog, id ->
                                dialog.dismiss()
                            }
                            .setPositiveButton("Settings") { dialog, id ->
                                try {
                                    val intent = Intent(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts(
                                            "package",
                                            "com.armada.storapp",
                                            null
                                        )
                                    )
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                dialog.dismiss()
                            }

                        val alertDialog = alertBuilder.create()
                        alertDialog.show()

                        val textView =
                            alertDialog.findViewById<View>(android.R.id.message) as TextView
                    } else if (android.Manifest.permission.CAMERA.equals(permissions.get(0))) {

                        // user did NOT check "never ask again"
                        // this is a good place to explain the user
                        // why you need the permission and ask if he wants
                        // to accept it (the rationale)
                    }
                }
            }

        } else if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted, yay! Do the
                // location-related task you need to do.
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ) === PackageManager.PERMISSION_GRANTED
                ) {

                    getLocation()
                }
            } else {
                val intent = Intent(rivaLookBookActivity, StoreListingActivity::class.java)
                rivaLookBookActivity?.startActivity(intent)
                //Default
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
        }
    }

    private fun getCamera() {

        val intent = Intent(rivaLookBookActivity, BarcodeScannerActivity::class.java)
        startForResult?.launch(intent)
    }

    private fun getLocation() {
        gps = GPSTracker(rivaLookBookActivity)
        if (gps!!.canGetLocation()) {
            val intent = Intent(rivaLookBookActivity, StoreListingActivity::class.java)
            rivaLookBookActivity?.startActivity(intent)
        }
    }

    fun getRecentSearchList() {

        try {
            arrListRecentSearch = java.util.ArrayList<RecentSearch>()
            //todo
            if (arrListRecentSearch.isNotEmpty() && arrListRecentSearch.size > 0) {
                arrListRecentSearch.reverse()
                val adapter = RecentSearchAdapter(arrListRecentSearch, rivaLookBookActivity)
                val layoutManager = LinearLayoutManager(
                    rivaLookBookActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                fragmentSearchBinding.rcyRecentSearch.itemAnimator = DefaultItemAnimator()
                fragmentSearchBinding.rcyRecentSearch.layoutManager = layoutManager
                fragmentSearchBinding.rcyRecentSearch.adapter = adapter
                fragmentSearchBinding.lnrRecent.visibility = View.VISIBLE

            } else {
                fragmentSearchBinding.lnrRecent.visibility = View.GONE
            }
        } catch (e: Exception) {
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK && requestCode == 501 && data != null && data.hasExtra(
//                "barcode"
//            )
//        ) {
//            val barcode = data.getStringExtra("barcode")
//            searchQuery(barcode!!)
//        }
//    }


    interface OnItemClickListener {
        fun onItemClick()

    }
}
