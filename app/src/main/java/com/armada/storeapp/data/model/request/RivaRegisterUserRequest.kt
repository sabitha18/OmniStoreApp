package com.armada.storeapp.data.model.request

data class RivaRegisterUserRequest(
    val device: Device,
    val dob: String,
    val email: String,
    val firstname: String,
    val gender: String,
    val is_guest: String,
    val lastname: String,
    val mobile_number: String,
    val newsletter_subscription: String,
    val password: String,
    val phone_code: String,
    val prefix: String
)