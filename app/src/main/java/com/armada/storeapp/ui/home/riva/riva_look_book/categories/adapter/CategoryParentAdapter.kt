package com.armada.storeapp.ui.home.riva.riva_look_book.categories.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.armada.riva.Category.Algolia.CategoriesAdapter
import com.armada.storeapp.R
import com.armada.storeapp.data.model.response.CollectionGroupsItemModel
import com.armada.storeapp.data.model.response.CollectionListItemModel
import com.armada.storeapp.data.model.response.SubCategoryResponse
import com.armada.storeapp.databinding.LayoutCategoryView1Binding
import com.armada.storeapp.databinding.LayoutCategoryView2Binding
import com.armada.storeapp.ui.home.riva.riva_look_book.RivaLookBookActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.home.adapter.HomeImageAdapter
import com.armada.storeapp.ui.utils.Utils

class CategoryParentAdapter(
    private val collectionData: ArrayList<CollectionGroupsItemModel>,
    private val activity: RivaLookBookActivity,
    val density: Double
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_ONE =
            1  // Viewpager - image width is equals to 320 & count is greater than 1
        const val VIEW_TYPE_TWO = 2  //Else
    }


    override fun getItemViewType(position: Int): Int {
        if (collectionData[position]?.image_width!! == 320 && collectionData[position]?.collection_list != null && collectionData[position]?.collection_list?.size ?: 0 > 1) {
            return VIEW_TYPE_ONE
        } else {
            return VIEW_TYPE_TWO
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is View1ViewHolder) {
            holder.onBind(position)
        } else if (holder is View2ViewHolder) {
            holder.onBind(position)
        }
    }

    override fun getItemCount(): Int = collectionData.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_ONE) {
            return View1ViewHolder(
                LayoutCategoryView1Binding.inflate(LayoutInflater.from(activity), parent, false)
            )
        } else if (viewType == VIEW_TYPE_TWO) {
            return View2ViewHolder(
                LayoutCategoryView2Binding.inflate(LayoutInflater.from(activity), parent, false)
            )
        }
        return View2ViewHolder(
            LayoutCategoryView2Binding.inflate(LayoutInflater.from(activity), parent, false)
        )
    }


    ///ViewPager
    private inner class View1ViewHolder(val binding: LayoutCategoryView1Binding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(position: Int) {
            var imageHeight: Double = (collectionData[position]!!.image_height!! * (density))
            val image_Margin: Double = (collectionData[position]!!.image_margin!! * (density))
            val topMargin: Double = (collectionData[position]!!.margin_top!! * (density))
            val bottomMargin: Double = (collectionData[position]!!.margin_bottom!! * (density))
            val collectionTitle = collectionData[position]!!.hide_collection_title!!
            val collectionSubTitle = collectionData[position]!!.hide_collection_sub_title!!
            val hide_underline: String = collectionData[position]!!.hide_underline.toString()!!

            //apply margin top/bottom to main layout
            val layoutParam = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParam.setMargins(0, topMargin.toInt(), 0, bottomMargin.toInt())
            binding.root.layoutParams = layoutParam

            if (collectionData[position]!!.hide_title==1) {
                binding.txtBannerTitle.visibility = View.GONE

            } else if (collectionData[position]!!.hide_title==0) {
                    val lpText = RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    lpText.addRule(RelativeLayout.CENTER_HORIZONTAL)
                    //small_param1.setMargins(image_Margin.toInt(), top_margin.toInt(), 0, bottom_margin.toInt())
                    binding.txtBannerTitle.visibility = View.VISIBLE

                    binding.txtBannerTitle.layoutParams = lpText
                binding.txtBannerTitle.text =
                        Utils.getDynamicStringFromApi(activity, collectionData[position]!!.title)


                if (topMargin > 0)
                    binding.txtBannerTitle.setPadding(
                        activity.resources.getDimension(R.dimen.seven_dp).toInt(),
                        0,
                        activity.resources.getDimension(R.dimen.seven_dp).toInt(),
                        0
                    )
                else binding.txtBannerTitle.setPadding(
                    activity.resources.getDimension(R.dimen.seven_dp).toInt(),
                    activity.resources.getDimension(R.dimen.five_dp).toInt(),
                    activity.resources.getDimension(R.dimen.seven_dp).toInt(),
                    activity.resources.getDimension(R.dimen.seven_dp).toInt()
                )
            }

            binding.viewPagerIndicator.noOfPages = collectionData[position]?.collection_list?.size as Int
            binding.viewPagerIndicator.visibleDotCounts = 7
            binding.viewPagerIndicator.onPageChange(binding.Homepager.currentItem)
            binding.Homepager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {

                }

                override fun onPageSelected(position: Int) {

                    if (binding.Homepager.currentItem >= 0)
                        binding.viewPagerIndicator.onPageChange(binding.Homepager.currentItem)
                    else binding.viewPagerIndicator.onPageChange(0)
                }
            })

            binding.Homepager.adapter = HomeImageAdapter(
                activity,
                imageHeight.toInt(),
                topMargin.toInt(),
                bottomMargin.toInt(),
                collectionTitle,
                collectionSubTitle,
                (collectionData.get(position)!!.collection_list as List<CollectionListItemModel>?)!!,
                false,
                activity
            )

            val titleHeight = activity.resources.getDimension(R.dimen.sixty_dp)
            if (collectionTitle == 0) {
                imageHeight += titleHeight
            }

            val pagerSize = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                imageHeight.toInt()
            )
            pagerSize.addRule(RelativeLayout.BELOW, R.id.txtBannerTitle)
            binding.Homepager.layoutParams = pagerSize
            binding.Homepager.currentItem = 0
            binding.viewPagerIndicator.visibility = View.GONE
        }
    }

    private inner class View2ViewHolder(val binding: LayoutCategoryView2Binding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(position: Int) {

            val imageHeight: Double = (collectionData[position]!!.image_height!! * (density))
            val imageWidth: Double = (collectionData[position]!!.image_width!! * (density))
            val imageMargin: Double = (collectionData[position]!!.image_margin!! * (density))
            val topMargin: Double = (collectionData[position]!!.margin_top!! * (density))
            val bottomMargin: Double = (collectionData[position]!!.margin_bottom!! * (density))
            val collectionTitle: String = collectionData[position]!!.hide_collection_title.toString()!!
            val collectionSubTitle: String = collectionData[position]!!.hide_collection_sub_title.toString()!!
            val hideUnderline: String = collectionData[position]!!.hide_underline.toString()!!

            binding.rvHomeviewList.layoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

            if (collectionData[position]!!.hide_title==1) {

                val lpText = RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                lpText.addRule(RelativeLayout.CENTER_HORIZONTAL)
                lpText.setMargins(0, 0, 0, 0)
                binding.txtCollectionTitle.visibility = View.GONE

                binding.txtCollectionTitle.layoutParams = lpText

            } else if (collectionData.get(position)!!.hide_title==0) {

                val lpText = RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                lpText.addRule(RelativeLayout.CENTER_HORIZONTAL)
                lpText.setMargins(
                    imageMargin.toInt(),
                    activity.resources.getDimension(R.dimen.medium_margin).toInt(),
                    0,
                    0
                )
                binding.txtCollectionTitle.visibility = View.VISIBLE
                binding.txtCollectionTitle.layoutParams = lpText
                binding.txtCollectionTitle.text =
                    Utils.getDynamicStringFromApi(activity, collectionData[position]?.title)
            }

            val dataAdapter = CategoriesAdapter(
                collectionData?.get(position)?.collection_list!!,
                activity,
                imageHeight.toInt(),
                imageWidth.toInt(),
                imageMargin.toInt(),
                topMargin.toInt(),
                bottomMargin.toInt(),
                collectionTitle,
                collectionSubTitle,
                hideUnderline
            )
            binding.rvHomeviewList.adapter = dataAdapter

        }
    }


}