package com.armada.storeapp.ui.home.instore_transactions.picklist.listener

import com.armada.storeapp.data.model.response.CreatePicklistSkuResponse

interface DestinationBinSelectedListener {

    fun onDestinationBinSelected(item: CreatePicklistSkuResponse.PickDetails, position: Int)
}