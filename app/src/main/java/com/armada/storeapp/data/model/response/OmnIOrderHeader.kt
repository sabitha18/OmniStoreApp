package com.armada.storeapp.data.model.response

data class OmnIOrderHeader(
    val acceptedBy: Int,
    val acceptedOn: Any,
    val active: Boolean,
    val activityDateTime: String,
    val airWayBillNo: Any,
    val baseCurrency: Any,
    val baseCurrencyGrandTotal: Double,
    val bdate_dmy: String,
    val businessDate: String,
    val cashier: Any,
    val countryDetail: Any,
    val countryName: Any,
    val createBy: Int,
    val createOn: String,
    val customerCode: String,
    val customerMaster: Any,
    val customerMobileNo: Any,
    val customerName: String,
    val customername: Any,
    val declinedBy: Int,
    val declinedOn: Any,
    val declinedReasonCode: Any,
    val deliveredBy: Int,
    val deliveredOn: Any,
    val deliveryType: String,
    val documentNumberingID: Int,
    val documentTypeID: Int,
    val email: Any,
    val enablePaymentSend: Any,
    val enquiryID: String,
    val f_WarehouseCode: Any,
    val fromCountryDetail: Any,
    val fromStoreCode: String,
    val fromStoreDetail: Any,
    val fromStoreID: Int,
    val id: Int,
    val invoiceID: Any,
    val invoiceNo: String,
    val invoiceNumber: Any,
    val locationType: Any,
    val magento_order_id: Any,
    val omnI_CashPayment: Any,
    val omnI_OrderActivity: Any,
    val omnI_OrderActivity_withtracking: Any,
    val omnI_OrderDetail: Any,
    val omni_InvoiceHeader: Any,
    val orderCurrency: Any,
    val orderCurrencyGrandTotal: Double,
    val orderHeaderID: Int,
    val orderID: Int,
    val orderNo: String,
    val orderType: Any,
    val order_id: Any,
    val otherReasonRemarks: Any,
    val packingStatus: Any,
    val paymentStatus: Any,
    val paymentWithHandover: Boolean,
    val phoneNumber: String,
    val pickupDate: String,
    val pickupDate1: String,
    val pickupTimeSlot: String,
    val placeOrder: Any,
    val placeOrderWithPayment: Any,
    val reason: Any,
    val referenceNo: Any,
    val reference_No: Any,
    val remarks: Any,
    val returnOrderNo: Any,
    val rrOrderID: Int,
    val rrOrderNo: Any,
    val runningNo: Int,
    val salesEmployeeCode: Any,
    val salesEmployeeID: Int,
    val salesEmployeeName: Any,
    val shipmentID: Int,
    val shipmentType: String,
    val shippingAddress1: String,
    val shippingAddress2: Any,
    val shippingArea: String,
    val shippingBlock: String,
    val shippingCity: String,
    val shippingCountryCode: String,
    val shippingDate: Any,
    val shippingPhoneNumber: Any,
    val shippingStateName: String,
    val shippingStreet: String,
    val skipStockCheck: Boolean,
    val skuCode: Any,
    val status: String,
    val storeCode: Any,
    val storeDetail: Any,
    val storeID: Int,
    val storeImage: Any,
    val storeName: String,
    val systemDateTime: String,
    val taxCode: Any,
    val timeSlot: Any,
    val toStoreCode: String,
    val toStoreID: Int,
    val toStoreName: String,
    val toWarehouseCode: Any,
    val toWarehouseId: Int,
    val totalDiscountAmount: Double,
    val totalQty: Int,
    val towarehouseName: Any,
    val transferReqList: Any,
    val transferRequest: Any,
    val transporterID: Int,
    val transporterType_StatusMapping: Any,
    val transporter_VariableMapping: Any,
    val type: Any,
    val updateBy: Int,
    val updateOn: String,
    val userCode: Any,
    val warehouseCode: String,
    val warehouseCountryName: Any,
    val warehouseFullFillment: Int,
    val warehouseID: Int,
    val warehouseName: String
)