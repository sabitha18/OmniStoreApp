package com.armada.storeapp.ui.home.others.item_search.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.armada.storeapp.data.model.response.ItemBinSearchResponse
import com.armada.storeapp.data.model.response.ItemNotBinSearchResponse
import com.armada.storeapp.databinding.LayoutItemBincodeBinding


class ItemNoBincodeRecyclerviewAdapter(
    val list: ArrayList<ItemNotBinSearchResponse.BinLogDetails>,
    val context: Context,
    val isItemSearch: Boolean
) :
    RecyclerView.Adapter<ItemNoBincodeRecyclerviewAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvBincode.text = list.get(position).skuCode

        holder.binding.tvQty.text = list.get(position).stockQty?.toString()
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