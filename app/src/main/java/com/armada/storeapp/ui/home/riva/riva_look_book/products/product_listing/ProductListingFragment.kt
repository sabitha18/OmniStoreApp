package com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.apollographql.apollo3.api.Optional
import com.armada.storeapp.CategoryQuery
import com.armada.storeapp.R
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.response.CollectionGroupsItemModel
import com.armada.storeapp.data.model.response.CollectionListItemModel
import com.armada.storeapp.data.model.response.HomeDataModel
import com.armada.storeapp.data.model.response.SubCategoryResponse
import com.armada.storeapp.data.onError
import com.armada.storeapp.data.onLoading
import com.armada.storeapp.data.onSuccess
import com.armada.storeapp.databinding.FragmentProductListingBinding
import com.armada.storeapp.type.*
import com.armada.storeapp.ui.home.riva.riva_look_book.RivaLookBookActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.home.Algolia.ExpandableRecyler.Model.CategoriesListMDataModel
import com.armada.storeapp.ui.home.riva.riva_look_book.product_listing.ProductFilterActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.AddToCartBottomSheetDialog
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.OmniProductDetailsActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.ProductDetailsActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing.adapter.ProductListAdapter
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing.listener.PaginationScrollListener
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing.listener.ProductListInterface
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing.match_with.MatchWithActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing.model.*
import com.armada.storeapp.ui.utils.Constants
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import com.armada.storeapp.ui.utils.Utils
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ProductListingFragment : Fragment() {
    private var selectedLanguage = "en"
    private var selectedCurrency = "USD"
    lateinit var sharedpreferenceHandler: SharedpreferenceHandler
    private var loading: Dialog? = null
    private var searchKeyword: String = ""
    private var fragmentProductListingBinding: FragmentProductListingBinding? = null
    lateinit var productListingViewModel: ProductListingViewModel
    private var rivaLookBookActivity: RivaLookBookActivity? = null

    private var productListAdapter: ProductListAdapter? = null
    private var arrProductList: ArrayList<ProductListMDataModel>? = ArrayList()
    private var arrUsedPattern = ArrayList<Int>()
    var empty_product: HomeDataModel.EmptyPages? = null
    private var firstCompletelyVisibleItem = -1

    private var strPriceRange = ""

    //pagination
    private var totalPages = 0
    private var numberOfResult = ""
    private var isFromRefresh: Boolean = false

    // If current page is the last page (Pagination will stop after this page load)
    private var isLastPage = false
    private var isPageLoading: Boolean = false
    private var currentPage = 1 // indicates the current page which Pagination is fetching.
    var isFirstPage = true

    var clearProducts = false//todo

    //filter todo
    var arrListFilterData: ArrayList<ProductListMAllFilterModel?>? = ArrayList()
    private val requestCodeFilter: Int = 7
    private var jsonAttributes = JsonObject()
    private var originalMaxPrice = 0.0

    //Filter arraylist for categories
    //Need to pass separate array list in graphql api for each type of filter
    private val arrFilterCategories = ArrayList<String>() //category filter
    private val arrFilterColors = ArrayList<String>() //color filter
    private val arrFilterSizes = ArrayList<String>() //size filter
    private val arrFilterBrands = ArrayList<String>() //brand filter
    private val arrFilterDiscount = ArrayList<String>() //discount filter

    var parentSort = ProductAttributeSortInput() //Sort object
    val arrAssociateList: ArrayList<AssociateProductModel> = ArrayList()
    var homeModel: CollectionListItemModel? = null
    private var header = ""
    private var strCategoryId = ""
    private var strDefaultCategoryID = ""
    private var has_banner = "0"
    private var strBanner = ""
    private var banner_height = 0
    private var mapPattern: TreeMap<Int, ArrayList<ProductListMDataModel>>? = null
    private var subCategoryList: ArrayList<CollectionGroupsItemModel>? = null

    private var strPath: String = ""
    private var strLevel: String = ""
    private var currentList = ArrayList<CategoryQuery.Item>()

    private var strSort = "2"

    private var minPrice = 0.00
    private var maxPrice = 0.00

    private var isExcludedChecked = false
    private var isSaleChecked = false

    var layoutManagerGlobal: GridLayoutManager? = null

    private var productListInterface: ProductListInterface? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentProductListingBinding =
            FragmentProductListingBinding.inflate(inflater, container, false)
        productListingViewModel =
            ViewModelProvider(this).get(ProductListingViewModel::class.java)
        rivaLookBookActivity = activity as RivaLookBookActivity
        rivaLookBookActivity?.binding?.imageViewRivaLogo?.visibility=View.GONE
        sharedpreferenceHandler = SharedpreferenceHandler(rivaLookBookActivity!!)
        selectedLanguage =
            sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_SELECTED_COUNTRY, "en")!!
        selectedCurrency =
            sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_SELECTED_CURRENCY, "USD")!!
        initData()
        setEmptyPage()
        setClickListeners()
