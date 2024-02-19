package com.armada.storeapp.ui.home.riva.riva_look_book.products.product_filter.adapter

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.armada.storeapp.R
import com.armada.storeapp.databinding.*
import com.armada.storeapp.ui.home.riva.riva_look_book.home.adapter.HomeBannerRecyclerviewAdapter
import com.armada.storeapp.ui.home.riva.riva_look_book.product_listing.ProductFilterActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing.model.ProductListMAllFilterModel
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing.model.ProductListMOptionModel
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing.model.ProductListMSortingModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.ImageViewTarget
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.lv_item_filter_detail_data.view.*
import android.graphics.drawable.BitmapDrawable as BitmapDrawable1

class FilterAdapter(
    private val activity: ProductFilterActivity,
    private val selectedFilterPosition: Int,
    private val viewType: Int,
    private val filterDataList: ArrayList<ProductListMAllFilterModel>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onFilterSelected: ((ArrayList<ProductListMAllFilterModel>, Int) -> Unit)? = null

    companion object {
        const val VIEW_TYPE_COLOR = 0
        const val VIEW_TYPE_SIZE = 1
        const val VIEW_TYPE_BRAND = 2
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            VIEW_TYPE_COLOR -> {
                return ColorViewHolder(
                    LayoutItemColorBinding.inflate(LayoutInflater.from(activity), parent, false)
                )
            }
            VIEW_TYPE_SIZE -> {
                return SizeViewHolder(
                    LayoutItemSizeBinding.inflate(LayoutInflater.from(activity), parent, false)
                )
            }
            VIEW_TYPE_BRAND -> {
                return BrandViewHolder(
                    LayoutItemBrandBinding.inflate(LayoutInflater.from(activity), parent, false)
                )
            }

            else -> return BrandViewHolder(
                LayoutItemBrandBinding.inflate(LayoutInflater.from(activity), parent, false)
            )
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ColorViewHolder -> {
                holder.onBind(position)
            }
            is SizeViewHolder -> {
                holder.onBind(position)
            }
            is BrandViewHolder -> {
                holder.onBind(position)
            }
            else -> {
                val viewHolder = holder as BrandViewHolder
                viewHolder.onBind(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return filterDataList.get(selectedFilterPosition)?.options!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return viewType
    }

    inner class ColorViewHolder(val binding: LayoutItemColorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(position: Int) {
            val colorItem = filterDataList.get(selectedFilterPosition)?.options?.get(position)
            binding.tvColorName.text = colorItem?.attribute_name.toString().trim().uppercase()
            if (colorItem?.isSelected!!) {
                binding.tvColorName?.setTextColor(activity.resources.getColor(R.color.brown_text_color))
            } else
                binding.tvColorName?.setTextColor(activity.resources.getColor(R.color.black))
            Glide.with(activity)
                .asBitmap()
                .load(
                    colorItem?.swatch_url
                )
                .listener(object : RequestListener<Bitmap> {
                    override fun onResourceReady(
                        resource: Bitmap?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                })
                .into(binding.imageViewColor)

            binding.root.setOnClickListener {
                if (colorItem?.isSelected != null && colorItem.isSelected!!) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        binding.tvColorName?.setTextColor(
                            activity.resources.getColor(
                                R.color.black,
                                activity.resources.newTheme()
                            )
                        )
                    } else {
                        binding.tvColorName?.setTextColor(activity.resources.getColor(R.color.black))
                    }
                    colorItem?.isSelected = false
                } else {
                    colorItem?.isSelected = true
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        binding.tvColorName?.setTextColor(
                            activity.resources.getColor(
                                R.color.brown_text_color,
                                activity.resources.newTheme()
                            )
                        )
                    } else {
                        binding.tvColorName?.setTextColor(activity.resources.getColor(R.color.brown_text_color))
                    }
                }
                filterDataList.get(selectedFilterPosition)?.options?.set(position, colorItem!!)
                onFilterSelected?.invoke(filterDataList, 0)
                activity.updatedFilters(filterDataList)
            }


        }
    }

    inner class SizeViewHolder(val binding: LayoutItemSizeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(position: Int) {
            val sizeItem = filterDataList.get(selectedFilterPosition)?.options?.get(position)
            binding.tvSizeName.text = sizeItem?.attribute_name.toString().trim().uppercase()
            if (sizeItem?.isSelected!!) {
                binding.root?.background = activity.resources.getDrawable(
                    R.drawable.rectangle_black,
                    activity.resources.newTheme()
                )
            } else {
                binding.root?.background = activity.resources.getDrawable(
                    R.drawable.rectangle_grey,
                    activity.resources.newTheme()
                )
            }
            binding.root.setOnClickListener {
                if (sizeItem?.isSelected != null && sizeItem.isSelected!!) {
                    binding.root?.background = activity.resources.getDrawable(
                        R.drawable.rectangle_grey,
                        activity.resources.newTheme()
                    )
                    sizeItem?.isSelected = false
                } else {
                    sizeItem?.isSelected = true
                    binding.root?.background = activity.resources.getDrawable(
                        R.drawable.rectangle_black,
                        activity.resources.newTheme()
                    )
                }
                filterDataList.get(selectedFilterPosition)?.options?.set(position, sizeItem!!)
                onFilterSelected?.invoke(filterDataList, 1)
                activity.updatedFilters(filterDataList)
            }
        }
    }

    inner class BrandViewHolder(val binding: LayoutItemBrandBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(position: Int) {
            val brandItem = filterDataList.get(selectedFilterPosition)?.options?.get(position)
            binding.tvBrandName.text = brandItem?.attribute_name.toString().trim().uppercase()
            if (brandItem?.isSelected!!) {
                binding.root?.background = activity.resources.getDrawable(
                    R.drawable.rectangle_black,
                    activity.resources.newTheme()
                )
            } else {
                binding.root?.background = activity.resources.getDrawable(
                    R.drawable.rectangle_grey,
                    activity.resources.newTheme()
                )
            }
            binding.root.setOnClickListener {
                if (brandItem?.isSelected != null && brandItem.isSelected!!) {
                    binding.root?.background = activity.resources.getDrawable(
                        R.drawable.rectangle_grey,
                        activity.resources.newTheme()
                    )
                    brandItem?.isSelected = false
                } else {
                    brandItem?.isSelected = true
                    binding.root?.background = activity.resources.getDrawable(
                        R.drawable.rectangle_black,
                        activity.resources.newTheme()
                    )
                }
                filterDataList.get(selectedFilterPosition)?.options?.set(position, brandItem!!)
                onFilterSelected?.invoke(filterDataList, 2)
                activity.updatedFilters(filterDataList)
            }
        }
    }
}