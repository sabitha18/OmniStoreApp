package com.armada.storeapp.data.model.response

data class ValidateBinResponse(
    val binDetailsList: ArrayList<BinDetails>?,
    val businessDate: String?,
    val displayMessage: String?,
    val documentNo: Any?,
    val exceptionMessage: Any?,
    val iDs: Any?,
    val recordCount: Int?,
    val responseDynamicData: Any?,
    val stackTrace: Any?,
    val statusCode: Int?,
    val wmsiDs: Any?
) {
    data class BinDetails(
        val active: Boolean?,
        val barCode: Any?,
        val binCode: String?,
        val binID: Int?,
        val binSubLevelCode: Any?,
        val createBy: Int?,
        val createOn: String?,
        val documentDate: String?,
        val fromBinCode: Any?,
        val id: Int?,
        val quantity: Int?,
        val remarks: Any?,
        val rfid: Any?,
        val skuCode: String?,
        val status: Any?,
        val storeCode: String?,
        val storeID: Int?,
        val updateBy: Int?,
        val updateOn: String?
    )
}