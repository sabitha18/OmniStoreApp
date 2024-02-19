package com.armada.storeapp.ui.home.warehouse_transactions.stock_receive.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.armada.storeapp.data.model.response.OpenStockReceiptDocumentResponse
import com.armada.storeapp.databinding.LayoutStockReceiptItemBinding


class StockItemRecyclerviewAdapter(
    val items: ArrayList<OpenStockReceiptDocumentResponse.OpenStockReceiptDocumentResponseItem>,
    val context: Context
) :
    RecyclerView.Adapter<StockItemRecyclerviewAdapter.ViewHolder>() {

    //    var onReceiveClick: ((StockReceiptDocumentResponseModel.StockReceiptDocumentResponseModelItem) -> Unit)? =
//        null
    private var stockItems: ArrayList<OpenStockReceiptDocumentResponse.OpenStockReceiptDocumentResponseItem>? =
        null

    init {
        this.stockItems = items
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvSno?.text = "${position + 1}"
        holder.binding.tvQty?.text = stockItems?.get(position)?.QUANTITY?.toString()
        holder.binding.tvItemCode?.text = stockItems?.get(position)?.ITEMCODE
        holder.binding.tvBarcode?.text = stockItems?.get(position)?.BARCODE?.toString()
//        holder.binding.tvItemDescription?.text = stockItems?.get(position)?.ITEMNAME
        holder.binding.tvCollectionQty?.text = stockItems?.get(position)?.CQTY?.toString()
        holder.binding.tvReceiptQty?.text = stockItems?.get(position)?.RQTY?.toString()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding =
            LayoutStockReceiptItemBinding.inflate(LayoutInflater.from(context), parent, false)

        return ViewHolder(viewBinding)
    }

    override fun getItemCount(): Int {
        if (stockItems == null)
            return 0
        return stockItems?.size!!
    }


    class ViewHolder(val binding: LayoutStockReceiptItemBinding) :
        RecyclerView.ViewHolder(binding.root)


}