package com.armada.storeapp.data.model.request

data class PicklistTransferRequest(
    val binLogList: List<BinLog>?,
    val businessDate: String?,
    val storeCode: String?,
    val userId: String?
) {
    data class BinLog(
        val binCode: String?,
        val fromBinCode: String?,
        val originalSourceBin: String?,
        val orginalDestinationBin: String?,
        val id: Int?,
        val isSkipped: Int?,
        val quantity: String?,
        val remarks: String?,
        val skuCode: String?,
        val skippedQty: Int
    )
}