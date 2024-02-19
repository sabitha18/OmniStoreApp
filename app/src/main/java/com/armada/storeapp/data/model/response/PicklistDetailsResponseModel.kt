package com.armada.storeapp.data.model.response

data class PicklistDetailsResponseModel(
    val businessDate: String?,
    val displayMessage: Any?,
    val documentNo: Any?,
    val exceptionMessage: Any?,
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
        val documentNumber: Any?,
        val id: Int?,
        val invoiceDate: Any?,
        val invoiceDateConvert: String?,
        val invoiceNumber: Any?,
        val pickListDetails: ArrayList<PickDetails>?,
        val status: Boolean?,
        val totalItem: Int?,
        val type: Any?
    ) {
        data class PickDetails(
            val active: Boolean?,
            val destinationBin: String?,
            val documentNumber: Any?,
            val id: Int?,
            val invoiceNumber: String?,
            var isSkipped: Int?,
            val item: String?,
            val itemBarcode: String?,
            val pickListHeaderID: String?,
            val quantity: Int?,
            var scannedQty: Int?,
            val skippedQty: Int?,
            val sourceBin: String?,
            val status: Boolean?,
            var stockAvailInSourceBin: Int?,
            val storeCode: String?,
            val visualOrder: Int?,
            var newDestinationBin: String?,
            var newSourceBin: String?
        )
    }
}