package com.armada.storeapp.data.model.response

data class OmniOrdersResponse(
    val businessDate: String,
    val displayMessage: String,
    val documentNo: Any,
    val exceptionMessage: String,
    val grandToatl: Double,
    val iDs: Any,
    val omnI_CustomerCityNamePorter: Any,
    val omnI_CustomerStateNamePorter: Any,
    val omnI_OrderDetail: Any,
    val omnI_OrderHeaderList: ArrayList<OmnIOrderHeader>,
    val omnI_StoreAddressList: Any,
    val omnI_StoreCityNamePorter: Any,
    val omnI_StoreStateNamePorter: Any,
    val orderID: Any,
    val orderNo: Any,
    val recordCount: Int,
    val responseDynamicData: Any,
    val skuMasterList: Any,
    val stackTrace: Any,
    val statusCode: Int,
    val wmsiDs: Any
)