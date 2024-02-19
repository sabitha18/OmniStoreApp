package com.armada.storeapp.ui.home.riva.riva_look_book.home

import android.graphics.Bitmap
import android.os.Bundle
import android.os.CountDownTimer
import android.util.DisplayMetrics
import android.view.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.UiThread
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import coil.load
import coil.request.ImageRequest
import coil.transition.CrossfadeTransition
import com.armada.storeapp.R
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.response.*
import com.armada.storeapp.databinding.FragmentSubcollectionBinding
import com.armada.storeapp.databinding.HomeBannerItemBinding
import com.armada.storeapp.databinding.HomeEditorialItemBinding
import com.armada.storeapp.databinding.HomeViewItemBinding
import com.armada.storeapp.ui.home.riva.riva_look_book.RivaLookBookActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.home.Algolia.ExpandableRecyler.ExpandableRecyclerAdapter
import com.armada.storeapp.ui.home.riva.riva_look_book.home.adapter.HomeDataAdapter
import com.armada.storeapp.ui.home.riva.riva_look_book.home.adapter.HomeEditAdapter
import com.armada.storeapp.ui.home.riva.riva_look_book.home.adapter.HomeImageAdapter
import com.armada.storeapp.ui.home.riva.riva_look_book.home.model.HomeEditModel
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing.ProductListingFragment
import com.armada.storeapp.ui.utils.Constants
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import com.armada.storeapp.ui.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class SubCollectionFragment : Fragment() {

    lateinit var sharedpreferenceHandler: SharedpreferenceHandler
    lateinit var fragmentSubcollectionBinding: FragmentSubcollectionBinding
    private var dataAdapter: HomeDataAdapter? = null
    private var isFromRefresh: Boolean? = false
    private var strCountryCode: String = "KW"
    private var model: HomeDataModel? = null
    private var rivaLookBookActivity: RivaLookBookActivity? = null

    /////////////////////////////////////////////////////////////
    ////////For Sale //////////////////////////////////////

    private var mCountDownTimer: CountDownTimer? = null
    private var strStartDate = ""
    private var strEndDate = ""
    private var strCllctnId = ""
    private var strName = ""
    private var boolFrom = false
    private var selectedLanguage="en"
    private var selectedCurrency="USD"
    //////////////////////////////////////////////////////
    ////////Timeline

    var arrListTimeLineGroupIds = java.util.ArrayList<Int>()
    var arrListTimeLineIds = java.util.ArrayList<Int>()
    private var isFragmentHidden = false

    lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentSubcollectionBinding = FragmentSubcollectionBinding.inflate(inflater)
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        rivaLookBookActivity = activity as RivaLookBookActivity
        sharedpreferenceHandler= SharedpreferenceHandler(rivaLookBookActivity!!)
         selectedLanguage=sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_SELECTED_COUNTRY,"en")!!
         selectedCurrency=sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_SELECTED_CURRENCY,"USD")!!
        init()
        return fragmentSubcollectionBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    ///
    private fun init() {

        if (arguments?.getBoolean("from_home", false) != null && arguments?.getBoolean(
                "from_home",
                false
            ) == true
        )
            boolFrom = true

        strCllctnId = try {
            arguments?.getInt("id").toString()
        } catch (e: Exception) {
            e.printStackTrace()
            arguments?.getInt("id", 0).toString()
        }

        if (!arguments?.getString("name").isNullOrEmpty()) {
            strName = arguments?.getString("name").toString()
        }
        fragmentSubcollectionBinding?.toolbarActionbar?.txtHead?.text = strName
        fragmentSubcollectionBinding?.toolbarActionbar?.tvFilter?.visibility = View.GONE

        fragmentSubcollectionBinding?.swipeRefreshLayout?.setOnRefreshListener {
            isFromRefresh = true
            fragmentSubcollectionBinding?.swipeRefreshLayout?.isRefreshing = true
            fragmentSubcollectionBinding?.swipeRefreshLayout.postDelayed(Runnable {
                fragmentSubcollectionBinding?.swipeRefreshLayout.isRefreshing = false
                //Load Home page data
                if (Utils.hasInternetConnection(activity)) {
                    loadHomePageData()
                } else {
                    Utils.showSnackbar(
                        fragmentSubcollectionBinding.root,
                        "Please check your internet connection"
                    )
                }
            }, 1000)
        }

        ////getting timeline
//        getTimelineIds(helper)//todo


        if (Utils.hasInternetConnection(requireContext())) {
            loadHomePageData()
        }


    }

    fun getHeader(): String {
        return strName
    }

    fun saleTimer(strStartDate: String, strEndDate: String) {
        if (strEndDate != null && !strEndDate.isEmpty()) {
            try {
                val formatter = SimpleDateFormat("dd.MM.yyyy, HH:mm:ss", Locale.US)
                formatter.isLenient = false
                formatter.timeZone = TimeZone.getTimeZone("gmt")

                val endTime = strEndDate
                var endmilliseconds = 0L

                var endDate: Date? = null
                var startTime = System.currentTimeMillis()

                try {
                    endDate = formatter.parse(endTime)
                    endmilliseconds = endDate.time

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                mCountDownTimer = object : CountDownTimer(endmilliseconds, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        try {
                            startTime -= 1
                            val serverUptimeSeconds = (millisUntilFinished - startTime) / 1000
                            // var daysLeft :String= String.format(Locale.ENGLISH, (serverUptimeSeconds / 86400).toString())
                            val daysLeft = String.format(
                                Locale.ENGLISH,
                                "%02d",
                                (serverUptimeSeconds / 86400).toInt()
                            )
                            //txtViewDays.setText(daysLeft);
                            //Log.d("daysLeft", daysLeft)
                            val hoursLeft = String.format(
                                Locale.ENGLISH,
                                "%02d",
                                (serverUptimeSeconds % 86400 / 3600).toInt()
                            )
                            //txtViewHours.setText(hoursLeft);
                            //Log.d("hoursLeft", hoursLeft)

                            val minutesLeft = String.format(
                                Locale.ENGLISH,
                                "%02d",
                                (serverUptimeSeconds % 86400 % 3600 / 60).toInt()
                            )
                            //txtViewMinutes.setText(minutesLeft);
                            //Log.d("minutesLeft", minutesLeft)

                            val secondsLeft = String.format(
                                Locale.ENGLISH,
                                "%02d",
                                (serverUptimeSeconds % 86400 % 3600 % 60).toInt()
                            )
                            //txtViewSecond.setText(secondsLeft);
                            //Log.d("secondsLeft", secondsLeft)

                            fragmentSubcollectionBinding?.saleView?.txtDays?.text = daysLeft
                            fragmentSubcollectionBinding?.saleView?.txtHours?.text = hoursLeft
                            fragmentSubcollectionBinding?.saleView?.txtMins?.text = minutesLeft
                            fragmentSubcollectionBinding?.saleView?.txtSeconds?.text = secondsLeft

                            if (daysLeft.toInt() <= 0 && hoursLeft.toInt() <= 0 && minutesLeft.toInt() <= 0 && secondsLeft.toInt() <= 0) {
                                mCountDownTimer!!.cancel()
                                fragmentSubcollectionBinding?.saleView?.cvSale?.visibility =
                                    View.GONE
                            }
                        } catch (e: Exception) {
                        }
                    }

                    override fun onFinish() {
                    }
                }.start()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //Load Home Page Data from Api
    private fun loadHomePageData() {

        if (boolFrom)
            getBannerCollections(strCllctnId)
        else
            getCategorySubcollections(strCllctnId)
    }

    fun getBannerCollections(collectionId: String) {
        homeViewModel.getBannerCollections(selectedLanguage,"", collectionId)
        homeViewModel.responseBannerCollection.observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    isFromRefresh = false
                    fragmentSubcollectionBinding?.swipeRefreshLayout?.isRefreshing = false
                    handleResponse(it.data!!)
                    rivaLookBookActivity?.dismissProgress()

                }

                is Resource.Error -> {
                    isFromRefresh = false
                    fragmentSubcollectionBinding?.swipeRefreshLayout?.isRefreshing = false
                    rivaLookBookActivity?.dismissProgress()
                }
                is Resource.Loading -> {
                    if (!isFromRefresh!!) {
                        rivaLookBookActivity?.showProgress()
                    }

                }
            }
        }
    }

    fun getCategorySubcollections(collectionId: String) {
        homeViewModel.getCategorySubCollection("", collectionId)
        homeViewModel.responseCategorySubcollection.observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    isFromRefresh = false
//                    val homeDataModel=HomeDataModel(it?.data?.data?.collectionGroups!!,"",200,true)
                    fragmentSubcollectionBinding?.swipeRefreshLayout?.isRefreshing = false
                    setHomeView(it.data?.data?.collectionGroups)
                    rivaLookBookActivity?.dismissProgress()


                }

                is Resource.Error -> {
                    isFromRefresh = false
                    fragmentSubcollectionBinding?.swipeRefreshLayout?.isRefreshing = false
                    rivaLookBookActivity?.dismissProgress()
                }
                is Resource.Loading -> {
                    if (!isFromRefresh!!) {
                        rivaLookBookActivity?.showProgress()
                    }

                }
            }
        }
    }


    //Here We can handle response
    private fun handleResponse(homeList: HomeDataModel) {
        fragmentSubcollectionBinding?.swipeRefreshLayout?.isRefreshing = false
        if (homeList.data?.sale != null) {

            val displayMetrics = DisplayMetrics()
            rivaLookBookActivity?.windowManager?.defaultDisplay?.getMetrics(
                displayMetrics
            )
            val width: Double = displayMetrics.widthPixels.toDouble()
            val density: Double = (width / 320)
            var imageHeight = homeList.data.sale.sale_image_height.toDouble()

            if (imageHeight > 100)
                imageHeight *= density
            else imageHeight = 100 * density

            val params = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                imageHeight.toInt()
            )
            fragmentSubcollectionBinding?.saleView?.imgSale.layoutParams = params
            fragmentSubcollectionBinding?.saleView?.cvSale.layoutParams = params
            fragmentSubcollectionBinding?.saleView?.cvSale.visibility = View.VISIBLE

            if (homeList.data.sale.sale_start_date != null && !homeList.data.sale.sale_start_date.isEmpty()) {
                strStartDate = Utils.getFormattedDateTime(
                    "yyyy-MM-dd HH:mm:ss",
                    "dd.MM.yyyy, HH:mm:ss",
                    homeList.data.sale.sale_start_date
                )
            }
            if (homeList.data.sale.sale_end_date != null && !homeList.data.sale.sale_end_date.isEmpty()) {
                strEndDate = Utils.getFormattedDateTime(
                    "yyyy-MM-dd HH:mm:ss",
                    "dd.MM.yyyy, HH:mm:ss",
                    homeList.data.sale.sale_end_date
                )
                saleTimer(strStartDate, strEndDate)
            } else {
                fragmentSubcollectionBinding?.saleView?.cvSale.visibility = View.GONE
            }

            if (homeList.data.sale.sale_image != null && homeList.data.sale.sale_image != "") {
                fragmentSubcollectionBinding?.saleView?.imgSale.visibility = View.VISIBLE
                Utils.loadImagesUsingCoil(
                    requireContext(),
                    homeList.data.sale.sale_image,
                    fragmentSubcollectionBinding?.saleView?.imgSale
                )

            } else {
                fragmentSubcollectionBinding?.saleView?.imgSale.visibility = View.GONE
            }

            fragmentSubcollectionBinding?.saleView?.cvSale.setOnClickListener {

                val arrList = ArrayList<String>()
                val arrListCollection = java.util.ArrayList<CollectionListItemModel>() //Dummy
                val arListPattrn = java.util.ArrayList<PatternsProduct>()  /// Dummy
                val arrListTimeline = ArrayList<TimeLineModel>() //Dummy
                val model = CollectionListItemModel(
                    "",
                    "",
                    "",
                    homeList.data.sale.sale_category_id,
                    homeList.data.sale.sale_title,
                    "",
                    "",
                    homeList.data.sale.sale_path,
                    homeList.data.sale.sale_category_path,
                    "0",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    homeList.data.sale.sale_title,
                    homeList.data.sale.sale_category_id,
                    homeList.data.sale.sale_path,
                    homeList.data.sale.sale_category_path,
                    0,
                    0,
                    "",
                    arListPattrn as ArrayList<PatternsProduct?>,
                    0,
                    "",
                    arrListCollection as ArrayList<CollectionListItemModel?>,
                    0,
                    arrList,
                    0,
                    0
                )
//                arrListTimeline as ArrayList<TimeLineModel?>,
                val bundle = Bundle()
                bundle.putInt("banner_height", 0)
                bundle.putSerializable("model", model)
                val productListingFragment = ProductListingFragment()
                productListingFragment.arguments = bundle
//                rivaLookBookActivity?.replaceFragment(productListingFragment)
                rivaLookBookActivity?.navController?.navigate(
                    R.id.navigation_product_listing,
                    bundle
                )
            }

        } else {
            fragmentSubcollectionBinding?.saleView?.cvSale.visibility = View.GONE
        }

        model = homeList
        setHomeView(model?.data!!.collectionGroups)
        if (!isFromRefresh!!) {
            rivaLookBookActivity?.dismissProgress()
        }
    }

    ///
    private fun setHomeView(collectionData: List<CollectionGroupsItemModel?>?) {

        val displayMetrics = DisplayMetrics()
        rivaLookBookActivity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val value = resources.displayMetrics.density
        val width: Double = displayMetrics.widthPixels.toDouble()
        //  Log.d("Width",width.toString())
        val density: Double = (width / 320)
        // Log.d("Density",density.toString())
        fragmentSubcollectionBinding?.lnrMain.removeAllViews()


        for (i in 0 until collectionData!!.size) {

            if ((collectionData[i]?.only_editorial ?: null != null) && (collectionData[i]?.only_editorial
                    ?: "0") == "1"
            ) {

                val imageHeight: Double = (collectionData[i]!!.image_height!! * (density))
                val imageMargin: Double = (collectionData[i]!!.image_margin!! * (density))
                val topMargin: Double = (collectionData[i]!!.margin_top!! * (density))
                val bottomMargin: Double = (collectionData[i]!!.margin_bottom!! * (density))
                val collectionTitle = collectionData[i]!!.hide_collection_title!!
                val collectionSubTitle = collectionData[i]!!.hide_collection_sub_title!!

                val parent = LinearLayout(requireContext())
                parent.layoutParams = LinearLayout.LayoutParams(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                    LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                )
                parent.orientation = LinearLayout.VERTICAL

                val homeEditorialItemBinding = HomeEditorialItemBinding.inflate(layoutInflater)

                if (collectionData[i]!!.hide_title == 1) {
                    homeEditorialItemBinding.txtEditTitle.visibility = View.GONE

                } else if (collectionData[i]!!.hide_title == 0) {

                    val relParam = RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    relParam.addRule(RelativeLayout.CENTER_HORIZONTAL)
                    relParam.setMargins(
                        (10 * density).toInt(),
                        topMargin.toInt(),
                        (10 * density).toInt(),
                        0
                    )

                    homeEditorialItemBinding.txtEditTitle.visibility = View.VISIBLE
                    homeEditorialItemBinding.txtEditTitle.layoutParams = relParam
                    homeEditorialItemBinding.txtEditTitle.text = collectionData[i]!!.title

                    if (topMargin > 0)
                        homeEditorialItemBinding.txtEditTitle.setPadding(
                            topMargin.toInt(),
                            0,
                            topMargin.toInt(),
                            0
                        )
                    else homeEditorialItemBinding.txtEditTitle.setPadding(
                        topMargin.toInt(),
                        0,
                        topMargin.toInt(),
                        0
                    )
                }


                //If editorials collection size is single
                if (collectionData[i]!!.collection_list!!.size == 1) {

                    if ((width.toInt() > 0) && ((collectionData[i]?.image_height ?: 0) > 0)) {
                        homeEditorialItemBinding.imgEditorial.load(if (collectionData[i]?.image != null && collectionData[i]?.image != "") collectionData[i]?.image else Constants.strNoImage) {
                            crossfade(true)
                            crossfade(800)
                            allowConversionToBitmap(true)
                            size(width.toInt(), collectionData[i]!!.image_height!!.toInt())
                            bitmapConfig(Bitmap.Config.ARGB_8888)
                            allowHardware(true)
                            listener(object : ImageRequest.Listener {

                            })
                            transition(CrossfadeTransition())
                        }
                    }

                    homeEditorialItemBinding.imgEditorial.visibility = View.VISIBLE
                    homeEditorialItemBinding.rcyEditorials.visibility = View.GONE

                    homeEditorialItemBinding.imgEditorial.setOnClickListener {
                        val bundle =
                            Bundle()
                        bundle.putString("id", collectionData[i]!!.collection_list!![0].type_id)
                        bundle.putString("name", collectionData[i]!!.collection_list!![0].title)
                        rivaLookBookActivity?.navController?.navigate(
                            R.id.navigation_editorial,
                            bundle
                        )
                    }

                } else {

                    //For Multiple collection size

                    homeEditorialItemBinding.imgEditorial.visibility = View.GONE
                    homeEditorialItemBinding.rcyEditorials.visibility = View.VISIBLE

                    val layoutManager =
                        androidx.recyclerview.widget.LinearLayoutManager(requireContext())

                    val arListEdit = ArrayList<HomeEditModel>()
                    val model = collectionData[i]?.collection_list?.let {
                        HomeEditModel(
                            collectionData[i]?.image.toString(),
                            it
                        )
                    }
                    model?.let { arListEdit.add(it) }

                    val mAdapter = HomeEditAdapter(
                        rivaLookBookActivity,
                        arListEdit,
                        imageHeight.toInt(),
                        topMargin.toInt(),
                        bottomMargin.toInt(),
                        collectionData[i]!!.image_height!!.toInt(),
                        collectionData[i]
                    )
                    mAdapter.setExpandCollapseListener(object :
                        ExpandableRecyclerAdapter.ExpandCollapseListener {

                        @UiThread
                        override fun onParentExpanded(parentPosition: Int) {

                            homeEditorialItemBinding.rcyEditorials.postDelayed(Runnable {
                                homeEditorialItemBinding.rcyEditorials.parent.requestChildFocus(
                                    homeEditorialItemBinding.rcyEditorials,
                                    homeEditorialItemBinding.rcyEditorials
                                )

                            }, 100)
                        }

                        @UiThread
                        override fun onParentCollapsed(parentPosition: Int) {
                        }
                    })
                    homeEditorialItemBinding.rcyEditorials.isNestedScrollingEnabled = false
                    homeEditorialItemBinding.rcyEditorials.adapter = mAdapter
                    homeEditorialItemBinding.rcyEditorials.layoutManager = layoutManager
                }

                parent.addView(homeEditorialItemBinding.root)
                fragmentSubcollectionBinding?.lnrMain?.addView(parent)

            } else if (collectionData[i]?.image_width!! == 320 && collectionData[i]?.collection_list != null && collectionData[i]?.collection_list?.size ?: 0 > 1) {

                // Viewpager - image width is equals to 320 & count is greater than 1

                var imageHeight: Double = (collectionData[i]?.image_height!! * (density))
                val topMargin: Double = (collectionData[i]?.margin_top!! * (density))
                val bottomMargin: Double = (collectionData[i]?.margin_bottom!! * (density))
                val collectionTitle = collectionData[i]?.hide_collection_title!!
                val collectionSubTitle = collectionData[i]?.hide_collection_sub_title!!

                val parent = LinearLayout(requireContext())
                parent.layoutParams = LinearLayout.LayoutParams(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                    LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                )
                parent.orientation = LinearLayout.VERTICAL

                val homeViewBannerItemBinding = HomeBannerItemBinding.inflate(layoutInflater)

                //apply margin top/bottom to main layout
                val layoutParam = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
                )
                layoutParam.setMargins(0, topMargin.toInt(), 0, bottomMargin.toInt())
                homeViewBannerItemBinding.root.layoutParams = layoutParam


                if (collectionData[i]?.hide_title == 1) {
                    homeViewBannerItemBinding.txtBannerTitle.visibility = View.GONE

                } else if (collectionData[i]?.hide_title == 0) {

                    val relParam = RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    relParam.addRule(RelativeLayout.CENTER_HORIZONTAL)
                    //small_param1.setMargins(image_Margin.toInt(), top_margin.toInt(), 0, bottom_margin.toInt())
                    homeViewBannerItemBinding.txtBannerTitle.visibility = View.VISIBLE

                    homeViewBannerItemBinding.txtBannerTitle.layoutParams = relParam
                    homeViewBannerItemBinding.txtBannerTitle.text = collectionData[i]?.title
                    if (topMargin > 0)
                        homeViewBannerItemBinding.txtBannerTitle.setPadding(
                            resources.getDimension(R.dimen.seven_dp).toInt(),
                            0,
                            resources.getDimension(R.dimen.seven_dp).toInt(),
                            0
                        )
                    else homeViewBannerItemBinding.txtBannerTitle.setPadding(
                        resources.getDimension(
                            R.dimen.seven_dp
                        ).toInt(),
                        resources.getDimension(R.dimen.five_dp).toInt(),
                        resources.getDimension(R.dimen.seven_dp).toInt(),
                        resources.getDimension(R.dimen.seven_dp).toInt()
                    )
                }
                homeViewBannerItemBinding.Homepager.adapter = HomeImageAdapter(
                    requireContext(),
                    imageHeight.toInt(),
                    0,
                    0,
                    collectionTitle,
                    collectionSubTitle,
                    (collectionData[i]!!.collection_list as List<CollectionListItemModel>?)!!,
                    boolFrom,
                    rivaLookBookActivity!!
                )
                homeViewBannerItemBinding.viewPagerIndicator.noOfPages =
                    collectionData[i]!!.collection_list?.size as Int
                homeViewBannerItemBinding.viewPagerIndicator.visibleDotCounts = 7
                homeViewBannerItemBinding.viewPagerIndicator.onPageChange(homeViewBannerItemBinding.Homepager.currentItem)
                homeViewBannerItemBinding.Homepager.addOnPageChangeListener(object :
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

                        if (homeViewBannerItemBinding.Homepager.currentItem >= 0)
                            homeViewBannerItemBinding.viewPagerIndicator.onPageChange(
                                homeViewBannerItemBinding.Homepager.currentItem
                            )
                        else homeViewBannerItemBinding.viewPagerIndicator.onPageChange(0)
                    }
                })
                val titleHeight = resources.getDimension(R.dimen.sixty_dp)
                if (collectionTitle == 0) {
                    imageHeight += titleHeight
                }

                val pagerSize = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    imageHeight.toInt()
                )
                pagerSize.addRule(RelativeLayout.BELOW, R.id.txtBannerTitle)
                homeViewBannerItemBinding.Homepager.layoutParams = pagerSize
                homeViewBannerItemBinding.Homepager.currentItem = 0
                if (collectionData[i]?.collection_list!!.size < 2) {
                    homeViewBannerItemBinding.viewPagerIndicator.visibility = View.GONE
                }

                parent.addView(homeViewBannerItemBinding.root)
                fragmentSubcollectionBinding?.lnrMain?.addView(parent)

            } else {
                //println("HELLO BOSS ::: 1 " + collectionData[i]!!.image_height!!)

                val metrics = resources.displayMetrics
                val screenWidth = (metrics.widthPixels)
                val parent = LinearLayout(requireContext())

                val imageHeight: Double = (collectionData[i]!!.image_height!! * (density))
                val imageWidth: Double = (collectionData[i]!!.image_width!! * (density))
                val imageMargin: Double = (collectionData[i]!!.image_margin!! * (density))
                val topMargin: Double = (collectionData[i]!!.margin_top!! * (density))
                val bottomMargin: Double = (collectionData[i]!!.margin_bottom!! * (density))
                val collectionTitle = collectionData[i]!!.hide_collection_title!!
                val collectionSubTitle = collectionData[i]!!.hide_collection_sub_title!!
                val isTimeLine: String = collectionData[i]!!.is_timeline ?: "0"
                val showNewBadge = "0"

                parent.layoutParams = LinearLayout.LayoutParams(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                    LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                )
                parent.orientation = LinearLayout.VERTICAL

                val homeViewBinding = HomeViewItemBinding.inflate(layoutInflater)
                homeViewBinding.rvHomeviewList.layoutManager =
                    androidx.recyclerview.widget.LinearLayoutManager(
                        requireContext(),
                        androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,
                        false
                    )

                //apply margin top/bottom to main layout
                val layoutParam = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
                )
                layoutParam.setMargins(0, topMargin.toInt(), 0, bottomMargin.toInt())
                homeViewBinding.root.layoutParams = layoutParam

                if (collectionData[i]!!.hide_title == 1) {
                    val relParam = RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    relParam.addRule(RelativeLayout.CENTER_HORIZONTAL)
                    relParam.setMargins(0, 0, 0, 0)
                    homeViewBinding.txtCollectionTitle.visibility = View.GONE

                    // v2.txtCollectionTitle.setLayoutParams(small_param1)
                    // v2.txtCollectionTitle.setTextSize(globalClass!!.dp2px(resources.getDimension(R.dimen.DescriptionTextSize)).toFloat())

                } else if (collectionData[i]!!.hide_title == 0) {

                    val relParam = RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        resources.getDimension(R.dimen.thirty_dp).toInt()
                    )
                    relParam.addRule(RelativeLayout.CENTER_HORIZONTAL)
                    //small_param1.setMargins(image_Margin.toInt(), resources.getDimension(R.dimen.medium_margin).toInt(), 0, 0)
                    homeViewBinding.txtCollectionTitle.visibility = View.VISIBLE
                    homeViewBinding.txtCollectionTitle.gravity = Gravity.CENTER_VERTICAL

                    homeViewBinding.txtCollectionTitle.layoutParams = relParam
                    homeViewBinding.txtCollectionTitle.text = collectionData[i]!!.title

                }


                ///Timeline function for new badge on timeleine
                /// Using database to store ids and comparing then with apis id
                ///////////////////////////////////////////////////////////////////////////////////
                val arrListBadge = ArrayList<Int>()
                var isFound = false

                if (isTimeLine == "1") {

                    for (t in 0 until arrListTimeLineGroupIds.size) {

                        if (collectionData[i]!!.id == arrListTimeLineGroupIds[t]) {
                            isFound = true
                            break
                        }
                    }

                    for (p in 0 until collectionData[i]!!.collection_list!!.size) {

                        if (isFound) {
                            for (s in 0 until arrListTimeLineIds.size) {

                                if (collectionData[i]!!.collection_list!![p].id!!.toInt() == arrListTimeLineIds[s]) {
                                    arrListBadge.add(p, 1)
                                    break
                                } else arrListBadge.add(p, 0)

                            }
                        } else arrListBadge.add(0)

                    }

                } else arrListBadge.add(0)

                ///
                dataAdapter = HomeDataAdapter(selectedCurrency,
                    collectionData[i]!!.collection_list!!,
                    rivaLookBookActivity!!,
                    this,
                    imageHeight.toInt(),
                    imageWidth.toInt(),
                    imageMargin.toInt(),
                    0,
                    0,
                    collectionTitle,
                    collectionSubTitle,
                    isTimeLine,
                    collectionData[i]!!.id!!,
                    arrListBadge,
                    boolFrom,
                    rivaLookBookActivity!!
                )
                homeViewBinding.rvHomeviewList.adapter = dataAdapter

                homeViewBinding.rvHomeviewList.setOnScrollListener(object :
                    androidx.recyclerview.widget.RecyclerView.OnScrollListener() {

                })

                homeViewBinding.rvHomeviewList.setOnTouchListener(object : View.OnTouchListener {
                    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {

                        enableDisableSwipeRefresh(false)
                        return false
                    }
                })

                parent.addView(homeViewBinding.root)
                fragmentSubcollectionBinding?.lnrMain?.addView(parent)
            }
        }
    }

//    /////////GetTimeline todo
//    private fun getTimelineIds(helper: Helper) {
//
//        try {
//            arrListTimeLineGroupIds = ArrayList()
//            arrListTimeLineIds = ArrayList()
//            val c = helper.timelineIds
//            c.moveToFirst()
//            if (c.moveToFirst()) {
//                do {
//                    arrListTimeLineGroupIds.add(c.getInt(0))
//                    arrListTimeLineIds.add(c.getInt(1))
//
//                } while (c.moveToNext())
//            }
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//        }
//    }


    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        isFragmentHidden = hidden
    }

    private fun enableDisableSwipeRefresh(enable: Boolean) {
        if (fragmentSubcollectionBinding?.swipeRefreshLayout != null) {
            fragmentSubcollectionBinding?.swipeRefreshLayout?.isEnabled = enable
        }

    }


//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment SubCollectionFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            SubCollectionFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}