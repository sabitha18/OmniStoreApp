package com.armada.storeapp.data.model.request

data class CommonBinTransferRequest(
    val BinLogList: List<BinLog?>?,
    val UserId: Int?,
    val storeCode: String?
) {
    data class BinLog(
        val ItemCode: String?,
        var Quantity: Int?,
        var binCode: String?
    )
}