package com.armada.storeapp.data.model.request

data class DeliverOmniOrderRequest(
    val createBy: Int,
    val id: String,
    val oMNI_OrderDetail: ArrayList<OMNIOrderDetailXXX>,
    val orderNo: String,
    val storeCode: String,
    val toStoreCode: String
)