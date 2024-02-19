package com.armada.storeapp.ui.home.riva.riva_look_book.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.response.HomeDataModel
import com.armada.storeapp.data.model.response.ParentCategoryResponse
import com.armada.storeapp.databinding.FragmentRivaHomeBinding
import com.armada.storeapp.ui.home.riva.riva_look_book.RivaLookBookActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.home.adapter.RivaHomeStatePagerAdapter
import com.armada.storeapp.ui.home.riva.riva_look_book.home.adapter.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RivaHomeFragment : Fragment() {

    private var homePageDataModel: HomeDataModel? = null
    private var fragmentRivaHomeBinding: FragmentRivaHomeBinding? = null
    lateinit var homeViewModel: HomeViewModel
    private var rivaLookBookActivity: RivaLookBookActivity? = null
    var parentCategoryList = ArrayList<ParentCategoryResponse.Data>()
    private var selectedParentCategory=0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentRivaHomeBinding =
            FragmentRivaHomeBinding.inflate(inflater, container, false)
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        rivaLookBookActivity = activity as RivaLookBookActivity
        initData()
        return fragmentRivaHomeBinding?.root
    }

    private fun initData() {
        getParentCategories()
    }

    fun getParentCategories() {
        homeViewModel.getParentCategories()
        homeViewModel.responeParentCategory.observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    rivaLookBookActivity?.dismissProgress()
                    parentCategoryList = it.data?.data!!
                    setUpViewPager()

                }

                is Resource.Error -> {
                    rivaLookBookActivity?.dismissProgress()
                }
                is Resource.Loading -> {
                    rivaLookBookActivity?.showProgress()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        rivaLookBookActivity?.dismissProgress()
        rivaLookBookActivity?.binding?.imageViewRivaLogo?.visibility = View.VISIBLE
    }

    private fun setUpViewPager() {
        parentCategoryList.removeIf {
            !(it.name_en?.equals("WOMAN")!! ||
                    it.name_en?.equals("KIDS")!! ||
                    it.name_en?.equals("BEAUTY")!!)
                    it.name_en?.equals("LIFESTYLE")
        }

        val adapter =
            ViewPagerAdapter(childFragmentManager, parentCategoryList)
        fragmentRivaHomeBinding?.mainViewPager?.adapter = adapter
        //viewPager.setSaveFromParentEnabled(false)
        fragmentRivaHomeBinding?.mainViewPager?.offscreenPageLimit = parentCategoryList.size
        fragmentRivaHomeBinding?.tabLayout?.setupWithViewPager(fragmentRivaHomeBinding?.mainViewPager)
        // tabLayout.isTabIndicatorFullWidth = false
        fragmentRivaHomeBinding?.mainViewPager?.setPagingEnabled(true)
//        fragmentRivaHomeBinding?.mainViewPager?.currentItem = AppController.instance.currentTab

        fragmentRivaHomeBinding?.mainViewPager?.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(fragmentRivaHomeBinding?.tabLayout))
        fragmentRivaHomeBinding?.tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
//                AppController.instance.currentTab = tab?.position as Int
//                EventBus.getDefault().post(tab.position)
                // Handle tab select
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Handle tab reselect
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Handle tab unselect
            }
        })
//        fragmentRivaHomeBinding?.mainViewPager?.adapter =
//            RivaHomeStatePagerAdapter(requireActivity(), parentCategoryList)
//        TabLayoutMediator(
//            fragmentRivaHomeBinding?.tabLayout!!,
//            fragmentRivaHomeBinding?.mainViewPager!!
//        ) { tab, position ->
//            tab.text = parentCategoryList.get(position).name_en
//        }.attach()
//
//        fragmentRivaHomeBinding?.tabLayout?.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
//            override fun onTabSelected(tab: TabLayout.Tab?) {
//                selectedParentCategory= parentCategoryList?.get(tab?.position!!).parent_category_id!!
//                rivaLookBookActivity?.setParentCategory(selectedParentCategory)
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab?) {
//
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab?) {
//
//            }
//
//        })
    }
}