package com.armada.storeapp.ui.home.instore_transactions.picklist.dialog

import com.armada.storeapp.data.model.response.SkipReasonListResponse

interface ReasonInterface {
    fun OnReasonSelected(selectedReason: SkipReasonListResponse.SkipReasons)
}