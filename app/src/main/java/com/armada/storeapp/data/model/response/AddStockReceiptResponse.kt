package com.armada.storeapp.data.model.response

class AddStockReceiptResponse : ArrayList<AddStockReceiptResponse.AddStockReceiptResponseItem>() {
    data class AddStockReceiptResponseItem(
        val Success_Message: String?
    )
}