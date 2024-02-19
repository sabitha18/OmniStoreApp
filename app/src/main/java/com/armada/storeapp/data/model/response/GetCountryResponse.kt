package com.armada.storeapp.data.model.response

data class GetCountryResponse(
    val businessDate: String,
    val countryMasterList: ArrayList<CountryMaster>,
    val displayMessage: Any,
    val documentNo: Any,
    val exceptionMessage: Any,
    val grandToatl: Int,
    val iDs: Any,
    val orderID: Any,
    val orderNo: Any,
    val recordCount: Int,
    val responseDynamicData: Any,
    val stackTrace: Any,
    val statusCode: Int,
    val wmsiDs: Any
)