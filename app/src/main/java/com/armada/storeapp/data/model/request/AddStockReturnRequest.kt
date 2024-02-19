package com.armada.storeapp.data.model.request

data class AddStockReturnRequest(
    val TransferList: List<Transfer>?
) {
    data class Transfer(
        val Barcode: String?,
        var Quantity: String?,
        val itemcode: String?,
        val itemname: String?,
        var BinCode: String?
    )
}