package com.armada.storeapp.ui.home.riva.riva_look_book.omni_order_place.select_store.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.armada.storeapp.R
import com.armada.storeapp.data.model.response.OmniStoreStockCheckResponse
import com.armada.storeapp.data.model.response.SkuMasterTypes
import com.armada.storeapp.databinding.LayoutOmniStoreItemBinding
import com.armada.storeapp.ui.utils.Utils

class OmniStoreAdapter(
    private val context: Context,
    var storeList: ArrayList<OmniStoreStockCheckResponse>?
) : RecyclerView.Adapter<OmniStoreAdapter.ItemHolder>() {

    var onStoreClicked: ((OmniStoreStockCheckResponse, String) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemHolder {
        return ItemHolder(
            LayoutOmniStoreItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(
        holder: ItemHolder,
        position: Int
    ) {

        val store = storeList?.get(position)

//        holder.binding.recyclerView.layoutManager =
//            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//        val skuStockAdapter = SkuStockAdapter(context, storeItemList)
//        holder.binding.recyclerView.adapter = skuStockAdapter

        if (store?.status?.equals("Available") == true ) {
            holder.binding.tvAvailability.text = store.status
            holder.binding.tvAvailability.setTextColor(context.resources.getColor(R.color.green))

        } else if (store?.status?.equals("Unavailable") == true) {
            holder.binding.tvAvailability.text = store.status
            holder.binding.tvAvailability.setTextColor(context.resources.getColor(R.color.red))
        }

        try {
            if (store?.warehouseID!! > 0 && store?.warehouseName != null && store?.warehouseName.isNotEmpty()
            ) {
                holder.binding.tvStoreName.text = store.warehouseName
                holder.binding.tvStoreCode.text = store?.warehouseCode
            } else{
                holder.binding.tvStoreName.text = store.storeName
                holder.binding.tvStoreCode.text = store?.storeCode
            }

        } catch (exception: Exception) {
            exception.printStackTrace()
        }

        holder.binding.root.setOnClickListener {
            if (store?.status.equals("Available")) {
                onStoreClicked?.invoke(store!!, store?.status)
            } else {
                Utils.showSnackbar(
                    holder.binding.root,
                    "The stock is not available in the selected store"
                )
            }

        }

//
//        val availableQty = store?.availableQty
//        if (availableQty!! > 0) {
//            holder.binding.tvAvailability.text = "Available"
//            holder.binding.tvAvailability.setTextColor(context.resources.getColor(R.color.green))
//        } else {
//            holder.binding.tvAvailability.text = "Unavailable"
//            holder.binding.tvAvailability.setTextColor(context.resources.getColor(R.color.red))
//        }
//
//        holder.binding.tvAvailableQty.text = "Qty: $availableQty"
//
//        if(store.warehouseID>0 && store.warehouseName!=null && store.warehouseName.isNotEmpty()){
//          holder.binding.tvWarehouseName.text=store.warehouseName
//        }

    }


    // method for filtering our recyclerview items.
    fun filterList(filterlist: ArrayList<OmniStoreStockCheckResponse>) {
        storeList = filterlist
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return if (storeList == null) 0 else storeList!!.size
    }

    class ItemHolder(val binding: LayoutOmniStoreItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}