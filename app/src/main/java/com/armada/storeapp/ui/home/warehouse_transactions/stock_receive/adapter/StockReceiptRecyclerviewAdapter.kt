package com.armada.storeapp.ui.home.warehouse_transactions.stock_receive.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.armada.storeapp.data.model.response.StockReceiptDocumentResponseModel
import com.armada.storeapp.databinding.ItemStockReceiptBinding


class StockReceiptRecyclerviewAdapter(
    val documents: ArrayList<StockReceiptDocumentResponseModel.StockReceiptDocumentResponseModelItem>,
    val context: Context
) :
    RecyclerView.Adapter<StockReceiptRecyclerviewAdapter.ViewHolder>() {

    var onReceiveClick: ((StockReceiptDocumentResponseModel.StockReceiptDocumentResponseModelItem) -> Unit)? =
        null
    private var stockReceiptsLists: ArrayList<StockReceiptDocumentResponseModel.StockReceiptDocumentResponseModelItem>? =
        null

    init {
        this.stockReceiptsLists = documents
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvId?.text = stockReceiptsLists?.get(position)?.ID?.toString()
        holder.binding.tvFromLocation?.text = stockReceiptsLists?.get(position)?.FRMLOC
        holder.binding.tvToLocation?.text = stockReceiptsLists?.get(position)?.TOLOC
        holder.binding.tvQty?.text = stockReceiptsLists?.get(position)?.QTY?.toString()
        holder.binding.tvDate?.text = stockReceiptsLists?.get(position)?.DOCDATE
        holder.binding.tvRecvdQty?.text = stockReceiptsLists?.get(position)?.TRNSQTY?.toString()
        holder.binding.tvRefNo?.text = stockReceiptsLists?.get(position)?.TRNSNO
        holder.binding.tvRemarks?.text = stockReceiptsLists?.get(position)?.REMARKS
        holder.binding.btnReceive.setOnClickListener {
            onReceiveClick?.invoke(stockReceiptsLists?.get(position)!!)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding =
            ItemStockReceiptBinding.inflate(LayoutInflater.from(context), parent, false)

        return ViewHolder(viewBinding)
    }

    override fun getItemCount(): Int {
        if (stockReceiptsLists == null)
            return 0
        return stockReceiptsLists?.size!!
    }


    class ViewHolder(val binding: ItemStockReceiptBinding) : RecyclerView.ViewHolder(binding.root)


}