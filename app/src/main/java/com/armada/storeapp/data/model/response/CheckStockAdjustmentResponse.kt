package com.armada.storeapp.data.model.response

data class CheckStockAdjustmentResponse(
    val businessDate: String?,
    val displayMessage: Any?,
    val documentNo: Any?,
    val exceptionMessage: Any?,
    val iDs: Any?,
    val pickListDetails: List<PickDetails?>?,
    val putAwayDetails: Any?,
    val recordCount: Int?,
    val responseDynamicData: Any?,
    val stackTrace: Any?,
    val statusCode: Int?,
    val wmsiDs: Any?
) {
    data class PickDetails(
        val active: Boolean?,
        val destinationBin: Any?,
        val documentNumber: Any?,
        val id: Int?,
        val invoiceNumber: Any?,
        val isSkipped: Int?,
        val item: String?,
        val itemBarcode: Any?,
        val pickListHeaderID: Any?,
        val quantity: Int?,
        val skippedQty: Int?,
        val sourceBin: String?,
        val status: Boolean?,
        val stockAvailInSourceBin: Int?,
        val storeCode: Any?,
        val visualOrder: Int?
    )
}