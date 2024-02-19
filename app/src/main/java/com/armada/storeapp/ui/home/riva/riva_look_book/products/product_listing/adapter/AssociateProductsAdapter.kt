package com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing.adapter

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.app.ActivityOptionsCompat.makeSceneTransitionAnimation
import androidx.recyclerview.widget.RecyclerView
import com.armada.storeapp.R
import com.armada.storeapp.databinding.LvItemAssociateProductsListBinding
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.OmniProductDetailsActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.ProductDetailsActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing.model.AssociateProductModel
import com.armada.storeapp.ui.utils.Constants
import com.armada.storeapp.ui.utils.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener

class AssociateProductsAdapter(
    private val selectedCurrency:String,
    val arrayList: ArrayList<AssociateProductModel>?,
    private val container_width: Double?,
    val finalPrice: String?,
    val strId: String,
    val activity: Activity,
) : RecyclerView.Adapter<AssociateProductsAdapter.ItemViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LvItemAssociateProductsListBinding.inflate(
                LayoutInflater.from(
                    activity
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val product = arrayList?.get(position)

        val metrics = activity.resources.displayMetrics
        val fullWidth = (metrics.widthPixels.toDouble())
        val density = (metrics.widthPixels.toDouble() / 320)

        Glide.with(activity)
            .load(if (product?.image != null && product.image != "") product.image else Constants.strNoImage)
            .listener(object : RequestListener<Drawable> {

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            })
            .transition(DrawableTransitionOptions.withCrossFade(800))
            .into(holder.binding.imgAssociateProduct)


        val linParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        when (position) {
            0 -> {
                linParam.marginStart =
                    activity.resources.getDimension(R.dimen.ten_dp).toInt()
                linParam.marginEnd =
                    activity.resources.getDimension(R.dimen.five_dp).toInt()
            }
            (arrayList?.size?.minus(1)) -> {
                linParam.marginEnd =
                    activity.resources.getDimension(R.dimen.ten_dp).toInt()
            }
            else -> {
                linParam.marginEnd =
                    activity.resources.getDimension(R.dimen.five_dp).toInt()
            }
        }
        holder.binding.linAssociated.layoutParams = linParam

        val imgParam = if (container_width != 0.0) {
            val containerWidth = (container_width!! * (density))
            val containerHeight = containerWidth * 1.482
            LinearLayout.LayoutParams(
                containerWidth.toInt(),
                containerHeight.toInt()
            )
        } else {
            LinearLayout.LayoutParams(
                activity.resources.getDimension(R.dimen.ninety_dp).toInt(),
                activity.resources.getDimension(R.dimen.hundred_thirtyfive_dp).toInt()
            )
        }
        holder.binding.imgAssociateProduct.layoutParams = imgParam

        holder.binding.txtTitle.text =
            Utils.getPriceFormatted(product?.final_price.toString(),selectedCurrency)
        holder.binding.root.setOnClickListener {
            val intent = Intent(activity, OmniProductDetailsActivity::class.java)
            intent.putExtra("id", product?.entity_id.toString())
            intent.putExtra("cat_id", strId)
            intent.putExtra("image", product?.image ?: "")
            holder.binding.imgAssociateProduct.transitionName = "imageproduct"
            val options =
                makeSceneTransitionAnimation(
                    activity,
                    holder.binding.imgAssociateProduct,
                    "imageproduct"
                )
            activity.startActivity(intent, options.toBundle())
        }
    }


    override fun getItemCount(): Int {
        return if (arrayList.isNullOrEmpty()) 0 else arrayList.size
    }

    inner class ItemViewHolder(val binding: LvItemAssociateProductsListBinding) :
        RecyclerView.ViewHolder(binding.root)

}