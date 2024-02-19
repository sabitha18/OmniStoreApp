package com.armada.storeapp.data.model.request

data class ManualPicklistRequest(
    val PickListDetails: List<PickDetails?>?,
    val storeCode: String?,
    val userId: Int?
) {
    data class PickDetails(
        var Quantity: String?,
        val destinationBin: String?,
        val item: String?,
        val sourceBin: String?
    )
}