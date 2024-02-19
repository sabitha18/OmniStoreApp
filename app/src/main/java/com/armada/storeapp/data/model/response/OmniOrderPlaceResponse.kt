package com.armada.storeapp.data.model.response

data class OmniOrderPlaceResponse(
    val businessDate: String,
    val displayMessage: String,
    val documentNo: Any,
    val eCommerceOrderResponse: Any,
    val exceptionMessage: Any,
    val grandToatl: Double,
    val iDs: String,
    val omni_InvoiceSaveResponse: OmniInvoiceSaveResponse,
    val orderID: String,
    val orderNo: String,
    val recordCount: Int,
    val responseDynamicData: Any,
    val stackTrace: Any,
    val statusCode: Int,
    val wmsiDs: Any
)