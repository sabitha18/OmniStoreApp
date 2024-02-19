package com.armada.storeapp.ui.home.instore_transactions.picklist.listener

import com.armada.storeapp.data.model.response.CreatePicklistSkuResponse

interface ItemQuantityListener {

    fun onItemQtyChanged(item: CreatePicklistSkuResponse.PickDetails, position: Int)
}