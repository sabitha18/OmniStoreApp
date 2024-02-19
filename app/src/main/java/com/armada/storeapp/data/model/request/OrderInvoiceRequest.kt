package com.armada.storeapp.data.model.request

data class OrderInvoiceRequest(
    val active: Boolean,
    val baseCurrency: String,
    val city: String,
    val countryID: String,
    val deliveryType: String,
    val fromStoreCode: String,
    val fromStoreid: Int,
    val oMNI_OrderDetail: List<OMNIOrderDetailX>,
    val orderCurrency: String,
    val pickUpTimeSlot: String,
    val pickupDate: String,
    val stateID: String
)