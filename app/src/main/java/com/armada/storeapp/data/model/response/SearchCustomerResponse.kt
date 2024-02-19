package com.armada.storeapp.data.model.response

data class SearchCustomerResponse(
    val businessDate: String,
    val customerMasterData: ArrayList<CustomerMasterData>,
    val displayMessage: String,
    val documentNo: Any,
    val exceptionMessage: Any,
    val grandToatl: Int,
    val iDs: Any,
    val orderID: Any,
    val orderNo: Any,
    val recordCount: Int,
    val responseDynamicData: ArrayList<ResponseDynamicData>,
    val stackTrace: Any,
    val statusCode: Int,
    val wmsiDs: Any
)