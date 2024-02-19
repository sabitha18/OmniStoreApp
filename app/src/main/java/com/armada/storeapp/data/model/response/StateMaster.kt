package com.armada.storeapp.data.model.response

data class StateMaster(
    val active: Boolean,
    val appVersion: Any,
    val countryID: Int,
    val countryName: Any,
    val createBy: Any,
    val createOn: Any,
    val createdByUserName: Any,
    val id: Int,
    val isCountrySync: Boolean,
    val isServerSync: Boolean,
    val isStoreSync: Boolean,
    val remarks: Any,
    val scn: Any,
    val stateCode: String,
    val stateName: String,
    val updateBy: Any,
    val updateOn: Any,
    val updatedByUserName: Any
)