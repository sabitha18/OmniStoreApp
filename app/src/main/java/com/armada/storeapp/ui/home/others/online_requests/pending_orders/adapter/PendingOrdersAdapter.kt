package com.armada.storeapp.ui.home.others.online_requests.pending_orders.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.armada.storeapp.data.model.response.ShopPickOrdersResponseModel
import com.armada.storeapp.databinding.ItemShopPickPendingOrderBinding

class PendingOrdersAdapter(
    val items: ArrayList<ShopPickOrdersResponseModel.ShopPickOrdersResponseModelItem>,
    val context: Context, listener: DocumentOpenListener
) :
    RecyclerView.Adapter<PendingOrdersAdapter.ViewHolder>() {

    var pendingOrdersList: ArrayList<ShopPickOrdersResponseModel.ShopPickOrdersResponseModelItem>
    val documentOpenListener = listener

    init {
        this.pendingOrdersList = items
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvDocumentNo.text = pendingOrdersList.get(position).ORDER_REFNO
        holder.binding.tvOrderQty.text = pendingOrdersList.get(position).ORDER_QTY.toString()
        holder.binding.bgtnOpen.setOnClickListener {
            documentOpenListener.openDocument(pendingOrdersList.get(position))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding =
            ItemShopPickPendingOrderBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(viewBinding)
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
//        return items.size
        return pendingOrdersList!!.size;
    }


    class ViewHolder(val binding: ItemShopPickPendingOrderBinding) :
        RecyclerView.ViewHolder(binding.root)


}