package com.armada.storeapp.data.model.response

data class DocumentNumberingBillNoDetailsRecord(
    val active: Boolean,
    val appVersion: Any,
    val createBy: Any,
    val createOn: Any,
    val createdByUserName: Any,
    val detailID: Int,
    val docNumID: Int,
    val documentName: Any,
    val endDate: String,
    val endNumber: Int,
    val id: Int,
    val isCountrySync: Boolean,
    val isServerSync: Boolean,
    val isStoreSync: Boolean,
    val numberOfCharacter: Int,
    val prefix: String,
    val runningNo: Int,
    val scn: Any,
    val startDate: String,
    val startNumber: Int,
    val suffix: Any,
    val updateBy: Any,
    val updateOn: Any,
    val updatedByUserName: Any
)