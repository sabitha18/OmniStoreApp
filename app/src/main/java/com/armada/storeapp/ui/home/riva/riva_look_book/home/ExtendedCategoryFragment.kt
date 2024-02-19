package com.armada.storeapp.ui.home.riva.riva_look_book.home

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.armada.storeapp.R
import com.armada.storeapp.data.model.response.CollectionListItemModel
import com.armada.storeapp.databinding.FragmentExtendedCategoryBinding
import com.armada.storeapp.databinding.HomeViewImageBinding
import com.armada.storeapp.ui.home.riva.riva_look_book.RivaLookBookActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.home.Algolia.ExpandableRecyler.Model.CategoriesListMDataModel
import com.armada.storeapp.ui.home.riva.riva_look_book.home.Algolia.ExpandableRecyler.Model.MainCatModel
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing.ProductListingFragment
import com.armada.storeapp.ui.utils.Constants
import com.armada.storeapp.ui.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.home_banner_item.view.*

@AndroidEntryPoint
class ExtendedCategoryFragment : Fragment() {

    private var fragmentExtendedCategoryBinding: FragmentExtendedCategoryBinding? = null

    private var model: CollectionListItemModel? = null
    private var bannerHeight = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentExtendedCategoryBinding = FragmentExtendedCategoryBinding.inflate(inflater)
        init()
        return fragmentExtendedCategoryBinding?.root
    }

    ///
