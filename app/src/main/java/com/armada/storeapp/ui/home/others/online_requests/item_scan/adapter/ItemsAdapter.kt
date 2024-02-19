package com.armada.storeapp.ui.home.others.online_requests.item_scan.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.armada.storeapp.data.model.response.OpenDocumentResponseModel
import com.armada.storeapp.data.model.response.ShopPickReasonResponseModel
import com.armada.storeapp.databinding.LayoutItemScanBinding
import com.armada.storeapp.ui.home.others.online_requests.item_scan.ItemScanFragment

class ItemsAdapter(
    val items: ArrayList<OpenDocumentResponseModel.OpenDocumentResponseModelItem>,
    val context: Context,
    fragment: ItemScanFragment,
    val remarks: ArrayList<ShopPickReasonResponseModel.ShopPickReasonResponseModelItem>
) :
    RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {

    var itemsList: ArrayList<OpenDocumentResponseModel.OpenDocumentResponseModelItem>
    var remarksList: ArrayList<ShopPickReasonResponseModel.ShopPickReasonResponseModelItem>
    val itemScanFragment = fragment
    lateinit var viewHolder: ViewHolder

    init {
        this.itemsList = items
        this.remarksList = remarks
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        viewHolder = holder
        val currentItem = itemsList?.get(position)
        holder.binding.tvItemCode?.text = currentItem?.ITEM_CODE
        holder.binding.tvOrderQty?.text =
            currentItem?.ORDER_QTY.toString()
        holder.binding.tvScannedQty?.text =
            currentItem?.PICK_QTY.toString()
        holder.binding.tvFinalLocationCode?.text =
            currentItem?.LOCATION_CODE
        holder.binding.tvFromLocation?.text =
            currentItem?.FROM_LOCATION
        holder.binding.tvOrderNo?.text = currentItem?.ORDER_NO
        holder.binding.tvWhLocation?.text = currentItem?.WH_CODE

//        if (currentItem.USER_REMARKS == null)
//            holder.binding.btnAddItem.visibility = View.VISIBLE
//        else
//            holder.binding.btnAddItem.visibility = View.GONE

//        holder.binding.btnAddItem?.setOnClickListener {
//            val remark =
//                holder.binding.spinnerRemarks.selectedItem as ShopPickReasonResponseModel.ShopPickReasonResponseModelItem
//            if (remark.REASON?.contains("FOUND AND SCANNED") == true) {
//                if (holder.binding.tvScannedQty.text.toString().toInt() == 0)
//                    Toast.makeText(context, "Please scan the item", Toast.LENGTH_LONG).show()
//                else
//                    itemPickListener.onItemPicked(itemsList?.get(position), remark)
//            } else {
//                itemPickListener.onItemPicked(itemsList?.get(position), remark)
//            }
//
//        }

        holder.binding.spinnerRemarks.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                itemsList.get(holder.adapterPosition).USER_REMARKS =
                    remarksList.get(position).REASON
                itemScanFragment.updateRemarks(itemsList)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })
        holder.binding.spinnerRemarks?.setAdapter(
            RemarksSpinnerAdapter(
                context, remarksList
            )
        )

        if (currentItem.USER_REMARKS != null) {
            for ((index, value) in remarksList.withIndex()) {
                if (currentItem.USER_REMARKS.toString() == value.REASON)
                    holder.binding.spinnerRemarks.setSelection(index)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding =
            LayoutItemScanBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(viewBinding)
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
//        return items.size
        return itemsList!!.size;
    }


    class ViewHolder(val binding: LayoutItemScanBinding) :
        RecyclerView.ViewHolder(binding.root)

//    fun onItemScanFinished(itemCode: ScanItemResponseModel.ScanItemResponseModelItem) {
//        for (item in itemsList) {
//            if (item.ITEM_CODE == itemCode.ITEMCODE) {
//                val pos=viewHolder.layoutPosition
//
//                viewHolder.binding.tvScannedQty?.text = "33"
//            }
//        }
//    }


}