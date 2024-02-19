package com.armada.storeapp.data.model.response

class OpenDocumentResponseModel :
    ArrayList<OpenDocumentResponseModel.OpenDocumentResponseModelItem>() {
    data class OpenDocumentResponseModelItem(
        val AVAI_QTY: Int?,
        val CREATED_DATE: String?,
        val DRIVER_NAME: Any?,
        val FROM_LOCATION: String?,
        val ID: Int?,
        val ITEM_CODE: String?,
        val LOCATION_CODE: String?,
        val ORDER_NO: String?,
        val ORDER_QTY: Int?,
        val ORDER_REFNO: String?,
        val ORDER_STAT: String?,
        val ORD_STAT: Int?,
        var PICK_QTY: Int?,
        val POS_QTY: Int?,
        val POS_QTY1: Int?,
        val POS_REF_NO: Any?,
        val RECVE_QTY: Int?,
        val REQ_QTY: Int?,
        var USER_REMARKS: String?,
        val WH_CODE: String?
    )
}