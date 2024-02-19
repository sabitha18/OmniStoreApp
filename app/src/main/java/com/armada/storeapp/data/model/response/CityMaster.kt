package com.armada.storeapp.data.model.response

data class CityMaster(
    val active: Boolean,
    val appVersion: Any,
    val cityCode: String,
    val cityName: String,
    val countryID: Int,
    val createBy: Any,
    val createOn: Any,
    val createdByUserName: Any,
    val id: Int,
    val isCountrySync: Boolean,
    val isServerSync: Boolean,
    val isStoreSync: Boolean,
    val remarks: Any,
    val scn: Any,
    val stateID: Int,
    val stateName: Any,
    val updateBy: Any,
    val updateOn: Any,
    val updatedByUserName: Any
)