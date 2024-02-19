package com.armada.storeapp.data.model.request

data class SaveOmniOrderRequest(
    val businessdate: String?,
    val deliveryType: String?,
    val id: String?,
    val oMNI_OrderDetail: ArrayList<OMNIOrderDetailXX>?,
    val status: String?,
    val storeCode: String?,
    val storeID: Int?,
    val storeName: String?,
    val updateBy: Int?
)