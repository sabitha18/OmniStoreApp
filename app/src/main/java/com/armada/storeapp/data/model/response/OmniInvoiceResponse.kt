package com.armada.storeapp.data.model.response

data class OmniInvoiceResponse(
    val businessDate: String,
    val displayMessage: String,
    val documentNo: Any,
    val exceptionMessage: Any,
    val grandToatl: Int,
    val iDs: Any,
    val omnI_OrderHeaderData: OmnIOrderHeaderData,
    val orderID: Any,
    val orderNo: Any,
    val recordCount: Int,
    val responseDynamicData: Any,
    val stackTrace: Any,
    val statusCode: Int,
    val wmsiDs: Any
)