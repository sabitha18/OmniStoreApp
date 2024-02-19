package com.armada.storeapp.ui.omni_orders.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.armada.storeapp.R
import com.armada.storeapp.data.model.response.OmnIOrderHeader
import com.armada.storeapp.data.model.response.OmnIPendingAcceptance
import com.armada.storeapp.data.model.response.OmniOrderItem
import com.armada.storeapp.data.model.response.PicklistResponseModel
import com.armada.storeapp.databinding.LayoutOmniOrderItemBinding


class OmniOrderAdapter(
    val orderList: ArrayList<OmnIOrderHeader>,
    val context: Context
) :
    RecyclerView.Adapter<OmniOrderAdapter.ViewHolder>() {

    var tapToScan: ((OmnIOrderHeader:OmnIOrderHeader) -> Unit)? = null
    var acceptPendingOrder: ((OmnIOrderHeader:OmnIOrderHeader,position:Int) -> Unit)? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val orderItem = orderList.get(position)
        holder.binding.tvOrderId.text = orderItem.orderNo
        holder.binding.tvOrderDate.text = orderItem.bdate_dmy
        holder.binding.tvAmount.text =
            orderItem.orderCurrencyGrandTotal.toString() + " " + orderItem.baseCurrency

        holder.binding.tvOrderedQty.text = "Ordered.Qty:" + orderItem.totalQty.toString()
        when (orderItem.status) {
            "Pending" -> {
                holder.binding.txtTaptoScan.visibility = View.GONE
                holder.binding.btnAccept.visibility = View.VISIBLE
            }
            "Accepted" -> {
                holder.binding.txtTaptoScan.visibility = View.VISIBLE
                holder.binding.btnAccept.visibility = View.GONE
            }
            "Delivered" -> {
                holder.binding.txtTaptoScan.visibility = View.GONE
                holder.binding.sideColorView.setBackgroundColor(context.resources.getColor(R.color.green))
            }
        }

        holder.binding.tvStatus.text = orderItem.status.uppercase()

        holder.binding.btnAccept.setOnClickListener {
            acceptPendingOrder?.invoke(orderItem,position)
        }

        holder.binding.txtTaptoScan.setOnClickListener {
            tapToScan?.invoke(orderItem)
        }
        holder.binding.root.setOnClickListener {
            tapToScan?.invoke(orderItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding =
            LayoutOmniOrderItemBinding.inflate(LayoutInflater.from(context), parent, false)

        return ViewHolder(viewBinding)
    }

    override fun getItemCount(): Int {
        if (orderList == null)
            return 0
        return orderList?.size!!
    }


    class ViewHolder(val binding: LayoutOmniOrderItemBinding) :
        RecyclerView.ViewHolder(binding.root)


}