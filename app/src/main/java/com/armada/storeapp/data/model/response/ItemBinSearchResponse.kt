package com.armada.storeapp.data.model.response

data class ItemBinSearchResponse(
    val binLogDetailsList: ArrayList<BinLogDetails>?,
    val businessDate: String?,
    val displayMessage: String?,
    val documentNo: Any?,
    val exceptionMessage: Any?,
    val iDs: Any?,
    val recordCount: Int?,
    val responseDynamicData: Any?,
    val stackTrace: Any?,
    val statusCode: Int?,
    val wmsiDs: Any?
) {
    data class BinLogDetails(
        val createBy: Any?,
        val createOn: Any?,
        val documentId: Int?,
        val fromBinCode: String?,
        val id: Int?,
        val quantity: Int?,
        val skuCode: String?,
        val toBinCode: Any?,
        val transactionName: Any?
    )
}