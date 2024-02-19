package com.armada.storeapp.data.model.request

data class AddStockReceiptRequest(
    val Receiptlst: ArrayList<Item>?
) {
    data class Item(
        val Barcode: String?,
        val Quantity: String?,
        val itemcode: String?,
        val itemname: String?,
        val rquantity: String?
    )
}