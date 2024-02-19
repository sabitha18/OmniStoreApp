package com.armada.storeapp.data.model.response

data class GetCustomerCodeResponse(
    val businessDate: String,
    val displayMessage: Any,
    val documentNo: String,
    val documentNumberingBillNoDetailsRecord: DocumentNumberingBillNoDetailsRecord,
    val exceptionMessage: Any,
    val grandToatl: Double,
    val iDs: Any,
    val orderID: Any,
    val orderNo: Any,
    val recordCount: Int,
    val responseDynamicData: Any,
    val stackTrace: Any,
    val statusCode: Int,
    val wmsiDs: Any
)