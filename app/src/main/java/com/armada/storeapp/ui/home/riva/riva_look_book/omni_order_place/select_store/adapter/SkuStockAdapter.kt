package com.armada.storeapp.ui.home.riva.riva_look_book.omni_order_place.select_store.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.armada.storeapp.R
import com.armada.storeapp.data.model.response.OmniStoreStockCheckResponse
import com.armada.storeapp.databinding.LayoutOmniStoreItemBinding
import com.armada.storeapp.databinding.LayoutSkuStockBinding

class SkuStockAdapter(
    private val context: Context,
    var storeList: ArrayList<OmniStoreStockCheckResponse>?
) : RecyclerView.Adapter<SkuStockAdapter.ItemHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemHolder {
        return ItemHolder(
            LayoutSkuStockBinding.inflate(
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

        val stock = storeList?.get(position)
        holder.binding.tvSku.text = stock?.skuCode
        val availableQty = stock?.availableQty
        holder.binding.tvAvailableQty.text = "Qty: $availableQty"

    }

    override fun getItemCount(): Int {
        return if (storeList == null) 0 else storeList!!.size
    }

    class ItemHolder(val binding: LayoutSkuStockBinding) :
        RecyclerView.ViewHolder(binding.root)
}