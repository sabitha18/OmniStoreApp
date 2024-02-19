package com.armada.storeapp.ui.home.riva.riva_look_book.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.response.ParentCategoryResponse
import com.armada.storeapp.databinding.FragmentCategoryBinding
import com.armada.storeapp.ui.home.riva.riva_look_book.RivaLookBookActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.categories.adapter.CategoryStatePagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryFragment : Fragment() {

    private var fragmentCategoryBinding: FragmentCategoryBinding? = null
    lateinit var categoryViewModel: CategoryViewModel
    private var rivaLookBookActivity: RivaLookBookActivity? = null
    var parentCategoryList = ArrayList<ParentCategoryResponse.Data>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentCategoryBinding =
            FragmentCategoryBinding.inflate(inflater, container, false)
        categoryViewModel =
            ViewModelProvider(this).get(CategoryViewModel::class.java)
        rivaLookBookActivity = activity as RivaLookBookActivity
        rivaLookBookActivity?.hideLogo()
        initData()
        return fragmentCategoryBinding?.root
    }

    private fun initData() {
        getParentCategories()
    }

    override fun onResume() {
        super.onResume()
        rivaLookBookActivity?.hideLogo()
    }

    fun getParentCategories() {
        categoryViewModel.getParentCategories()
        categoryViewModel.responeParentCategory.observe(requireActivity()) {
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

    private fun setUpViewPager() {
        parentCategoryList.removeIf {
            !(it.name_en?.equals("WOMAN")!! ||
                    it.name_en?.equals("KIDS")!! ||
                    it.name_en?.equals("BEAUTY")!!)
        }
        fragmentCategoryBinding?.pager?.adapter =
            CategoryStatePagerAdapter(rivaLookBookActivity!!, parentCategoryList)
        TabLayoutMediator(
            fragmentCategoryBinding?.tabLayout!!,
            fragmentCategoryBinding?.pager!!
        ) { tab, position ->
            tab.text = parentCategoryList.get(position).name_en
        }.attach()
    }


}