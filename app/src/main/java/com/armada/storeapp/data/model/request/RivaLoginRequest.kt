package com.armada.storeapp.data.model.request

data class RivaLoginRequest(
    val username: String,
    val password: String,
    val device: Device
)