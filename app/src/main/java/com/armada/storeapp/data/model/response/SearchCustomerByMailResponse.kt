package com.armada.storeapp.data.model.response

data class SearchCustomerByMailResponse(
    val businessDate: String,
    val displayMessage: String,
    val documentNo: Any,
    val exceptionMessage: Any,
    val grandToatl: Double,
    val iDs: Any,
    val omniCustomerMasterType: ArrayList<CustomerMasterData>,
    val orderID: Any,
    val orderNo: Any,
    val recordCount: Int,
    val responseDynamicData: Any,
    val stackTrace: Any,
    val statusCode: Int,
    val wmsiDs: Any
)