//        rivaLookBookActivity?.BackPressed(this)
        return fragmentProductListingBinding?.root
    }

    private fun setClickListeners() {
        swipeToRefresh()
        fragmentProductListingBinding?.toolbarActionbar?.tvFilter?.setOnClickListener {
            displayFilter()
        }

        fragmentProductListingBinding?.txtContinue?.setOnClickListener {
            rivaLookBookActivity?.onBackPressed()
        }

        fragmentProductListingBinding?.toolbarActionbar?.imageViewBack?.setOnClickListener {
            rivaLookBookActivity?.onBackPressed()
        }

//        requireActivity()
//            .onBackPressedDispatcher
//            .addCallback(rivaLookBookActivity!!, object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
////                    Log.d(TAG, "Fragment back pressed invoked")
//                    // Do custom work here
//                    // if you want onBackPressed() to be called as normal afterwards
////                    if (isEnabled) {
////                        isEnabled = false
////                        requireActivity().onBackPressed()
////                    }
//                }
//            }
//            )
    }

    fun redirectingFromHomeScreen() {
        if (Utils.hasInternetConnection(requireContext())) {
            try {
                if (arguments?.getSerializable("model") != null) {

                    homeModel =
                        arguments?.getSerializable(Constants.MODEL) as CollectionListItemModel
                    header = homeModel?.title.toString()
                    fragmentProductListingBinding?.toolbarActionbar?.txtHead?.text = header
                    if (!homeModel!!.type_id.isNullOrEmpty()) {
                        strCategoryId = homeModel!!.type_id!!
                        strDefaultCategoryID = homeModel!!.type_id!!
                    } else if (homeModel?.category_id != null) {
                        strCategoryId = homeModel!!.category_id.toString()
                        strDefaultCategoryID = homeModel!!.category_id.toString()
                    }

                    val json = Gson().toJson(homeModel)

                    strPath = homeModel?.path.toString()
                    strLevel = homeModel?.lvl.toString()
                    strBanner = homeModel?.banner.toString()
                    has_banner = homeModel?.has_banner.toString()
                    banner_height = arguments?.getInt(Constants.BANNER_HEIGHT, 0)!!

                    if (homeModel?.title.isNullOrEmpty()) {
                        // if (!homeModel!!.name.isNullOrEmpty())
                        //txtHead.text = homeModel!!.name
                        header = homeModel?.name.toString()
                    }

                    if (strPath.isEmpty()) {
                        if (!homeModel?.category_path.isNullOrEmpty()) {
                            strPath = homeModel?.category_path.toString()
                        }
                    }

                    if (strLevel.isEmpty()) {
                        if (!homeModel?.category_level.isNullOrEmpty()) {
                            strLevel = homeModel?.category_level.toString()
                        }
                    }

                    if (homeModel?.banners != null && (homeModel?.banners?.size ?: 0) > 0) {
                        has_banner = "1"
                        strBanner = homeModel?.banners?.get(0).toString()
                        homeModel?.banner = strBanner
                        homeModel?.has_banner = "1"
                    }
//                    relCategories?.visibility = View.GONE
//                    linDummy.visibility = View.GONE
                    //println("Here i am categoriessssss 2222")
                } else {
                    val arrListCategories =
                        arguments?.getSerializable("categoriesModel") as ArrayList<CategoriesListMDataModel>
                    //setTopCategories(arrListCategories)
//                    relCategories?.visibility = View.GONE
//                    linDummy.visibility = View.VISIBLE
                }
            } catch (e: java.lang.Exception) {
            }


            /**here will check weather patterns is getting or not based on
             * that we will add banners in product listing as a Product model based on the pattern number(IndexPosition)*/
            if (homeModel != null && homeModel?.has_pattern != null && homeModel?.has_pattern == 1) {
                mapPattern = TreeMap<Int, java.util.ArrayList<ProductListMDataModel>>()
                /*var storecode = AppController.instance.getStoreCode()
                if (strCategoryId.isEmpty() || strCategoryId.equals("0")) {
                    storecode = if (isTextContainsArabic(strPath)) AppController.instance.getStoreCodeAr() else AppController.instance.getStoreCodeEng()
                }*/
                val productIds = homeModel?.pattern?.filter { it?.show_container_grid == 1 }
                    ?.joinToString(",") { it?.product_id.toString() }
                if (!productIds.isNullOrEmpty()) {
                    /** from banners we will we get product ids ,
                     *  to get associate product we are passing those ids to below api*/
                    getAssociateProducts(productIds.toString())
                } else {
                    makePatternBannersInProductModel()
                    productListAPI()

                }
            }
//            else {
//                productListAPI()
//
//            }
            //setTopCategoryData()
            //println("here i am : mapPattern ${mapPattern?.size}")

        } else {
//            Global.showSnackbar(
//                lnMain!!,
//                activity?.resources?.getString(R.string.noInternet).toString()
//            )
        }
    }

    /** adding banners in pattern using Treemap as a Product model based on the pattern number(IndexPosition)*/
    fun makePatternBannersInProductModel() {
        for (pattern in homeModel?.pattern!!) {
            var mediafile = ""
            if (pattern?.media_type == "V") {
                mediafile = pattern.media_file.toString()
            }
            val arrListCon = ArrayList<ProductListMConfigurableOptionModel>()
            val arrAssociateProducts = ArrayList<AssociateProductModel>()
            for (item in arrAssociateList) {
                if (pattern?.product_id.toString() == item.parent_id.toString()) {
                    arrAssociateProducts.add(item)
                }
            }
            val productModelPattern =
                ProductListMDataModel(
                    barcode = "",
                    brand = "",
                    configurable_option = arrListCon,
                    description = "",
                    enable_special_text = "",
                    final_price = pattern?.price.toString(),
                    has_options = 0,
                    id = pattern?.product_id.toString(),
                    image = pattern?.image,
                    is_salable = true,
                    name = pattern?.name,
                    options = null,
                    ordered_qty = "",
                    price = pattern?.price.toString(),
                    remaining_qty = 0,
                    sale_img = "",
                    sale_img_h = "0px",
                    sale_img_w = "0px",
                    short_description = "",
                    sku = "",
                    special_text = "",
                    type = "",
                    wishlist_item_id = 0,
                    is_wishList = false,
                    hasMargin = false,
                    has_custom_options = true,
                    arrListColors = null,
                    mediaType = pattern?.media_type,
                    mediaFile = mediafile,
                    custom_options = pattern?.height.toString(),
                    associated_products = arrAssociateProducts,
                    show_container_grid = pattern?.show_container_grid,
                    container_width = pattern?.container_width
                )
            if (mapPattern?.containsKey(pattern?.pattern_number ?: 0) == true) {
                val arrList = mapPattern?.get(pattern?.pattern_number)
                arrList?.add(productModelPattern)
                mapPattern?.set(
                    pattern?.pattern_number ?: 0,
                    arrList as ArrayList<ProductListMDataModel>
                )
            } else {
                val arrListProduct = ArrayList<ProductListMDataModel>()
                arrListProduct.add(productModelPattern)
                mapPattern?.set(pattern?.pattern_number ?: 0, arrListProduct)
            }
        }
    }

    fun displayFilter() {
        //  SimpleView.visibility = View.VISIBLE
        //println("color list :: " + arrListColor)
        //println("size list :: " + arrListSize)
        //println("discount list :: " + arrListDiscount)
        //println("brand list :: " + arrListBrand)
        //println("Sort order :: " + strSortBy)
        //println("originalMaxPrice :: " + originalMaxPrice)

        val intent = Intent(activity, ProductFilterActivity::class.java)
        intent.putExtra("filterData", arrListFilterData)
        intent.putExtra("sortBy", strSort)
        intent.putExtra("isExcludedChecked", isExcludedChecked)
        intent.putExtra("isSaleChecked", isSaleChecked)
//        intent.putExtra("isList", isList)
        intent.putExtra("maxPrice", maxPrice)
        intent.putExtra("minPrice", minPrice)
        intent.putExtra("originalMaxPrice", originalMaxPrice)
        startActivityForResult(intent, requestCodeFilter)
        activity?.overridePendingTransition(R.anim.bottom_up, R.anim.nothing)
    }

    private fun initData() {
        fragmentProductListingBinding?.toolbarActionbar?.imageViewBack?.visibility=View.VISIBLE
        arguments?.let {

            if (arguments?.containsKey(Constants.SELECTED_CATEGORY_ID) == true)
                strCategoryId = arguments?.getString(Constants.SELECTED_CATEGORY_ID, "")!!
            if (arguments?.getString("categoryId") != null) {
                strCategoryId = arguments?.getString("categoryId") as String
                strDefaultCategoryID = arguments?.getString("categoryId") as String
            }

            redirectingFromHomeScreen()

            try {
                if (arguments?.containsKey("searchKeyword")!!) {
                    searchKeyword = arguments?.getString("searchKeyword")!!
                    fragmentProductListingBinding?.toolbarActionbar?.txtHead?.text = searchKeyword
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }

        }

        //Adding default category in filter
        if (strCategoryId.isNotEmpty()) {
            arrFilterCategories.add(strCategoryId)
        }

        getSortData()


        productListInterface = object : ProductListInterface {
            override fun onProductClickEvent(product: ProductListMDataModel) {
                val intent = Intent(rivaLookBookActivity, OmniProductDetailsActivity::class.java)
                intent.putExtra("id", product?.id)
                intent.putExtra("name", product?.name)
                intent.putExtra("image", product?.image)
                //intent.putExtra("size_guide", strSizeGuide)
                rivaLookBookActivity!!.startActivity(intent)
            }


            override fun toggleWishList(
                type: String?,
                wishlistId: String,
                position: Int,
                list: ArrayList<ProductListMDataModel>?
            ) {
//            adddelWishlist(type ?: "", wishlistId) todo
            }

            override fun onSeeTheLook(product: ProductListMDataModel) {
                val intent = Intent(rivaLookBookActivity, MatchWithActivity::class.java)
                intent.putExtra("id", product?.id)
                intent.putExtra("name", product?.name)
                intent.putExtra("image", product?.image)
                //intent.putExtra("size_guide", strSizeGuide)
                rivaLookBookActivity!!.startActivity(intent)
            }

        }
        layoutManagerGlobal =
            GridLayoutManager(requireContext(), 2)

        val allProducts =
            mutableListOf<CategoryQuery.Item?>()
        arrUsedPattern.clear()

        productListAdapter = ProductListAdapter(
            selectedCurrency,
            rivaLookBookActivity!!,
            arrProductList,
            null,
            productListInterface!!
        )
        productListAdapter!!.onItemClick = { product ->
//            val intent = Intent(rivaLookBookActivity, ProductDetailsActivity::class.java)
//            intent.putExtra("id", product?.id)
//            intent.putExtra("name", product?.name)
//            intent.putExtra("image", product?.image)
//            //intent.putExtra("size_guide", strSizeGuide)
//            rivaLookBookActivity!!.startActivity(intent)
        }
        fragmentProductListingBinding?.rcyProductListing?.layoutManager =
            layoutManagerGlobal
        fragmentProductListingBinding?.rcyProductListing?.adapter = productListAdapter

        fragmentProductListingBinding?.rcyProductListing?.addOnScrollListener(
            object :
                PaginationScrollListener(layoutManagerGlobal) {

                override fun showUp() {
                }

                override fun hideUp() {

                }

                override fun loadVideo(
                    mLayoutManager: GridLayoutManager,
                    firstCompletelyVisibleItemPosition: Int
                ) {
                    firstCompletelyVisibleItem = firstCompletelyVisibleItemPosition
                }

                override fun pauseVideo(
                    mLayoutManager: GridLayoutManager,
                    firstCompletelyVisibleItemPosition: Int
                ) {

                }

                override val totalPageCount: Int = totalPages
                override val isLoading: Boolean
                    get() = false

                //override var isLoading: Boolean = false

                override fun loadMoreItems() {
                    //Increment page index to load the next one
                    //println("Here i am pagination Is last page: $isLastPage  is loading $isPageLoading")
                    if (!isLastPage && !isPageLoading) {
                        if (currentPage != totalPages) {
                            productListAdapter?.addLoadingFooter()
                        } else isLastPage = true
                        isFirstPage = false
                        isPageLoading = true
                        currentPage += 1
                        productListAPI()
                    }
                }
            })

        if (strCategoryId?.isNotEmpty())
            getSubCategories(strCategoryId)

        productListAPI()
    }

    fun getSubCategories(collectionId: String) {
        productListingViewModel.getSubCategories("", collectionId)
        productListingViewModel.responseSubCategory.observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    if (it.data?.data?.collectionGroups?.size!! > 0)
                        setTabData(it.data)
                    else
                        fragmentProductListingBinding?.tabLayout?.visibility = View.GONE
                }

                is Resource.Error -> {

                }
                is Resource.Loading -> {

                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        fragmentProductListingBinding?.toolbarActionbar?.tvFilter?.visibility = View.VISIBLE

        try {
            rivaLookBookActivity?.setBagCount()
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        rivaLookBookActivity?.hideLogo()
    }

    private fun setTabData(response: SubCategoryResponse?) {
        fragmentProductListingBinding?.tabLayout?.visibility = View.VISIBLE
        response?.data?.collectionGroups?.forEach {
            fragmentProductListingBinding?.tabLayout!!.addTab(
                fragmentProductListingBinding?.tabLayout!!.newTab()
                    .setText(it?.collection_list?.get(0)?.name)
            )
        }


        fragmentProductListingBinding?.tabLayout?.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = fragmentProductListingBinding?.tabLayout?.selectedTabPosition

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }


    fun getAssociateProducts(productIds: String) {
        productListingViewModel.getAssociateProducts(productIds)
        productListingViewModel.responseAssociateProduct.observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    dismissProgress()
                    for ((key, value) in it.data?.data!!) {
                        for ((key1, value1) in value.associated) {
                            arrAssociateList.addAll(listOf(value1!!))
                        }

                    }
                    makePatternBannersInProductModel()
                    productListAPI()
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

    private fun productListAPI() {
        showProgress()
        getSortData()

        //Create an optional list of filter params separately for each type of filter
        val categoryFilter =
            Optional.Present(
                FilterEqualTypeInput(
                    eq = Optional.Present(
                        strCategoryId
                    )
                )
            ) // category filter
        val colorFilter =
            Optional.Present(
                FilterEqualTypeInput(
                    `in` = Optional.Present(
                        arrFilterColors
                    )
                )
            ) //Color filter object
        val brandFilter =
            Optional.Present(
                FilterEqualTypeInput(
                    `in` = Optional.Present(
                        arrFilterBrands
                    )
                )
            ) //Brand filter object
        val discountFilter =
            Optional.Present(
                FilterEqualTypeInput(
                    `in` = Optional.Present(
                        arrFilterDiscount
                    )
                )
            ) //Discount filter object

        val sizeFilter =
            Optional.Present(FilterEqualTypeInput(`in` = Optional.Present(arrFilterSizes))) //Size filter object

        val priceFilter = if (maxPrice > 0) {
            Optional.Present(
                FilterRangeTypeInput(
                    from = Optional.Present(minPrice.toString()),
                    to = Optional.Present(maxPrice.toString())
                )
            ) //Pric filter object
        } else {
            Optional.Absent
        }

        //println("Here i am categories filter   " + Gson().toJson(categoryFilter) + "   list   " + arrFilterCategories)
        //Stock Filter
        val stock = if (isExcludedChecked) {
            Optional.Present(1)
        } else {
            Optional.Absent
        }
        val stockFilter =
            Optional.Present(AmShopbyCustomFilterTypeInput(eq = stock)) // stock filter object

        //sale filter
        val sale = if (isSaleChecked) {
            Optional.Present(1)
        } else {
            Optional.Absent
        }

        val saleFilter =
            Optional.Present(AmShopbyCustomFilterTypeInput(eq = sale))//sale filter object

        val parentFilter =
            ProductAttributeFilterInput(
                category_id = if (strCategoryId.isNotEmpty()) {
                    categoryFilter
                } else {
                    Optional.Absent
                },
                brand = brandFilter,
                color_swatch = colorFilter,
                price = priceFilter,
                size = sizeFilter,
                stock_status = stockFilter,
                am_on_sale = saleFilter,
                discount_percentage = discountFilter
            ) //Main filter object in query

        val sort = Optional.Present(parentSort)
        getProducts(50, currentPage, parentFilter, sort, searchKeyword)
    }

    fun getProducts(
        pageSize: Int, currentPage: Int, filter: ProductAttributeFilterInput,
        sort: Optional<ProductAttributeSortInput?>, search: String
    ) {
        try {
            productListingViewModel.getProducts(
                activity!!,
                pageSize,
                currentPage,
                filter,
                sort,
                search
            )
            productListingViewModel.responseProduct.observe(requireActivity()) {
                it.onSuccess { list ->
                    isFromRefresh = false
                    fragmentProductListingBinding?.swipeRefreshLayout?.isRefreshing = false
                    dismissProgress()
                    setData(list!!)
                }.onError { error ->
                    dismissProgress()
                    isFromRefresh = false
                    fragmentProductListingBinding?.swipeRefreshLayout?.isRefreshing = false
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
                    showProgress()
                }
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun setData(response: CategoryQuery.Products) {
        fragmentProductListingBinding?.toolbarActionbar?.tvResultCount?.text =
            "(${response.total_count})"

        val allProducts =
            mutableListOf<CategoryQuery.Item?>() //all products list from query response
        if (clearProducts) {
            arrUsedPattern.clear()
            arrProductList?.clear()
            currentList?.clear()
            allProducts.clear()
            isFirstPage = true
            currentPage = 1
            isLastPage = false
            clearProducts = false
            productListAdapter?.notifyDataSetChanged()
        }
        var newProducts: List<CategoryQuery.Item?>? = null
        if (currentList.size == 0) {
            currentList =
                response.items as ArrayList<CategoryQuery.Item> /* = java.util.ArrayList<com.armada.storeapp.CategoryQuery.Item> */
            newProducts = response?.items
        } else if (currentList.size > 0) {
            if (currentList.containsAll(response.items as ArrayList)) {

            } else {
                currentList.addAll(response.items as ArrayList<CategoryQuery.Item>)
                newProducts = response?.items
            }
        }

        dismissProgress()
        layoutManagerGlobal?.spanSizeLookup =
            object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val value: Int
                    value = when (productListAdapter?.getItemViewType(
                        position
                    )) {
                        productListAdapter?.VIEW_TYPE_ITEM -> 1
                        productListAdapter?.VIEW_TYPE_HEADER -> 2
                        else -> 1
                    }
                    return value
                }
            }
        totalPages = response.page_info?.total_pages ?: 0
        numberOfResult =
            response?.total_count.toString() + " " + getString(R.string.count_items)
//        if (activity is HomeActivity) {
//            (mActivity!!).displayHeaderInHome(header, numberOfResult, parentFragment)
//        }todo

        if (isFirstPage && arrListFilterData.isNullOrEmpty()) {
            val filterData = response?.aggregations
            filterData?.forEach {
                //Price max and min
                if (it?.attribute_code == "price") {
                    if (it?.options?.isNotEmpty() == true) {
                        val priceArray = it?.options?.get(
                            (it?.options?.size ?: 1) - 1
                        )?.value?.split("_")
                        if (priceArray?.size == 2) {
                            maxPrice = if (priceArray[1].toDouble() != 0.0) {
                                priceArray[1].toDouble()
                            } else {
                                priceArray[0].toDouble()
                            }
                        } else if (priceArray?.size == 1) {
                            maxPrice = priceArray[0].toDouble()
                        }
                    }
                    originalMaxPrice = maxPrice
                }
                //filterData?.get(i)?.attribute_code == "category_id" ||
                if (it?.attribute_code == "discount_percentage" || it?.attribute_code == "color_swatch"
                    || it?.attribute_code == "brand" || it?.attribute_code == "size"
                ) {
                    //println("Here i am filter valuesss  " + filterData[i]?.label)
                    val arrListFilterValues = ArrayList<ProductListMOptionModel>()
                    if (it?.attribute_code == "color_swatch") {
                        it?.options?.forEach {
                            val modelFilterValue = ProductListMOptionModel(
                                attribute_name = it?.label,
                                id = it?.value,
                                isSelected = false,
                                swatch_url = it?.swatch_data?.value.toString()
                            )
                            arrListFilterValues.add(modelFilterValue)
                        }
                    } else {
                        it?.options?.forEach {
                            val modelFilterValue = ProductListMOptionModel(
                                attribute_name = it?.label,
                                id = it?.value,
                                isSelected = false
                            )
                            arrListFilterValues.add(modelFilterValue)
                        }
                    }
                    val modelFilterType = ProductListMAllFilterModel(
                        attribute_id = "",
                        code = it?.attribute_code,
                        name = it?.label,
                        options = arrListFilterValues
                    )
                    arrListFilterData?.add(modelFilterType)
                }
            }
        }


        //pagination loading
        if (currentPage == totalPages) {
            isLastPage = true
        }

        isFirstPage = false

        //Creating new array list of products coming after pagination loading

//        val newProducts = response?.items
        productListAdapter?.removeLoadingFooter()
        //Adding new products in all previous products list and refreshing adapter
        if (!newProducts.isNullOrEmpty()) {
            //println("Here i am productssssss    " + newProducts[0]?.name)
            newProducts.let {
                allProducts.addAll(it)

            }
            isPageLoading = false

            newProducts?.forEach {
                val arrListConfig = ArrayList<ProductListMConfigurableOptionModel>()
                for (j in 0 until (it?.onConfigurableProduct?.configurable_options?.size
                    ?: 0)) {
                    val arrListConfigValues = ArrayList<ProductListMAttributeModel>()
                    for (k in 0 until (it?.onConfigurableProduct?.configurable_options?.get(
                        j
                    )?.values!!.size)) {
                        val modelConfigValues = ProductListMAttributeModel(
                            color_code = it?.onConfigurableProduct?.configurable_options?.get(
                                j
                            )?.values!![k]?.swatch_data?.onImageSwatchData?.thumbnail,
                            option_id = it?.onConfigurableProduct?.configurable_options?.get(
                                j
                            )?.values!![k]?.value_index.toString(),
                            value = it?.onConfigurableProduct?.configurable_options?.get(
                                j
                            )?.values!![k]?.label
                        )

                        arrListConfigValues.add(modelConfigValues)
                    }

                    val configModel = ProductListMConfigurableOptionModel(
                        attribute_code = it?.onConfigurableProduct?.configurable_options?.get(
                            j
                        )?.attribute_code,
                        attribute_id = it?.onConfigurableProduct?.configurable_options?.get(
                            j
                        )?.attribute_id?.toInt(),
                        attributes = arrListConfigValues,
                        type = it?.onConfigurableProduct?.configurable_options?.get(
                            j
                        )?.label,
                    )

                    arrListConfig.add(configModel)
                }
                val productModel = ProductListMDataModel(
                    barcode = "",
                    brand = "",
                    configurable_option = arrListConfig,
                    description = "",
                    enable_special_text = null,
                    final_price = it?.price_range?.minimum_price?.final_price?.value.toString(),
                    has_options = null,
                    id = it?.id.toString(),
                    image = it?.small_image?.url,
                    is_salable = true,
                    name = it?.name,
                    options = listOf(),
                    ordered_qty = null,
                    price = it?.price_range?.minimum_price?.regular_price?.value.toString(),
                    remaining_qty = null,
                    sale_img = null,
                    sale_img_h = null,
                    sale_img_w = null,
                    short_description = null,
                    sku = it?.sku,
                    special_text = null,
                    type = "",
                    wishlist_item_id = null,
                    is_wishList = null,
                    hasMargin = null,
                    has_custom_options = false,
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

                if (!arrProductList!!.contains(productModel))
                    arrProductList?.add(productModel)
            }


            /** here we are merging the products and the banners data in one list with specific position using pattern Treemap*/
            try {
                if (homeModel != null && homeModel?.has_pattern != null && homeModel?.has_pattern == 1) {
                    //  for (k in 0 until homeModel?.pattern?.size as Int) {
                    for ((key, value) in mapPattern as TreeMap) {
                        if ((key + (arrUsedPattern.size)) < arrProductList?.size ?: 0) {
                            /** value is Arraylist of products , here we are putting the banners on specific index in arrProductList using pattern numbers value */
                            if (value.size == 1) {
                                arrProductList?.add(
                                    key + (arrUsedPattern.size),
                                    value.get(0)
                                )
                                arrUsedPattern.add(key)
                            }
                            /** value is Arraylist of products cause 2 banners also we can get to handle that below logic is implemented */
                            if (value.size == 2) {
                                val tempArrListProduct =
                                    ArrayList<ProductListMDataModel>()
                                tempArrListProduct.addAll(value)
                                val arrListCon =
                                    ArrayList<ProductListMConfigurableOptionModel>()
                                val height = value.get(0).custom_options
                                val product = ProductListMDataModel(
                                    barcode = "",
                                    brand = "",
                                    configurable_option = arrListCon,
                                    description = "",
                                    enable_special_text = "",
                                    final_price = "",
                                    has_options = 0,
                                    id = "",
                                    image = "",
                                    is_salable = true,
                                    name = "",
                                    options = null,
                                    ordered_qty = "",
                                    price = "",
                                    remaining_qty = 0,
                                    sale_img = "",
                                    sale_img_h = "0px",
                                    sale_img_w = "0px",
                                    short_description = "",
                                    sku = "",
                                    special_text = "",
                                    type = "",
                                    wishlist_item_id = 0,
                                    is_wishList = false,
                                    hasMargin = false,
                                    has_custom_options = true,
                                    arrListColors = null,
                                    mediaType = "",
                                    mediaFile = "",
                                    custom_options = height,
                                    associated_products = null,
                                    show_container_grid = 0,
                                    container_width = "",
                                    hasMultipleBannerInOne = false,
                                    arrListMultipleProduct = tempArrListProduct
                                )
                                arrProductList?.add(
                                    key + (arrUsedPattern.size),
                                    product
                                )
                                arrUsedPattern.add(key)

                            }
                        }
                    }
                }
            } catch (e: java.lang.Exception) {

            }

            productListAdapter?.notifyDataSetChanged()
        }

        if (currentList.isNullOrEmpty()) {
            fragmentProductListingBinding?.relNoResult?.visibility = View.VISIBLE
            fragmentProductListingBinding?.tabLayout?.visibility = View.GONE
        } else {
            fragmentProductListingBinding?.relNoResult?.visibility = View.GONE
        }

    }


    fun swipeToRefresh() {
        fragmentProductListingBinding?.swipeRefreshLayout?.setProgressViewOffset(false, 0, 300)
        fragmentProductListingBinding?.swipeRefreshLayout?.setOnRefreshListener()
        {
            isFromRefresh = true
            fragmentProductListingBinding?.swipeRefreshLayout?.isRefreshing = true
            fragmentProductListingBinding?.swipeRefreshLayout?.postDelayed({
                fragmentProductListingBinding?.swipeRefreshLayout?.isRefreshing = false

                if (Utils.hasInternetConnection(activity)) {
                    arrListFilterData = ArrayList()
                    isFirstPage = true
                    isLastPage = false
                    currentPage = 1
                    clearProducts = true
                    productListAPI()
                } else {
                    Utils.showSnackbar(
                        fragmentProductListingBinding?.root!!,
                        activity?.resources?.getString(R.string.noInternet).toString()
                    )
                }
            }, 1000)
        }
    }

    private fun getSortData() {
        when (strSort) {
            "1" -> {
                val optionalSort = Optional.Present(SortEnum.DESC)
                parentSort = ProductAttributeSortInput(position = optionalSort)
            }
            "2" -> {
                val optionalSort = Optional.Present(SortEnum.ASC)
                parentSort = ProductAttributeSortInput(position = optionalSort)
            }
            "3" -> {
                val optionalSort = Optional.Present(SortEnum.DESC)
                parentSort = ProductAttributeSortInput(price = optionalSort)
            }
            "4" -> {
                val optionalSort = Optional.Present(SortEnum.ASC)
                parentSort = ProductAttributeSortInput(price = optionalSort)
            }
        }
    }

    private fun setEmptyPage() {
        empty_product = Utils.empty_product
        if (empty_product != null) {
            if (!empty_product?.icon.isNullOrEmpty()) {
                Utils.loadImagesUsingCoil(
                    requireContext(),
                    empty_product?.icon,
                    fragmentProductListingBinding?.imgEmptyProduct!!
                )
                //Glide.with(mActivity!!).load(emptyPage.icon).into(imgEmptyProduct!!)
                fragmentProductListingBinding?.imgEmptyProduct?.visibility = View.VISIBLE
            } else {
                fragmentProductListingBinding?.imgEmptyProduct?.visibility = View.GONE

            }
            if (!empty_product?.title.isNullOrEmpty()) {
                fragmentProductListingBinding?.txtNoResult?.text = empty_product?.title
                fragmentProductListingBinding?.txtNoResult?.visibility = View.VISIBLE
            } else {
                fragmentProductListingBinding?.txtNoResult?.visibility = View.GONE

            }
            if (!empty_product?.subtitle.isNullOrEmpty()) {
                fragmentProductListingBinding?.txtNote?.text = empty_product?.subtitle
                fragmentProductListingBinding?.txtNote?.visibility = View.VISIBLE
            } else {
                fragmentProductListingBinding?.txtNote?.visibility = View.GONE

            }

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestCodeFilter && data != null && data.hasExtra("applyFilter")) {
            arrListFilterData =
                data.getSerializableExtra("filterData") as ArrayList<ProductListMAllFilterModel?>
            val json = JsonObject() // json form for attribute

            //category - filter
            for (u in 0 until (arrListFilterData?.size ?: 0)) {
                //For categories list in filter
                //Comparing with attribute code, clearing array list and re-adding selected categories with previous category
                when (arrListFilterData?.get(u)?.code) {
                    "category_id" -> {
                        arrFilterCategories.clear()
                        if (strCategoryId.isNotEmpty()) {
                            arrFilterCategories.add(strCategoryId)
                        }
                        for (v in 0 until (arrListFilterData?.get(u)?.options?.size ?: 0)) {
                            if (arrListFilterData?.get(u)?.options?.get(v)?.isSelected == true) {
                                arrFilterCategories.add(arrListFilterData?.get(u)?.options?.get(v)?.id.toString())
                            }
                        }
                        //println("here i am : Selected filter categories $arrFilterCategories")
                    }
                    "color_swatch" -> {
                        arrFilterColors.clear()
                        for (v in 0 until (arrListFilterData?.get(u)?.options?.size ?: 0)) {
                            if (arrListFilterData?.get(u)?.options?.get(v)?.isSelected == true) {
                                arrFilterColors.add(arrListFilterData?.get(u)?.options?.get(v)?.id.toString())
                            }
                        }
                        //println("here i am : Selected filter colors $arrFilterColors")
                    }
                    "brand" -> {
                        arrFilterBrands.clear()
                        for (v in 0 until (arrListFilterData?.get(u)?.options?.size ?: 0)) {
                            if (arrListFilterData?.get(u)?.options?.get(v)?.isSelected == true) {
                                arrFilterBrands.add(arrListFilterData?.get(u)?.options?.get(v)?.id.toString())
                            }
                        }
                        //println("here i am : Selected filter brands $arrFilterBrands")
                    }
                    "discount_percentage" -> {
                        arrFilterDiscount.clear()
                        for (v in 0 until (arrListFilterData?.get(u)?.options?.size ?: 0)) {
                            if (arrListFilterData?.get(u)?.options?.get(v)?.isSelected == true) {
                                arrFilterDiscount.add(arrListFilterData?.get(u)?.options?.get(v)?.id.toString())
                            }
                        }
                    }

                    "size" -> {
                        arrFilterSizes.clear()
                        for (v in 0 until (arrListFilterData?.get(u)?.options?.size ?: 0)) {
                            if (arrListFilterData?.get(u)?.options?.get(v)?.isSelected == true) {
                                arrFilterSizes.add(arrListFilterData?.get(u)?.options?.get(v)?.id.toString())
                            }
                        }
                    }
                }

            }

            if (data.hasExtra("sortBy") && data.getStringExtra("sortBy") != null) {
                strSort = data.getStringExtra("sortBy") as String
                getSortData()
            }

            if (data.hasExtra("isExcludeChecked")) {
                isExcludedChecked = data.getBooleanExtra("isExcludeChecked", false)
            }
            if (data.hasExtra("isSaleChecked")) {
                isSaleChecked = data.getBooleanExtra("isSaleChecked", false)
            }
            maxPrice = data.getDoubleExtra("maxPrice", 0.00)
            minPrice = data.getDoubleExtra("minPrice", 0.00)
            strPriceRange = data.getStringExtra("range").toString()
            clearProducts = true
            currentPage = 1
            productListAPI()
        }

    }


    ///loading dialog
    private fun showProgress() {
        if (loading == null) {
            loading = Dialog(requireContext(), R.style.TranslucentDialog)
            //loading!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            loading?.setContentView(R.layout.custom_loading_view)
            loading?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            loading?.show()
            loading?.setCanceledOnTouchOutside(false)
        } else if (loading != null) {
            if (loading?.isShowing!!)
            else
                loading?.show()
        }
    }

    private fun dismissProgress() {
        if (loading != null)
            loading?.dismiss()
    }
}