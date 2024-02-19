package com.armada.storeapp.data.model.response

class StockReceiptDocumentResponseModel :
    ArrayList<StockReceiptDocumentResponseModel.StockReceiptDocumentResponseModelItem>() {
    data class StockReceiptDocumentResponseModelItem(
        val DOCDATE: String?,
        val FRMLOC: String?,
        val ID: Int?,
        val ORJWAN_NO: Int?,
        val QTY: Int?,
        val REMARKS: String?,
        val REQBY: String?,
        val STATUS: String?,
        val TOLOC: String?,
        val TRNSNO: String?,
        val TRNSQTY: Int?
    )
}