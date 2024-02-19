package com.armada.storeapp.ui.home.others.item_search.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.armada.storeapp.data.model.response.ItemBinSearchResponse
import com.armada.storeapp.databinding.LayoutItemBincodeBinding


class ItemBincodeRecyclerviewAdapter(
    val list: ArrayList<ItemBinSearchResponse.BinLogDetails>,
    val context: Context,
    val isItemSearch: Boolean
) :
    RecyclerView.Adapter<ItemBincodeRecyclerviewAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (list.get(position).quantity?.toInt()!! > 0) {
            if (isItemSearch) {
                holder.binding.tvBincode.text = list.get(position).fromBinCode
            } else
                holder.binding.tvBincode.text = list.get(position).skuCode

            holder.binding.tvQty.text = list.get(position).quantity?.toString()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding =
            LayoutItemBincodeBinding.inflate(LayoutInflater.from(context), parent, false)

        return ViewHolder(viewBinding)
    }

    override fun getItemCount(): Int {
        if (list == null)
            return 0
        return list?.size!!
    }


    class ViewHolder(val binding: LayoutItemBincodeBinding) : RecyclerView.ViewHolder(binding.root)


}