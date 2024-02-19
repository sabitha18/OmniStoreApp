package com.armada.storeapp.data.model.response

data class PickListItem(
    val id: Int,
    val skuCode: String,
    val fromBinCode: String,
    val binCode: String,
    val remarks: String,
    val quantity: String,
    val createBy: String?,
    val updateBy: String?
)
