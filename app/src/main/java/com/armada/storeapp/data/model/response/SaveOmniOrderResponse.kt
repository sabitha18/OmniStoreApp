package com.armada.storeapp.data.model.response

data class SaveOmniOrderResponse(
    val businessDate: String,
    val displayMessage: String,
    val documentNo: Any,
    val eCommerceOrderResponse: Any,
    val exceptionMessage: Any,
    val grandToatl: Int,
    val iDs: Any,
    val langCode: Any,
    val omni_InvoiceSaveResponse: Any,
    val orderID: Any,
    val orderNo: Any,
    val recordCount: Int,
    val responseDynamicData: Any,
    val stackTrace: Any,
    val statusCode: Int,
    val wmsiDs: Any
)