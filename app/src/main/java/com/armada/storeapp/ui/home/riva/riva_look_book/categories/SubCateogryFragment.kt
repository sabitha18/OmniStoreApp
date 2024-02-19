package com.armada.storeapp.ui.home.riva.riva_look_book.categories

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import coil.imageLoader
import com.armada.storeapp.R
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.response.SubCategoryResponse
import com.armada.storeapp.databinding.FragmentSubcategoryBinding
import com.armada.storeapp.ui.home.riva.riva_look_book.RivaLookBookActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.categories.adapter.CategoryParentAdapter
import com.armada.storeapp.ui.home.riva.riva_look_book.categories.adapter.ExpandRecyclerviewAdapter
import com.armada.storeapp.ui.home.riva.riva_look_book.categories.model.ChildData
import com.armada.storeapp.ui.home.riva.riva_look_book.categories.model.ParentData
import com.armada.storeapp.ui.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubCateogryFragment : Fragment() {

    private var fragmentSubCategoryBinding: FragmentSubcategoryBinding? = null
    lateinit var categoryViewModel: CategoryViewModel
    private var rivaLookBookActivity: RivaLookBookActivity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentSubCategoryBinding =
            FragmentSubcategoryBinding.inflate(inflater, container, false)
        categoryViewModel =
            ViewModelProvider(this).get(CategoryViewModel::class.java)
        rivaLookBookActivity = activity as RivaLookBookActivity
        return fragmentSubCategoryBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey("parent_category_id") }?.apply {
//            val textView: TextView = view.findViewById(android.R.id.text1)
//            textView.text = getInt(ARG_OBJECT).toString()
            getSubCategories(getInt("parent_category_id")?.toString())
        }
    }

    fun getSubCategories(parentCategoryId: String) {
        categoryViewModel.getSubCategories(parentCategoryId, "")
        categoryViewModel.responseSubCategory.observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    setData(it.data)
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
    }

    private fun setData(subCategoryResponse: SubCategoryResponse?) {
//        val listData = ArrayList<ParentData>()
//        for (item in subCategoryResponse?.data?.collectionGroups!!) {
//            item?.collection_list?.forEach {
//
//                val childList = ArrayList<ChildData>()
//                childList.add(ChildData(it!!))
//                val parentData = ParentData(it, childList!!)
//                listData.add(parentData)
//            }
//        }
        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val width: Double = displayMetrics.widthPixels.toDouble()
        val density: Double = (width / 320)
        val categoryParentAdapter = CategoryParentAdapter(subCategoryResponse?.data?.collectionGroups!!,rivaLookBookActivity!!,density)
//        CategoryParentAdapter?.onCategoryClick = { selectedCategory, hasChild ->
//            if (hasChild) {
//
//            } else {
//                val bundle = Bundle()
//                bundle.putString(
//                    Constants.SELECTED_CATEGORY_ID,
//                    selectedCategory.category_id?.toString()
//                )
//                rivaLookBookActivity?.navController?.navigate(
//                    R.id.navigation_product_listing,
//                    bundle
//                )
//            }
//
//        }

        fragmentSubCategoryBinding?.recyclerView?.layoutManager =
            LinearLayoutManager(requireContext())
        fragmentSubCategoryBinding?.recyclerView?.adapter =
            categoryParentAdapter

    }

//    private fun setData(subCategoryResponse: SubCategoryResponse?) {
//        val listData = ArrayList<ParentData>()
//        for (item in subCategoryResponse?.data?.collectionGroups!!) {
//            item?.collection_list?.forEach {
//
//                val childList = ArrayList<ChildData>()
//                childList.add(ChildData(it!!))
//                val parentData = ParentData(it, childList!!)
//                listData.add(parentData)
//            }
//        }
//        val expandRecyclerViewAdapter = ExpandRecyclerviewAdapter(requireContext(), listData)
//        expandRecyclerViewAdapter?.onCategoryClick = { selectedCategory, hasChild ->
//            if (hasChild) {
//
//            } else {
//                val bundle = Bundle()
//                bundle.putString(
//                    Constants.SELECTED_CATEGORY_ID,
//                    selectedCategory.category_id?.toString()
//                )
//                rivaLookBookActivity?.navController?.navigate(
//                    R.id.navigation_product_listing,
//                    bundle
//                )
//            }
//
//        }
//        fragmentSubCategoryBinding?.recyclerView?.layoutManager =
//            LinearLayoutManager(requireContext())
//        fragmentSubCategoryBinding?.recyclerView?.adapter =
//            expandRecyclerViewAdapter
//
//    }
}