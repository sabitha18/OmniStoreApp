package com.armada.storeapp.ui.home.instore_transactions.picklist.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.armada.storeapp.R
import com.armada.storeapp.data.model.response.PicklistDetailsResponseModel
import com.armada.storeapp.databinding.LayoutPicklistItemBinding


class PicklistItemRecyclerviewAdapter(
    val items: ArrayList<PicklistDetailsResponseModel.PickHeader.PickDetails>,
    val context: Context
) :
    RecyclerView.Adapter<PicklistItemRecyclerviewAdapter.ViewHolder>() {

    private var pickItemlist: ArrayList<PicklistDetailsResponseModel.PickHeader.PickDetails>? = null

    //    var onDeleteClick: ((PicklistDetailsResponseModel.PickHeader.PickDetails, Int) -> Unit)? =
//        null
    init {
        this.pickItemlist = items
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvSourceBin?.text = pickItemlist?.get(position)?.newSourceBin
        holder.binding.tvDestinationBin?.text = pickItemlist?.get(position)?.newDestinationBin
//        holder.binding.tvInvoiceNumber?.text = pickItemlist?.get(position)?.invoiceNumber
        holder.binding.tvItem?.text = pickItemlist?.get(position)?.item
        holder.binding.tvSaleQty?.text = pickItemlist?.get(position)?.quantity.toString()
        holder.binding.tvScannedQty?.text = pickItemlist?.get(position)?.scannedQty?.toString()
//        holder.binding.btnDelete?.setOnClickListener {
//            onDeleteClick?.invoke(pickItemlist?.get(position)!!,position)
//        }
//        holder.binding.tvStoreCode?.text=pickItemlist?.get(position)?.storeCode
//        holder.binding.imageViewActive?.setImageDrawable(getDrawableByStatus(pickItemlist?.get(position)?.active!!))
//        holder.binding.imageViewStatus?.setImageDrawable(getDrawableByStatus(pickItemlist?.get(position)?.status!!))
//
//        holder.binding.checkbox?.isChecked = pickItemlist?.get(position)?.itemScanned!!

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
            LayoutPicklistItemBinding.inflate(LayoutInflater.from(context), parent, false)

        return ViewHolder(viewBinding)
    }

    override fun getItemCount(): Int {
        if (pickItemlist == null)
            return 0
        return pickItemlist?.size!!
    }


    class ViewHolder(val binding: LayoutPicklistItemBinding) : RecyclerView.ViewHolder(binding.root)


}