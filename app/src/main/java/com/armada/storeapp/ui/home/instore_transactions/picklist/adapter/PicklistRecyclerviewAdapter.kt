package com.armada.storeapp.ui.home.instore_transactions.picklist.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.armada.storeapp.R
import com.armada.storeapp.data.model.response.PicklistResponseModel
import com.armada.storeapp.databinding.ItemPickOrderBinding


class PicklistRecyclerviewAdapter(
    val items: ArrayList<PicklistResponseModel.PickHeader>,
    val context: Context,
    val isTransferred: Boolean
) :
    RecyclerView.Adapter<PicklistRecyclerviewAdapter.ViewHolder>() {

    var onOpenClick: ((PicklistResponseModel.PickHeader) -> Unit)? = null
    private var picklist: ArrayList<PicklistResponseModel.PickHeader>? = null

    init {
        this.picklist = items
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvInvoiceNumber?.text = picklist?.get(position)?.invoiceNumber
        holder.binding.tvType?.text = picklist?.get(position)?.type
        holder.binding.tvDocumentNumber?.text = picklist?.get(position)?.documentNumber
        holder.binding.tvItemQty?.text = picklist?.get(position)?.totalItem?.toString()
        holder.binding.imageViewActive?.setImageDrawable(getDrawableByStatus(picklist?.get(position)?.active!!))
        holder.binding.imageViewStatus?.setImageDrawable(getDrawableByStatus(picklist?.get(position)?.status!!))
        holder.binding.collapseGroup?.visibility = View.GONE
        holder.binding.btnOpen.setOnClickListener {
            onOpenClick?.invoke(picklist?.get(position)!!)
        }
        if (isTransferred) {
            holder.binding?.btnOpen?.visibility = View.GONE

        } else
            holder.binding?.btnOpen?.visibility = View.VISIBLE
        holder.binding.imageviewDownArrow.setOnClickListener {
            if (holder.binding.collapseGroup.getVisibility() === View.VISIBLE) {
                TransitionManager.beginDelayedTransition(holder.binding.root, AutoTransition())
                holder.binding.collapseGroup.setVisibility(View.GONE)
                holder.binding.imageviewDownArrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            } else {
                TransitionManager.beginDelayedTransition(holder.binding.root, AutoTransition())
                holder.binding.collapseGroup.setVisibility(View.VISIBLE)
                holder.binding.imageviewDownArrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
            }
        }
    }

    fun getDrawableByStatus(trueValue: Boolean): Drawable? {
        if (trueValue) {
            return context?.resources?.getDrawable(
                R.drawable.ic_baseline_circle_green_24,
                context.theme
            )
        }
        return context?.resources?.getDrawable(
            R.drawable.ic_baseline_circle_red_24,
            context.theme
        )


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding =
            ItemPickOrderBinding.inflate(LayoutInflater.from(context), parent, false)

        return ViewHolder(viewBinding)
    }

    override fun getItemCount(): Int {
        if (picklist == null)
            return 0
        return picklist?.size!!
    }


    class ViewHolder(val binding: ItemPickOrderBinding) : RecyclerView.ViewHolder(binding.root)


}