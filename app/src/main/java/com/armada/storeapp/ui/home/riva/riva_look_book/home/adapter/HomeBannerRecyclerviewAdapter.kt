package com.armada.storeapp.ui.home.riva.riva_look_book.home.adapter

import android.app.Activity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.armada.storeapp.R
import com.armada.storeapp.data.model.response.CollectionGroupsItemModel
import com.armada.storeapp.data.model.response.CollectionListItemModel
import com.armada.storeapp.data.model.response.PatternsProduct
import com.armada.storeapp.data.model.response.TimeLineModel
import com.armada.storeapp.databinding.HomeBannerItemBinding
import com.armada.storeapp.databinding.HomeEditorialItemBinding
import com.armada.storeapp.databinding.HomeSaleItemBinding
import com.armada.storeapp.databinding.HomeViewItemBinding
import com.armada.storeapp.ui.home.riva.riva_look_book.RivaLookBookActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.home.EditorialFragment
import com.armada.storeapp.ui.home.riva.riva_look_book.home.model.HomeEditModel
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing.ProductListingFragment
import com.armada.storeapp.ui.utils.Constants
import com.armada.storeapp.ui.utils.Utils
import java.text.SimpleDateFormat
import java.util.*

class HomeBannerRecyclerviewAdapter(
    private val selectedCurrency:String,
    private val collectionData: ArrayList<CollectionGroupsItemModel>,
    private val mActivity: Activity,
    private val density: Double,
    val arrListTimeLineGroupIds: ArrayList<Int>,
    val arrListTimeLineIds: ArrayList<Int>,
    val arrListTime: ArrayList<HomeDataAdapter>,
    private var mCountDownTimer: CountDownTimer? = null,
    private val rivaLookBookActivity: RivaLookBookActivity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_SALE = 0 //For sale
        const val VIEW_TYPE_EDITORIAL = 1 //Editorial
        const val VIEW_TYPE_IMAGE_COUNT_1 =
            2  // Viewpager - image width is equals to 320 & count is greater than 1
        const val VIEW_TYPE_IMAGE = 3 //Else
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            VIEW_TYPE_SALE -> {
                return SaleViewHolder(
                    HomeSaleItemBinding.inflate(LayoutInflater.from(mActivity), parent, false)
                )
            }
            VIEW_TYPE_EDITORIAL -> {
                return EditorialViewHolder(
                    HomeEditorialItemBinding.inflate(LayoutInflater.from(mActivity), parent, false)
                )
            }
            VIEW_TYPE_IMAGE_COUNT_1 -> {
                return ImageCount1ViewHolder(
                    HomeBannerItemBinding.inflate(LayoutInflater.from(mActivity), parent, false)
                )
            }
            VIEW_TYPE_IMAGE -> {
                return ImageViewHolder(
                    HomeViewItemBinding.inflate(LayoutInflater.from(mActivity), parent, false)
                )
            }
            else -> return ImageViewHolder(
                HomeViewItemBinding.inflate(LayoutInflater.from(mActivity), parent, false)
            )
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SaleViewHolder -> {
                holder.onBind()
            }
            is EditorialViewHolder -> {
                holder.onBind(position)
            }
            is ImageCount1ViewHolder -> {
                holder.onBind(position)
            }
            else -> {
                val viewHolder = holder as ImageViewHolder
                viewHolder.onBind(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return collectionData.count()
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 && collectionData[0].saleModel?.sale_start_date != null) {
            VIEW_TYPE_SALE
        } else if ((collectionData[position].only_editorial != null) && (collectionData[position].only_editorial
                ?: "0") == "1"
        ) {
            VIEW_TYPE_EDITORIAL
        } else if (collectionData[position].image_width!! == 320 && collectionData[position].collection_list != null && collectionData[position].collection_list?.size!! > 1) {
            VIEW_TYPE_IMAGE_COUNT_1
        } else {
            VIEW_TYPE_IMAGE
        }


    }

    //Sale
    private inner class SaleViewHolder(val homeSaleItemBinding: HomeSaleItemBinding) :
        RecyclerView.ViewHolder(homeSaleItemBinding.root) {
        fun onBind() {
            val saleModel = collectionData[0].saleModel
            if (saleModel != null) {

                val displayMetrics = DisplayMetrics()
                mActivity.windowManager.defaultDisplay.getMetrics(displayMetrics)
                val width: Double = displayMetrics.widthPixels.toDouble()
                val density: Double = (width / 320)
                var imageHeight = saleModel.sale_image_height.toDouble()

                if (imageHeight > 100)
                    imageHeight *= density
                else imageHeight = 100 * density

                val imageHeightSale = imageHeight.toInt()

                var strStartDate = ""
                var strEndDate = ""


                if (saleModel.sale_start_date.isNotEmpty()) {
                    strStartDate = Utils.getFormattedDateTime(
                        "yyyy-MM-dd HH:mm:ss",
                        "dd.MM.yyyy, HH:mm:ss",
                        saleModel.sale_start_date
                    )
                }
                if (saleModel.sale_end_date.isNotEmpty()) {
                    strEndDate = Utils.getFormattedDateTime(
                        "yyyy-MM-dd HH:mm:ss",
                        "dd.MM.yyyy, HH:mm:ss",
                        saleModel.sale_end_date
                    )
                    saleTimer(strEndDate, imageHeightSale, homeSaleItemBinding)
                } else {
                    homeSaleItemBinding.cvSale!!.visibility = View.GONE
                }

                if (saleModel.sale_image != "") {
                    Utils.loadImagesUsingCoil(
                        mActivity,
                        saleModel.sale_image,
                        homeSaleItemBinding.imgSale
                    )
                    //Glide.with(mActivity).load(saleModel.sale_image).into(itemView.imgSale!!)
                } else {
                    homeSaleItemBinding.imgSale.visibility = View.GONE
                }

                homeSaleItemBinding.cvSale?.setOnClickListener {

                    val arrList = ArrayList<String>()
                    val arrListCollection = java.util.ArrayList<CollectionListItemModel>() //Dummy
                    val arrListPattern = java.util.ArrayList<PatternsProduct>()  /// Dummy
                    val arrListTimeline = ArrayList<TimeLineModel>() //Dummy
                    val model = CollectionListItemModel(
                        "",
                        "",
                        "",
                        saleModel.sale_category_id,
                        saleModel.sale_title,
                        "",
                        "",
                        saleModel.sale_path,
                        saleModel.sale_category_path,
                        "0",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        saleModel.sale_title,
                        saleModel.sale_category_id,
                        saleModel.sale_path,
                        saleModel.sale_category_path,
                        0,
                        0,
                        "",
                        arrListPattern as ArrayList<PatternsProduct?>,
                        0,
                        "",
                        arrListCollection as ArrayList<CollectionListItemModel?>,
                        0,
                        arrList,
                        0,
                        0
                    )
//                    arrListTimeline as ArrayList<TimeLineModel?>
                    val bundle = Bundle()
                    bundle.putInt(Constants.BANNER_HEIGHT, 0)
                    bundle.putSerializable(Constants.MODEL, model)
                    val productListingFragment = ProductListingFragment()
                    productListingFragment.arguments = bundle
//                    rivaLookBookActivity?.replaceFragment(productListingFragment)
                    rivaLookBookActivity?.navController?.navigate(R.id.navigation_product_listing,bundle)

                }

            }
        }

    }

    /////Sale Timer
    private fun saleTimer(
        strEndDate: String,
        image_Height: Int,
        homeSaleItemBinding: HomeSaleItemBinding
    ) {
        if (strEndDate != null && strEndDate.isNotEmpty()) {
            try {

                val formatter = SimpleDateFormat("dd.MM.yyyy, HH:mm:ss", Locale.US)
                formatter.isLenient = false
                formatter.timeZone = TimeZone.getTimeZone("gmt")

                var endmilliseconds = 0L

                var endDate: Date? = null
                var startTime = System.currentTimeMillis()


                try {
                    endDate = formatter.parse(strEndDate)
                    endmilliseconds = endDate.time

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                mCountDownTimer = object : CountDownTimer(endmilliseconds, 1000) {
                    override fun onTick(millisUntilFinished: Long) {

                        startTime -= 1
                        val serverUptimeSeconds = (millisUntilFinished - startTime) / 1000

                        val daysLeft = String.format(
                            Locale.ENGLISH,
                            "%02d",
                            (serverUptimeSeconds / 86400).toInt()
                        )
                        val hoursLeft = String.format(
                            Locale.ENGLISH,
                            "%02d",
                            (serverUptimeSeconds % 86400 / 3600).toInt()
                        )
                        val minutesLeft = String.format(
                            Locale.ENGLISH,
                            "%02d",
                            (serverUptimeSeconds % 86400 % 3600 / 60).toInt()
                        )
                        val secondsLeft = String.format(
                            Locale.ENGLISH,
                            "%02d",
                            (serverUptimeSeconds % 86400 % 3600 % 60).toInt()
                        )

                        homeSaleItemBinding.txtDays!!.text = daysLeft
                        homeSaleItemBinding.txtHours!!.text = hoursLeft
                        homeSaleItemBinding.txtMins!!.text = minutesLeft
                        homeSaleItemBinding.txtSeconds!!.text = secondsLeft

                        if (daysLeft.toInt() <= 0 && hoursLeft.toInt() <= 0 && minutesLeft.toInt() <= 0 && secondsLeft.toInt() <= 0) {
                            mCountDownTimer!!.cancel()
                            homeSaleItemBinding.cvSale!!.visibility = View.GONE
                        }
                    }

                    override fun onFinish() {
                    }
                }.start()

                var startTimeDummy = System.currentTimeMillis()
                startTimeDummy -= 1
                val serverUptimeSeconds = (endmilliseconds - startTimeDummy) / 1000

                val daysLeft =
                    String.format(Locale.ENGLISH, "%02d", (serverUptimeSeconds / 86400).toInt())
                val hoursLeft = String.format(
                    Locale.ENGLISH,
                    "%02d",
                    (serverUptimeSeconds % 86400 / 3600).toInt()
                )
                val minutesLeft = String.format(
                    Locale.ENGLISH,
                    "%02d",
                    (serverUptimeSeconds % 86400 % 3600 / 60).toInt()
                )
                val secondsLeft = String.format(
                    Locale.ENGLISH,
                    "%02d",
                    (serverUptimeSeconds % 86400 % 3600 % 60).toInt()
                )
                if (daysLeft.toInt() <= 0 && hoursLeft.toInt() <= 0 && minutesLeft.toInt() <= 0 && secondsLeft.toInt() <= 0) {
                    homeSaleItemBinding.imgSale!!.visibility = View.GONE
                    homeSaleItemBinding.cvSale!!.visibility = View.GONE

                } else {
                    homeSaleItemBinding.imgSale!!.visibility = View.VISIBLE
                    homeSaleItemBinding.cvSale!!.visibility = View.VISIBLE
                    val params = RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        image_Height
                    )
                    homeSaleItemBinding.imgSale!!.layoutParams = params
//                    homeSaleItemBinding.relTimer!!.layoutParams = params
                }

            } catch (e: Exception) {
                e.printStackTrace()
                homeSaleItemBinding.cvSale.visibility = View.GONE
            }
        } else {
            homeSaleItemBinding.cvSale.visibility = View.GONE
        }
    }


    //Editorial
    private inner class EditorialViewHolder(val homeEditorialItemBinding: HomeEditorialItemBinding) :
        RecyclerView.ViewHolder(homeEditorialItemBinding.root) {
        fun onBind(position: Int) {
            val imageHeight: Double = (collectionData[position].image_height!! * (density))
            val imageMargin: Double = (collectionData[position].image_margin!! * (density))
            val topMargin: Double = (collectionData[position].margin_top!! * (density))
            val bottomMargin: Double = (collectionData[position].margin_bottom!! * (density))
            val collectionTitle: String =
                collectionData[position].hide_collection_title.toString()!!
            val collectionSubTitle: String =
                collectionData[position].hide_collection_sub_title.toString()!!


            if (collectionData[position].hide_title!!.equals("1")) {
                homeEditorialItemBinding.txtEditTitle.visibility = View.GONE

            } else if (collectionData[position].hide_title!!.equals("0")) {

                val lpText = RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                lpText.addRule(RelativeLayout.CENTER_HORIZONTAL)
                lpText.setMargins(
                    (10 * density).toInt(),
                    topMargin.toInt(),
                    (10 * density).toInt(),
                    0
                )

                homeEditorialItemBinding.txtEditTitle.visibility = View.VISIBLE
                homeEditorialItemBinding.txtEditTitle.layoutParams = lpText
                homeEditorialItemBinding.txtEditTitle.text = collectionData[position].title
            }

            //If editorials collection size is single
            if (collectionData[position].collection_list!!.size == 1) {

                homeEditorialItemBinding.imgEditorial.visibility = View.VISIBLE
                homeEditorialItemBinding.rcyEditorials.visibility = View.GONE

                /////Editorial Click
                ////////////////////////////////////////////////////////////////////////////////
                homeEditorialItemBinding.imgEditorial.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("id", collectionData[position].collection_list!![0]?.type_id)
                    bundle.putString("name", collectionData[position].collection_list!![0]?.title)
                    rivaLookBookActivity?.navController?.navigate(R.id.navigation_editorial,bundle)
                }


            } else {

                //For Multiple collection size

                homeEditorialItemBinding.imgEditorial.visibility = View.GONE
                homeEditorialItemBinding.rcyEditorials.visibility = View.VISIBLE

                val layoutManager = LinearLayoutManager(mActivity)

                val arListEdit = ArrayList<HomeEditModel>()
                val model =
                    HomeEditModel(
                        collectionData[position].image,
                        collectionData[position].collection_list!!
                    )
                arListEdit.add(model)

//                val mAdapter = HomeEditAdapter(
//                    mActivity,
//                    arListEdit,
//                    imageHeight.toInt(),
//                    topMargin.toInt(),
//                    bottomMargin.toInt(),
//                    collectionData[position].image_height!!.toInt(),
//                    collectionData[position]
//                )
//                mAdapter.setExpandCollapseListener(object :
//                    ExpandableRecyclerAdapter.ExpandCollapseListener {
//
//                    @UiThread
//                    override fun onParentExpanded(parentPosition: Int) {
//
//                        homeEditorialItemBinding.rcyEditorials.postDelayed({
//                            homeEditorialItemBinding.rcyEditorials.parent.requestChildFocus(
//                                homeEditorialItemBinding.rcyEditorials,
//                                homeEditorialItemBinding.rcyEditorials
//                            )
//
//                        }, 100)
//                    }
//
//                    @UiThread
//                    override fun onParentCollapsed(parentPosition: Int) {
//                    }
//                })
//
//                homeEditorialItemBinding.rcyEditorials.isNestedScrollingEnabled = false
//                homeEditorialItemBinding.rcyEditorials.adapter = mAdapter
//                homeEditorialItemBinding.rcyEditorials.layoutManager = layoutManager
            }
        }

    }

    //Banner Images AutoScroll Viewpager
    private inner class ImageCount1ViewHolder(val homeBannerItemBinding: HomeBannerItemBinding) :
        RecyclerView.ViewHolder(homeBannerItemBinding.root) {
        fun onBind(position: Int) {

            var imageHeight: Double = (collectionData[position].image_height!! * (density))
            val topMargin: Double = (collectionData[position].margin_top!! * (density))
            val bottomMargin: Double = (collectionData[position].margin_bottom!! * (density))
            val collectionTitle =
                collectionData[position].hide_collection_title!!
            val collectionSubTitle =
                collectionData[position].hide_collection_sub_title!!


//            //apply margin top/bottom to main layout
//            val layoutParam = RelativeLayout.LayoutParams(
//                RelativeLayout.LayoutParams.MATCH_PARENT,
//                RelativeLayout.LayoutParams.WRAP_CONTENT
//            )
//            layoutParam.setMargins(0, topMargin.toInt(), 0, bottomMargin.toInt())
//            homeBannerItemBinding.layoutParams = layoutParam

            if (collectionData[position].hide_title?.equals("1") == true) {
                homeBannerItemBinding.txtBannerTitle.visibility = View.GONE

            } else if (collectionData[position].hide_title?.equals("0") == true) {

                val lpText = RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                lpText.addRule(RelativeLayout.CENTER_HORIZONTAL)
                //small_param1.setMargins(image_Margin.toInt(), top_margin.toInt(), 0, bottom_margin.toInt())
                homeBannerItemBinding.txtBannerTitle.visibility = View.VISIBLE

                homeBannerItemBinding.txtBannerTitle.layoutParams = lpText
                homeBannerItemBinding.txtBannerTitle.text = collectionData[position].title
                homeBannerItemBinding.txtBannerTitle.textSize =
                    Utils.dp2px(
                        mActivity.resources.getDimension(R.dimen.DescriptionTextSize),
                        mActivity
                    )
                        .toFloat()


                if (topMargin > 0)
                    homeBannerItemBinding.txtBannerTitle.setPadding(
                        mActivity.resources.getDimension(R.dimen.seven_dp).toInt(),
                        0,
                        mActivity.resources.getDimension(R.dimen.seven_dp).toInt(),
                        0
                    )
                else homeBannerItemBinding.txtBannerTitle.setPadding(
                    mActivity.resources.getDimension(R.dimen.seven_dp).toInt(),
                    mActivity.resources.getDimension(R.dimen.five_dp).toInt(),
                    mActivity.resources.getDimension(R.dimen.seven_dp).toInt(),
                    mActivity.resources.getDimension(R.dimen.seven_dp).toInt()
                )
            }

            homeBannerItemBinding.Homepager.adapter = HomeImageAdapter(
                mActivity,
                imageHeight.toInt(),
                0,
                0,
                collectionTitle,
                collectionSubTitle,
                (collectionData[position]!!.collection_list as List<CollectionListItemModel>?)!!,
                true, rivaLookBookActivity
            )
            //v3.viewPagerIndicator.setViewPager(v3.Homepager)
            homeBannerItemBinding.viewPagerIndicator.noOfPages =
                collectionData[position].collection_list?.size as Int
            homeBannerItemBinding.viewPagerIndicator.visibleDotCounts = 7
            homeBannerItemBinding.viewPagerIndicator.onPageChange(homeBannerItemBinding.Homepager.currentItem)
            homeBannerItemBinding.Homepager.addOnPageChangeListener(object :
                ViewPager.OnPageChangeListener {

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

                    if (homeBannerItemBinding.Homepager.currentItem >= 0)
                        homeBannerItemBinding.viewPagerIndicator.onPageChange(homeBannerItemBinding.Homepager.currentItem)
                    else homeBannerItemBinding.viewPagerIndicator.onPageChange(0)
                }
            })

//            homeBannerItemBinding.Homepager.autoScroll(5000)
            homeBannerItemBinding.Homepager.startAutoScroll()
            homeBannerItemBinding.Homepager.startAutoScroll(6000)
            homeBannerItemBinding.Homepager.isCycle = true
            homeBannerItemBinding.Homepager.isStopWhenTouch = false

            val titleHeight = mActivity.resources.getDimension(R.dimen.sixty_dp)
            if (collectionTitle == 0) {
                imageHeight += titleHeight
            }

            val pagerSize = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                imageHeight.toInt()
            )
            pagerSize.addRule(RelativeLayout.BELOW, R.id.txtBannerTitle)
            homeBannerItemBinding.Homepager.layoutParams = pagerSize
            homeBannerItemBinding.Homepager.currentItem = 0

            if (collectionData[position].collection_list!!.size < 2) {
                homeBannerItemBinding.viewPagerIndicator.visibility = View.GONE
            }
        }

    }

    //Banner Images Recyclerview
    private inner class ImageViewHolder(val homeViewItemBinding: HomeViewItemBinding) :
        RecyclerView.ViewHolder(homeViewItemBinding.root) {
        fun onBind(position: Int) {

            val imageHeight: Double = (collectionData[position].image_height!! * (density))
            var imageWidth: Double = (collectionData[position].image_width!! * (density))
            val imageMargin: Double = (collectionData[position].image_margin!! * (density))
            val topMargin: Double = (collectionData[position].margin_top!! * (density))
            val bottomMargin: Double = (collectionData[position].margin_bottom!! * (density))
            val collectionTitle =
                collectionData[position].hide_collection_title!!
            val collectionSubTitle =
                collectionData[position].hide_collection_sub_title!!
            val isTimeLine: String = collectionData[position].is_timeline ?: "0"
            val showNewBadge = "0"


            homeViewItemBinding.rvHomeviewList.layoutManager =
                LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)

            //apply margin top/bottom to main layout
            val layoutParam = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParam.setMargins(0, topMargin.toInt(), 0, bottomMargin.toInt())
            homeViewItemBinding.root.layoutParams = layoutParam
            if (collectionData[position].hide_title?.equals("1") == true) {

                homeViewItemBinding.txtCollectionTitle.visibility = View.GONE

            } else if (collectionData[position].hide_title?.equals("0") == true) {

//                val lpText = RelativeLayout.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    mActivity.resources.getDimension(R.dimen.thirty_dp).toInt()
//                )
//                lpText.addRule(RelativeLayout.CENTER_HORIZONTAL)
                homeViewItemBinding.txtCollectionTitle.visibility = View.VISIBLE
//                homeViewItemBinding.txtCollectionTitle.gravity = Gravity.CENTER_VERTICAL

//                homeViewItemBinding.txtCollectionTitle.layoutParams = lpText
                homeViewItemBinding.txtCollectionTitle.text = collectionData[position].title
//                homeViewItemBinding.txtCollectionTitle.textSize =
//                    Utils.dp2px(
//                        mActivity.resources.getDimension(R.dimen.DescriptionTextSize),
//                        mActivity
//                    )
//                        .toFloat()

            }

            ///Timeline function for new badge on timeleine
            /// Using database to store ids and comparing then with apis id
            ///////////////////////////////////////////////////////////////////////////////////
            val arrListBadge = ArrayList<Int>()
            var isFound = false

            if (isTimeLine == "1") {

                for (t in 0 until arrListTimeLineGroupIds.size) {

                    if (collectionData[position].id == arrListTimeLineGroupIds[t]) {
                        isFound = true
                        break
                    }
                }

                for (p in 0 until collectionData[position].collection_list!!.size) {

                    arrListBadge.add(0)

                    if (isFound) {
                        for (s in 0 until arrListTimeLineIds.size) {

                            if (collectionData[position].collection_list!![p].id!!.toInt() == arrListTimeLineIds[s]) {
                                arrListBadge[p] = 1
                                break
                            } else arrListBadge[p] = 0

                        }
                    } else arrListBadge[p] = 0
                }

            }


            val collectionArrList = collectionData[position].collection_list!!
            if (collectionData[position].should_reverse == 1) {
                collectionArrList.reverse()
            }
            /////////////////////////////////////////////////////////////////////////////////////
            val dataAdapter = HomeDataAdapter(selectedCurrency,
                collectionArrList,
                mActivity,
                null,
                imageHeight.toInt(),
                imageWidth.toInt(),
                imageMargin.toInt(),
                0,
                0,
                collectionTitle,
                collectionSubTitle,
                isTimeLine,
                collectionData[position]!!.id!!,
                arrListBadge,
                true,
                rivaLookBookActivity

            )
            homeViewItemBinding.rvHomeviewList.adapter = dataAdapter

            ///If timeline saving adapter for new badge update
            if (isTimeLine == "1") {
                arrListTime.clear()
//                appController.arrListTmLineBadge.clear()
//                appController.arrListTmLineBadge = arrListBadge /// Badge Keeping
                arrListTime.add(dataAdapter)
            }

            ////Scrolling handled to avoid conflict scroll
            homeViewItemBinding.rvHomeviewList.setOnScrollListener(object :
                RecyclerView.OnScrollListener() {

            })

            homeViewItemBinding.rvHomeviewList.setOnTouchListener { _, _ -> //enableDisableSwipeRefresh(false)
                false
            }
        }

    }

    fun ViewPager.autoScroll(interval: Long) {

        val handler = Handler()
        var scrollPosition = 0

        val runnable = object : Runnable {

            override fun run() {

                /**
                 * Calculate "scroll position" with
                 * adapter pages count and current
                 * value of scrollPosition.
                 */
                val count = adapter?.count ?: 0
                setCurrentItem(scrollPosition++ % count, true)

                handler.postDelayed(this, interval)
            }
        }

        addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                // Updating "scroll position" when user scrolls manually
                scrollPosition = position + 1
            }

            override fun onPageScrollStateChanged(state: Int) {
                // Not necessary
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                // Not necessary
            }
        })

        handler.post(runnable)
    }

}