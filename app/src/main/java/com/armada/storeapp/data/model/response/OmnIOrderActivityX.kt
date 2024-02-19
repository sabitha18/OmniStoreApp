package com.armada.storeapp.data.model.response

data class OmnIOrderActivityX(
    val act_Time: String,
    val activity: String,
    val activityCode: Any,
    val activityDate: Any,
    val activityDateTime: String,
    val activityRemarks: Any,
    val areaCode: Any,
    val areaName: Any,
    val authorizationID: String,
    val comments: Any,
    val createTime: String,
    val description: String,
    val employeeName: String,
    val fromLocation: String,
    val fromStoreCode: String,
    val id: Int,
    val lineNo: Int,
    val magentoOrderID: String,
    val orderID: Int,
    val orderNo: String,
    val paymentDetails: Any,
    val paymentID: String,
    val paymentMethod: String,
    val paymentOrder: String,
    val paymentTransaction: String,
    val refundID: String,
    val refundResponseMsg: String,
    val responseMsg: String,
    val returnDocumentNo: String,
    val shipmentID: Int,
    val skuCode: String,
    val sortID: Int,
    val sortID1: Int,
    val status: String,
    val storeCode: Any,
    val toLocation: String,
    val toStoreCode: String,
    val totalReturnQty: Int,
    val track: String,
    val transactionID: Any,
    val transporterID: Int,
    val transporterName: String,
    val userCode: String,
    val userID: Any,
    val userId: Int,
    val userName: String,
    val wayBillNo: String,
    val wayBillNumber: Any
)