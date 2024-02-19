package com.armada.storeapp.data.model.request

data class CompletePickRequest(
    val ItemList: List<Item>?
) {
    data class Item(
        val From_Location: String?,
        val Id: String?,
        val Location_code: String?,
        val Qty: String?,
        val RefNo: String?,
        val Remark: String?,
        val User_code: String?,
        val Wh_code: String?,
        val itemCode: String?,
        val Bin_code: String?
    )
}