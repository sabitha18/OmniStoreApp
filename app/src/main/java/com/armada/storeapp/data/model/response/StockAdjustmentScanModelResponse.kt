package com.armada.storeapp.data.model.response

class StockAdjustmentScanModelResponse :
    ArrayList<StockAdjustmentScanModelResponse.StockAdjustmentScanModelResponseItem>() {
    data class StockAdjustmentScanModelResponseItem(
        var COUNT: Int?,
        var DIFF: Int?,
        var INSTOCK: Int?,
        val ITEMCODE: String?
    )
}