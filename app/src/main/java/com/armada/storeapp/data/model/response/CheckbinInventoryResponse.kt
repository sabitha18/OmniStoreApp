package com.armada.storeapp.data.model.response

data class CheckbinInventoryResponse(
    val binLogList: ArrayList<BinLog>?,
    val binQuantity: Int?,
    val businessDate: String?,
    val displayMessage: String?,
    val documentNo: Any?,
    val exceptionMessage: Any?,
    val iDs: Any?,
    val recordCount: Int?,
    val responseDynamicData: Any?,
    val skuCode: String?,
    val stackTrace: Any?,
    val statusCode: Int?,
    val totalQuantity: Int?,
    val transferQuantity: Int?,
    val wmsiDs: Any?
) {
    data class BinLog(
        val active: Boolean?,
        val appVersion: Any?,
        val binLogList: Any?,
        val businessDate: Any?,
        val createBy: Any?,
        val createOn: Any?,
        val createdByUserName: Any?,
        val fromBinCode: String?,
        val isCountrySync: Boolean?,
        val isServerSync: Boolean?,
        val isStoreSync: Boolean?,
        val quantity: String?,
        val scn: Any?,
        val skuCode: String?,
        val status: Any?,
        val storeCode: Any?,
        val toBinCode: Any?,
        val updateBy: Any?,
        val updateOn: Any?,
        val updatedByUserName: Any?
    )
}