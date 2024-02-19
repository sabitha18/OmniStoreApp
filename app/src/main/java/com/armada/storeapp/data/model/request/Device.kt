package com.armada.storeapp.data.model.request

data class Device(
    val app_device_model: String,
    val app_device_token: String,
    val app_device_type: String,
    val app_os_version: String,
    val app_version: String
)