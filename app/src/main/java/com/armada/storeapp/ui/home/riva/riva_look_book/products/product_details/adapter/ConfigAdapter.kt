package com.armada.riva.ProductDetail.adapter

import android.app.Activity
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.armada.storeapp.R
import com.armada.storeapp.data.model.response.ProductDetailsAttribute
import com.armada.storeapp.databinding.CircularImageViewBinding
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details.interfaces.OnAttributeSelectListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class ConfigAdapter(
    val activity: Activity,
    val arrListAttr: ArrayList<ProductDetailsAttribute>,
    val attrType: String,
    val attrSelectListener: OnAttributeSelectListener?
) : RecyclerView.Adapter<ConfigAdapter.ConfigViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConfigViewHolder {
        return ConfigViewHolder(
            CircularImageViewBinding.inflate(
                LayoutInflater.from(activity),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return arrListAttr.size
    }

    override fun onBindViewHolder(holder: ConfigViewHolder, position: Int) {

        val attr = arrListAttr.get(position)
        if (attrType.contains("color")) {
            Glide.with(activity)
                .asBitmap()
                .load(attr.attribute_image_url)
                .override(
                    activity.resources.getDimension(R.dimen.twenty_five_dp).toInt(),
                    activity.resources.getDimension(R.dimen.twenty_five_dp).toInt()
                )
                .listener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false

                    }

                    override fun onResourceReady(
                        resource: Bitmap?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }


                })
                .into(holder.binding.imgColor)
            if (attr.isSelected == true) {
                holder.binding.imgBorder.setBackgroundResource(R.drawable.attribute_sel_color_border)
            } else {
                holder.binding.imgBorder.setBackgroundResource(R.drawable.attributes_unsel_color_border)
            }
            holder.binding.imgColor.visibility = View.VISIBLE
            holder.binding.imgBorder.visibility = View.VISIBLE
            holder.binding.txtSize.visibility = View.GONE
            //println("Here i am attribute color   " + attr)
            holder.binding.root.setOnClickListener {
                attrSelectListener?.onAttributeSelected(attr, position)
            }
        } else {
            holder.binding.imgColor.visibility = View.GONE
            holder.binding.txtSize.text = attr.value
            //   holder.itemView.txtSize.setBackgroundResource(R.drawable.attributes_unsel_border)

            holder.binding.txtSize.visibility = View.VISIBLE

            if (attr.isSelected == true) {
                holder.binding.txtSize.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.white
                    )
                )
                holder.binding.imgBorder.setBackgroundResource(R.drawable.attributes_sel_border)
            } else {
                holder.binding.txtSize.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.black
                    )
                )
                holder.binding.imgBorder.setBackgroundResource(R.drawable.attributes_unsel_border)
                if (attr.isAvailable == true) {
                    holder.binding.txtSize.setTextColor(
                        ContextCompat.getColor(
                            activity,
                            R.color.black
                        )
                    )
                    holder.binding.imgBorder.setBackgroundResource(R.drawable.attributes_unsel_border)
                } else {
                    holder.binding.txtSize.setTextColor(
                        ContextCompat.getColor(
                            activity,
                            R.color.stroke_color
                        )
                    )
                    holder.binding.imgBorder.setBackgroundResource(R.drawable.attributes_disable_border)
                }
            }


            holder.binding.root.setOnClickListener {
                attrSelectListener?.onAttributeSelected(attr, position)
            }
        }
    }

    class ConfigViewHolder(val binding: CircularImageViewBinding) :
        RecyclerView.ViewHolder(binding.root)


}