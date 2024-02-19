package com.armada.storeapp.data.model.response

class AddStockAdjustmentResponse :
    ArrayList<AddStockAdjustmentResponse.AddStockAdjustmentResponseItem>() {
    data class AddStockAdjustmentResponseItem(
        val Success_Message: String?
    )
}