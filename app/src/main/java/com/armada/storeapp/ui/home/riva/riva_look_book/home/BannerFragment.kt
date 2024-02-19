package com.armada.storeapp.ui.home.riva.riva_look_book.home

import android.os.Bundle
import android.os.CountDownTimer
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.response.CollectionGroupsItemModel
import com.armada.storeapp.data.model.response.HomeDataModel
import com.armada.storeapp.databinding.FragmentBannerBinding
import com.armada.storeapp.ui.home.riva.riva_look_book.RivaLookBookActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.home.adapter.HomeBannerRecyclerviewAdapter
import com.armada.storeapp.ui.home.riva.riva_look_book.home.adapter.HomeDataAdapter
import com.armada.storeapp.ui.utils.Constants
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import com.armada.storeapp.ui.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BannerFragment : Fragment() {

    private var fragmentBannerBinding: FragmentBannerBinding? = null
    lateinit var homeViewModel: HomeViewModel
    private var rivaLookBookActivity: RivaLookBookActivity? = null
    var homePageDataModel: HomeDataModel? = null
    var parentCategoryId: String = ""
    lateinit var sharedpreferenceHandler: SharedpreferenceHandler
    private var selectedLanguage="en"
    private var selectedCurrency="USD"

    ////////Timeline
    var arrListTimeLineGroupIds = java.util.ArrayList<Int>()
    var arrListTimeLineIds = java.util.ArrayList<Int>()
    private var arrListTime = ArrayList<HomeDataAdapter>()
    private var mCountDownTimer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentBannerBinding =
            FragmentBannerBinding.inflate(inflater, container, false)
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        rivaLookBookActivity = activity as RivaLookBookActivity
        sharedpreferenceHandler= SharedpreferenceHandler(rivaLookBookActivity!!)
        selectedLanguage=sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_SELECTED_COUNTRY,"en")!!
        selectedCurrency=sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_SELECTED_CURRENCY,"USD")!!
        initData()
        setClickListeners()
        return fragmentBannerBinding?.root
    }

    private fun setClickListeners() {
        fragmentBannerBinding?.swipeRefreshLayout?.setOnRefreshListener {
            fragmentBannerBinding?.swipeRefreshLayout?.isRefreshing = true
            fragmentBannerBinding?.swipeRefreshLayout?.postDelayed(Runnable {

                //Load Home page data
                if (Utils.hasInternetConnection(requireContext())) {
                    getBannerCollections(parentCategoryId)
                } else {
                    fragmentBannerBinding?.swipeRefreshLayout?.isRefreshing = false
                }
            }, 1000)
        }

    }

    private fun initData() {
        arguments?.let {
            if (arguments?.containsKey(Constants.PARENT_CATEGORY_ID) == true) {
                parentCategoryId = arguments?.getInt(Constants.PARENT_CATEGORY_ID)?.toString()!!
            }
        }
        if (parentCategoryId?.isNotEmpty())
            getBannerCollections(parentCategoryId)
    }

    fun getBannerCollections(parentCategoryId: String) {
        homeViewModel.getBannerCollections(selectedLanguage,parentCategoryId, "")
        homeViewModel.responseBannerCollection.observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {

                    fragmentBannerBinding?.swipeRefreshLayout?.isRefreshing = false
                    rivaLookBookActivity?.dismissProgress()
                    homePageDataModel = it.data
                    setData()
                }

                is Resource.Error -> {
                    fragmentBannerBinding?.swipeRefreshLayout?.isRefreshing = false
                    rivaLookBookActivity?.dismissProgress()
                }
                is Resource.Loading -> {
                    rivaLookBookActivity?.showProgress()
                }
            }
        }
    }

    private fun setData() {
        homePageDataModel?.let {
            var collectionData = it?.data?.collectionGroups
            val displayMetrics = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            val width: Double = displayMetrics.widthPixels.toDouble()
            //  Log.d("Width",width.toString())
            val density: Double = (width / 320)

            val saleModel = it.data?.sale!!
            if (it.data.popular_banners != null)
                Utils.tempSearchModel?.popular_banners = it.data.popular_banners
            if (it.data.popular_searches != null)
                Utils.tempSearchModel?.popular_searches = it.data.popular_searches
            if (it.data.popular_top_scroll != null)
                Utils?.tempSearchModel?.popular_top_scroll = it.data.popular_top_scroll
            ///ArrlistAdaper
            arrListTime = ArrayList()

            if (saleModel != null && saleModel.sale_start_date.isNotEmpty() && saleModel.sale_end_date.isNotEmpty()) {
                val model = CollectionGroupsItemModel(
                    only_editorial = "",
                    image = "",
                    is_timeline = "",
                    saleModel = saleModel
                )
                collectionData?.add(0, model)
            }

            val adapterParent = HomeBannerRecyclerviewAdapter(selectedCurrency,
                (collectionData as ArrayList<CollectionGroupsItemModel>?)!!,
                requireActivity()!!,
                density,
                arrListTimeLineGroupIds,
                arrListTimeLineIds,
                arrListTime,
                mCountDownTimer, rivaLookBookActivity!!
            )
            fragmentBannerBinding?.recyclerViewBanners?.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            fragmentBannerBinding?.recyclerViewBanners?.adapter = adapterParent

            try {
                Utils.emptyCartPage=homePageDataModel?.data?.empty_bag ?: HomeDataModel.EmptyPages()
                Utils.emptyWishlistPage=homePageDataModel?.data?.empty_wishlist ?: HomeDataModel.EmptyPages()
                Utils.empty_notification=homePageDataModel?.data?.empty_notification ?: HomeDataModel.EmptyPages()
                Utils.empty_address=homePageDataModel?.data?.empty_address ?: HomeDataModel.EmptyPages()
                Utils.empty_order=homePageDataModel?.data?.empty_order ?: HomeDataModel.EmptyPages()
                Utils.empty_store=homePageDataModel?.data?.empty_store ?: HomeDataModel.EmptyPages()
                Utils.empty_product=homePageDataModel?.data?.empty_product ?: HomeDataModel.EmptyPages()
                Utils.empty_promotion=homePageDataModel?.data?.empty_promotion ?: HomeDataModel.EmptyPages()
            }catch (exception:Exception){
                exception.printStackTrace()
            }
        }
    }
}