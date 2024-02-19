package com.armada.storeapp.data.model.request

data class SkipPicklistTransferRequest(
    val destinationBin: String?,
    val itemCode: String?,
    val otherReasons: String?,
    val pickListId: Int?,
    val reasonCode: String?,
    val skippedBy: Int?,
    val sourceBin: String?
)