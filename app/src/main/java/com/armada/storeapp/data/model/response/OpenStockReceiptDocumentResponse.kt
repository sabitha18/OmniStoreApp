package com.armada.storeapp.data.model.response

class OpenStockReceiptDocumentResponse :
    ArrayList<OpenStockReceiptDocumentResponse.OpenStockReceiptDocumentResponseItem>() {
    data class OpenStockReceiptDocumentResponseItem(
        val BARCODE: Int?,
        val BASELINE: Int?,
        val CQTY: Int?,
        val FLOCATION: String?,
        val FROMLOCATION: String?,
        val INTRANSITENABLELOC: String?,
        val ITEMCODE: String?,
        val ITEMNAME: String?,
        val ONHAND: Int?,
        val ORJFLOCATION: String?,
        var QUANTITY: Int?,
        val RQTY: Int?,
        val RSPRICE: Int?,
        val TAGID: Int?,
        val TOCOUNTRY: String?,
        val TOLOCATION: String?,
        val TXTDISDATE: String?,
        val TXTREFNO: String?,
        val TXTREMARKS: String?
    )
}