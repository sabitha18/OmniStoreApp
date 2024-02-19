package com.armada.storeapp.ui.home.warehouse_transactions.stock_return.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.armada.storeapp.data.model.response.StockReturnItemScanResponse
import com.armada.storeapp.databinding.LayoutStockReturnItemBinding


class StockReturnItemRecyclerviewAdapter(
    val items: ArrayList<StockReturnItemScanResponse>,
    val context: Context
) :
    RecyclerView.Adapter<StockReturnItemRecyclerviewAdapter.ViewHolder>() {

    var onDeleteClick: ((StockReturnItemScanResponse, Int) -> Unit)? =
        null
    private var stockItems: ArrayList<StockReturnItemScanResponse>? =
        null

    init {
        this.stockItems = items
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.tvQty?.text = stockItems?.get(position)?.Qty
        holder.binding.tvItemCode?.text = stockItems?.get(position)?.ItemCode
        holder.binding.tvBarcode?.text = stockItems?.get(position)?.strBarCode
        holder.binding.tvAvailableQty?.text = stockItems?.get(position)?.onhand
        holder.binding.tvBincode?.text = stockItems?.get(position)?.binCode
        holder.binding.btnDelete?.setOnClickListener {
            onDeleteClick?.invoke(stockItems?.get(position)!!, position)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding =
            LayoutStockReturnItemBinding.inflate(LayoutInflater.from(context), parent, false)

        return ViewHolder(viewBinding)
    }

    override fun getItemCount(): Int {
        if (stockItems == null)
            return 0
        return stockItems?.size!!
    }


    class ViewHolder(val binding: LayoutStockReturnItemBinding) :
        RecyclerView.ViewHolder(binding.root)


}