//    private fun initToolbar() {
//        val mToolbar = fragmentExtendedCategoryBinding?.toolbarActionbar as Toolbar?
//        setS(mToolbar)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        supportActionBar!!.setDisplayShowTitleEnabled(false)
//        val upArrow: Drawable = if (AppController.instance.isLangArebic) {
//            resources.getDrawable(R.drawable.ic_arrow_right)
//        } else {
//            resources.getDrawable(R.drawable.ic_arrow_left)
//        }
//        upArrow.setColorFilter(resources.getColor(R.color.black), PorterDuff.Mode.SRC_ATOP)
//        upArrow.setVisible(true, true)
//        supportActionBar!!.setHomeAsUpIndicator(upArrow)
//        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
//        txtHead.text = model!!.name
//    }

    private fun init() {
        fragmentExtendedCategoryBinding?.swipeRefreshLayout?.isEnabled = false
        arguments?.let {
            if (arguments?.containsKey(Constants.MODEL) == true)
                model = arguments?.getSerializable(Constants.MODEL) as CollectionListItemModel
            if (arguments?.containsKey(Constants.BANNER_HEIGHT) == true)
                bannerHeight = arguments?.getInt(Constants.BANNER_HEIGHT)!!
        }
        getCategories()
    }

    ///
    private fun getCategories() {

        val arrayLstMainCat = ArrayList<MainCatModel>()
        val arrListImages = ArrayList<String>()
        val array = resources.getStringArray(R.array.cat_images)

        for (i in 0 until array.size)
            arrListImages.add(array[i])

        try {

            var post = 0
            for (i in 0 until model?.subcategories?.size as Int) {

                val catModel = model?.subcategories?.get(i)
                if (catModel?.category_id.toString() != "765") {

                    if (post > 11) post = 0

                    val arrayLstLvl1Cat = ArrayList<CategoriesListMDataModel?>()
                    if (catModel?.has_subcategory == 1) {

                        for (j in 0 until catModel.subcategories?.size as Int) {

                            var sublevel = 0
                            when {
                                catModel.subcategories?.get(j)?.category_level?.startsWith("categories.level0") == true -> sublevel =
                                    0
                                catModel.subcategories?.get(j)?.category_level?.startsWith("categories.level1") == true -> sublevel =
                                    1
                                catModel.subcategories?.get(j)?.category_level?.startsWith("categories.level2") == true -> sublevel =
                                    2
                            }
                        }
                    }

                    val category = MainCatModel(
                        catModel?.category_id.toString(),
                        catModel?.name.toString(),
                        catModel?.category_path.toString(),
                        catModel?.category_level.toString(),
                        "",
                        if (arrayLstLvl1Cat.size > 0) true else false,
                        arrayLstLvl1Cat
                    )
                    arrayLstMainCat.add(category)
                    post++
                }
            }

            fragmentExtendedCategoryBinding?.rcyCategories?.isNestedScrollingEnabled = false

            fragmentExtendedCategoryBinding?.rcyCategories?.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            if (model?.banners?.size ?: 0 > 0) {

                val param = RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
//                param.below(fragmentExtendedCategoryBinding?.lnrMain!!)
                fragmentExtendedCategoryBinding?.rcyCategories?.layoutParams = param

                setCategoryView(model!!)

            } else {
                fragmentExtendedCategoryBinding?.lnrMain?.visibility = View.GONE
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    ///
    private fun setCategoryView(collectionData: CollectionListItemModel) {

        val displayMetrics: DisplayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val value = resources.displayMetrics.density;
        val width: Double = displayMetrics.widthPixels.toDouble()
        val density: Double = (width / 320)

        fragmentExtendedCategoryBinding?.lnrMain?.removeAllViews()

        val imageHeight: Double = (150 * (density))

        val parent = LinearLayout(requireContext())
        parent.layoutParams = LinearLayout.LayoutParams(
            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
            LinearLayoutCompat.LayoutParams.WRAP_CONTENT
        )
        parent.orientation = LinearLayout.VERTICAL

        val v3 = layoutInflater.inflate(R.layout.home_banner_item, null)
        v3.txtBannerTitle.visibility = View.GONE

//        if (AppController.instance.isLangArebic) {
//            collectionData.banners?.reverse()
//        }

        v3.Homepager.adapter = BannersAdapter(
            requireContext(),
            (imageHeight.toInt() - resources.getDimension(R.dimen.thirty_dp).toInt()),
            resources.getDimension(R.dimen.fifteen_dp).toInt(),
            resources.getDimension(R.dimen.fifteen_dp).toInt(),
            "",
            "",
            (collectionData.banners as List<String>?)!!
        )
        v3.Homepager.currentItem = 0

        if (collectionData.banners?.size ?: 0 < 2) {
            v3.viewPagerIndicator.visibility = View.GONE
        }
        v3.viewPagerIndicator.noOfPages = collectionData.banners?.size as Int
        v3.viewPagerIndicator.visibleDotCounts = 7
        v3.viewPagerIndicator.onPageChange(v3.Homepager.currentItem)
        v3.Homepager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {

                if (v3.Homepager.currentItem >= 0)
                    v3.viewPagerIndicator.onPageChange(v3.Homepager.currentItem)
                else v3.viewPagerIndicator.onPageChange(0)
            }
        })

        parent.addView(v3)
        fragmentExtendedCategoryBinding?.lnrMain?.addView(parent)
    }

    inner class BannersAdapter internal constructor(
        var mContext: Context,
        private val image_Height: Int,
        private val top_margin: Int,
        private val bottom_margin: Int,
        private val collectionTitle: String,
        private val collectionSubTitle: String,
        private val images: List<String>
    ) : PagerAdapter() {
        private val inflater: LayoutInflater = (mContext as Activity).layoutInflater

        override fun destroyItem(container: View, position: Int, `object`: Any) {
            (container as ViewPager).removeView(`object` as View?)
        }

        override fun finishUpdate(container: View) {}

        override fun getCount(): Int {
            return images.size
        }

        override fun instantiateItem(view: View, position: Int): Any {

            val binding = HomeViewImageBinding.inflate(inflater)

            val relParam =
                RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, image_Height)
            relParam.addRule(RelativeLayout.CENTER_HORIZONTAL)
            relParam.setMargins(top_margin, top_margin, top_margin, top_margin)
            binding?.imgBrand?.layoutParams = relParam

            Utils.loadImagesUsingCoil(
                mContext,
                if (images[position] != null && images[position] != "") images[position] else Constants.strNoImage,
                binding?.imgBrand
            )

            binding.imgPlay.visibility = View.GONE
            binding.txtSubtitle.visibility = View.GONE
            binding.txtTitle.visibility = View.GONE

            binding.relMain.setOnClickListener {
                val bundle = Bundle()
                bundle.putInt(Constants.BANNER_HEIGHT, 0)
                bundle.putSerializable(Constants.MODEL, model)
                bundle.putString("isFromShopCatBanner", "yes")
                val activity = activity as RivaLookBookActivity
                activity?.navController?.navigate(R.id.navigation_product_listing,bundle)
            }

            (view as ViewPager).addView(binding.root)

            return binding.root
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}

        override fun saveState(): Parcelable? {
            return null
        }

        override fun startUpdate(container: View) {}
    }

}