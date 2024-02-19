package com.armada.storeapp.data.model.response

data class OmniStockResponse(
    val businessDate: String,
    val displayMessage: Any,
    val documentNo: Any,
    val exceptionMessage: Any,
    val grandToatl: Int,
    val iDs: Any,
    val omniStoreStockCheckResponse: List<OmniStoreStockCheckResponse>,
    val orderID: Any,
    val orderNo: Any,
    val recordCount: Int,
    val responseDynamicData: Any,
    val stackTrace: Any,
    val statusCode: Int,
    val wmsiDs: Any
)