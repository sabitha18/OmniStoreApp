package com.armada.storeapp.data.model.response

data class StockReturnItemScanResponse(
    val ItemCode: String?,
    var Qty: String?,
    val cquantity: String?,
    val itemname: String?,
    val onhand: String?,
    val rquantity: String?,
    val rsprice: String?,
    var strBarCode: String?,
    var binCode: String?
)