package com.armada.storeapp.data.model.response

data class PicklistResponseModel(
    val businessDate: String?,
    val displayMessage: String?,
    val documentNo: Any?,
    val exceptionMessage: String?,
    val iDs: Any?,
    val pickListHeader: ArrayList<PickHeader>?,
    val recordCount: Int?,
    val responseDynamicData: Any?,
    val stackTrace: Any?,
    val statusCode: Int?,
    val wmsiDs: Any?
) {
    data class PickHeader(
        val active: Boolean?,
        val documentNumber: String?,
        val id: Int?,
        val invoiceDate: String?,
        val invoiceDateConvert: String?,
        val invoiceNumber: String?,
        var pickListDetails: ArrayList<PickDetails>?,
        val status: Boolean?,
        val totalItem: Int?,
        val type: String?
    ) {
        data class PickDetails(
            val active: Boolean?,
            var destinationBin: String?,
            val documentNumber: Any?,
            val id: Int?,
            val invoiceNumber: String?,
            val item: String?,
            val itemBarcode: String?,
            val pickListHeaderID: String?,
            var quantity: Int?,
            var scannedQty: Int,
            var sourceBin: String?,
            val status: Boolean?,
            val storeCode: String?,
            val skippedQty: Int,
            var stockAvailInSourceBin: Int,
            val isSkipped: Int,
            var newSourceBin: String?,
            var newDestinationBin: String?,
            val visualOrder: Int?
        )
    }
}
