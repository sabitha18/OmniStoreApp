package com.armada.storeapp.ui.home.warehouse_transactions.stock_adjustment.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.recyclerview.widget.RecyclerView
import com.armada.storeapp.data.model.response.StockAdjustmentScanModelResponse
import com.armada.storeapp.databinding.ItemStockAdjustmentBinding


class StockAdjustmentItemRecyclerviewAdapter(
    val items: ArrayList<StockAdjustmentScanModelResponse.StockAdjustmentScanModelResponseItem>,
    val context: Context
) :
    RecyclerView.Adapter<StockAdjustmentItemRecyclerviewAdapter.ViewHolder>() {

    var onCountChange: ((stockItem: StockAdjustmentScanModelResponse.StockAdjustmentScanModelResponseItem) -> Unit)? =
        null
    private var stockItems: ArrayList<StockAdjustmentScanModelResponse.StockAdjustmentScanModelResponseItem>? =
        null

    init {
        this.stockItems = items
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = stockItems?.get(position)
        holder.binding.tvSku.text = currentItem?.ITEMCODE
        holder.binding.tvInstock.text = currentItem?.INSTOCK?.toString()
        holder.binding.edtCount.setText(currentItem?.COUNT?.toString())
        holder.binding.tvDiff.text = currentItem?.DIFF?.toString()

        holder.binding.edtCount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                text: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(text: Editable?) {
                try {
                    if (!text?.isEmpty()!!) {
                        if (text?.isDigitsOnly()) {
                            val count = text.toString().toInt()
                            val diff = count - currentItem?.INSTOCK!!
                            holder.binding.tvDiff.text = diff.toString()

                            currentItem.DIFF = diff
                            currentItem.COUNT = count
                            onCountChange?.invoke(currentItem)
                        } else
                            Toast.makeText(
                                context,
                                "Please enter a valid number",
                                Toast.LENGTH_SHORT
                            ).show()

                    }
//                    else {
//                        val count = 0
//                        val diff = currentItem?.INSTOCK!! - count
//                        holder.binding.tvDiff.text = diff.toString()
//
//                        currentItem.DIFF = diff
//                        currentItem.COUNT = count
//                        onCountChange?.invoke(currentItem)
//                    }
//                    else{
//                        holder.binding.edtCount?.setText("0")
//                        holder.binding.edtCount?.setSelection(1)
//                    }
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            }
        })
        holder.binding.edtCount.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus && holder.binding.edtCount.text.toString().isEmpty()) {
                holder.binding.edtCount.setText(currentItem?.INSTOCK?.toString())
            }
        }
        try {
            if (currentItem?.INSTOCK!! > 0) {
                currentItem?.DIFF = currentItem?.COUNT!! - currentItem?.INSTOCK!!
                holder.binding.tvDiff?.text = currentItem?.DIFF?.toString()
                onCountChange?.invoke(currentItem)


            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding =
            ItemStockAdjustmentBinding.inflate(LayoutInflater.from(context), parent, false)

        return ViewHolder(viewBinding)
    }

    override fun getItemCount(): Int {
        if (stockItems == null)
            return 0
        return stockItems?.size!!
    }


    class ViewHolder(
        val binding: ItemStockAdjustmentBinding
    ) :
        RecyclerView.ViewHolder(binding.root)


}