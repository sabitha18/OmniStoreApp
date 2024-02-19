package com.armada.storeapp.data.model.response

data class TimeSlot(
    val active: Boolean,
    val appVersion: Any,
    val createBy: Int,
    val createOn: String,
    val createdByUserName: Any,
    val fromTime: String,
    val id: Int,
    val isCountrySync: Boolean,
    val isServerSync: Boolean,
    val isStoreSync: Boolean,
    val scn: Any,
    val timeSlotName: String,
    val toTime: String,
    val updateBy: Int,
    val updateOn: String,
    val updatedByUserName: Any
)