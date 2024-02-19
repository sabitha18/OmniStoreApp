package com.armada.storeapp.ui.home.others.online_requests.pending_orders.adapter

import com.armada.storeapp.data.model.response.ShopPickOrdersResponseModel

interface DocumentOpenListener {
    fun openDocument(order: ShopPickOrdersResponseModel.ShopPickOrdersResponseModelItem)
}