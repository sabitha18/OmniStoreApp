package com.armada.storeapp.data.model.request

import com.armada.storeapp.data.model.response.OmniInvoiceHeader

data class OmniOrderPlaceRequest(
    val active: Boolean,
    val remarks:String,
    val baseCurrency: String,
    val city: String,
    val countryID: String,
    val customerCode: String,
    val deliveryType: String,
    val enablePaymentSend: String,
    val fromStoreCode: String,
    val fromStoreid: Int,
    val oMNI_OrderDetail: List<OMNIOrderDetail>,
    var omni_InvoiceHeader: PlaceOrderInvoiceHeader,
    val orderCurrency: String,
    val pickUpTimeSlot: String,
    val pickupDate: String,
    val salesEmployeeCode: String,
    val salesEmployeeID: Int,
    val shipmentType: String,
    val stateID: String,
    val storeName: String,
    val toStoreCode: String,
    val toStoreid: Int,
    val transferReqList: List<TransferReq>,
    val userCode: String,
    val warehouseCode: String,
    val warehouseID: Int,

    val wareHouseFullFillment: Int,
    val placeOrderWithPayment: String,
    val PayByCash: Double,
    val payByCard: Double,
    val approvalNo: String




)