package com.armada.storeapp.ui.home.riva.riva_look_book.home

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.armada.riva.HOME.VideoActivity
import com.armada.storeapp.R
import com.armada.storeapp.data.Resource
import com.armada.storeapp.data.model.response.CollectionListItemModel
import com.armada.storeapp.data.model.response.EditorialResponse
import com.armada.storeapp.data.model.response.PatternsProduct
import com.armada.storeapp.data.model.response.TimeLineModel
import com.armada.storeapp.databinding.FragmentEditorialsBinding
import com.armada.storeapp.ui.home.riva.riva_look_book.RivaLookBookActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.home.Algolia.ExpandableRecyler.ExpandableRecyclerAdapter
import com.armada.storeapp.ui.home.riva.riva_look_book.home.adapter.EditorialAdapter
import com.armada.storeapp.ui.home.riva.riva_look_book.home.model.EditorialModel
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.OmniProductDetailsActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.ProductDetailsActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing.ProductListingFragment
import com.armada.storeapp.ui.utils.Constants
import com.armada.storeapp.ui.utils.SharedpreferenceHandler
import com.armada.storeapp.ui.utils.Utils

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class EditorialFragment : Fragment() {
    lateinit var sharedpreferenceHandler: SharedpreferenceHandler
    lateinit var fragmentEdiorialBinding: FragmentEditorialsBinding
    private var param1: String? = null
    private var param2: String? = null
    private var isFromRefresh: Boolean? = false
    private var strCatId = ""
    private var strName = ""
    lateinit var homeViewModel: HomeViewModel
    private var rivaLookBookActivity: RivaLookBookActivity? = null
    private var selectedCurrency="USD"
    private var selectedLanguage="en"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentEdiorialBinding = FragmentEditorialsBinding.inflate(layoutInflater)
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        rivaLookBookActivity = activity as RivaLookBookActivity
        sharedpreferenceHandler= SharedpreferenceHandler(rivaLookBookActivity!!)
        selectedLanguage=sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_SELECTED_COUNTRY,"en")!!
        selectedCurrency=sharedpreferenceHandler.getData(SharedpreferenceHandler.RIVA_SELECTED_CURRENCY,"USD")!!
        return fragmentEdiorialBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments?.getString("id") != null)
            strCatId = arguments?.getString("id").toString()

        if (arguments?.getString("name") != null)
            strName = arguments?.getString("name").toString()

        if (Utils.hasInternetConnection(requireContext()))
            getEditorials(strCatId)
    }

    fun getHeader(): String {
        //println("Name return :: "+strName)
        return strName
    }

    fun getEditorials(categoryId: String) {
        homeViewModel.getEditorials(selectedLanguage,categoryId)
        homeViewModel.responseEditorial.observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    rivaLookBookActivity?.dismissProgress()
                    handleResponse(it.data!!)
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


    private fun handleResponse(editorials: EditorialResponse) {

        val arrayLstEditorials = ArrayList<EditorialModel>()

        val displaymetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displaymetrics)
        val width: Double = displaymetrics.widthPixels.toDouble()
        val density: Double = (width / 320)

        editorials.data?.forEach {
            val model = it
            val image_Height: Double = (model.image_height!! * (density))
            val arrListProducs = it.products
            val EditorialModel = EditorialModel(
                model.id!!,
                image_Height.toInt(),
                model.image!!,
                model.media_file!!,
                model.media_type!!,
                model.media_thumbnail!!,
                model.type!!,
                model.category_id!!,
                model.name!!,
                model.category_path!!,
                model.category_level!!,
                model.product!!,
                arrListProducs!!
            )
            arrayLstEditorials.add(EditorialModel)
        }

        val mAdapter = EditorialAdapter(
            rivaLookBookActivity,
            arrayLstEditorials,
            selectedCurrency
        )
        mAdapter.setExpandCollapseListener(object :
            ExpandableRecyclerAdapter.ExpandCollapseListener {
            @UiThread
            override fun onParentExpanded(parentPosition: Int) {
                val catModel = arrayLstEditorials.get(parentPosition)
                if (catModel.products!!.size == 0) {
                    if (catModel.type.equals(
                            "C",
                            ignoreCase = true
                        ) || catModel.media_type.equals("I", false)
                    ) {

                        val arrList = ArrayList<String>()
                        val arrListCollection = ArrayList<CollectionListItemModel>() //Dummy
                        val arListPattrn = ArrayList<PatternsProduct>()  /// Dummy
                        val arrListTimeline = ArrayList<TimeLineModel>() //Dummy
                        val model = CollectionListItemModel(
                            "",
                            "",
                            "",
                            catModel.id,
                            catModel.name,
                            catModel.type,
                            catModel.category_id,
                            catModel.category_path,
                            catModel.category_level,
                            "0",
                            "",
                            catModel.media_file,
                            catModel.media_type,
                            catModel.media_thumbnail,
                            "",
                            "",
                            catModel.name,
                            0,
                            "",
                            "",
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
//                        arrListTimeline as ArrayList<TimeLineModel?>,
                        val bundle = Bundle()
                        bundle.putSerializable(Constants.MODEL, model)
                        bundle.putInt(Constants.BANNER_HEIGHT, catModel.image_height!!)
                        val productListingFragment= ProductListingFragment()
                        productListingFragment.arguments=bundle
                        rivaLookBookActivity?.navController?.navigate(R.id.navigation_product_listing,bundle)
//                        rivaLookBookActivity?.replaceFragment(productListingFragment)

                    } else if (catModel.type.equals("P", ignoreCase = true)) {

                        val i = Intent(rivaLookBookActivity, OmniProductDetailsActivity::class.java)
                        i.putExtra("id", catModel.product.product_id.toString())
                        i.putExtra("cat_id", "-1")
                        i.putExtra("name", "")
                        startActivity(i)

                    } else if (catModel.media_type.equals("V", ignoreCase = true)) {

                        val i = Intent(rivaLookBookActivity, VideoActivity::class.java)
                        i.putExtra("video_path", catModel.media_file)
                        i.putExtra("name", "")
                        startActivity(i)

                    }
                }

            }

            @UiThread
            override fun onParentCollapsed(parentPosition: Int) {
                val collapsedCat = arrayLstEditorials.get(parentPosition)
            }
        })

        fragmentEdiorialBinding?.rcyCategories.setAdapter(mAdapter)
        fragmentEdiorialBinding?.rcyCategories.setLayoutManager(
            androidx.recyclerview.widget.LinearLayoutManager(
                requireContext()
            )
        )

        if (!isFromRefresh!!) {
            rivaLookBookActivity?.dismissProgress()
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditorialFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditorialFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}