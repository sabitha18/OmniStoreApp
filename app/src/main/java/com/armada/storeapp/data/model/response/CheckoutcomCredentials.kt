package com.armada.storeapp.data.model.response

import java.io.Serializable

data class CheckoutcomCredentials(
    val checkout_host: String,
    val fail_url: String,
    val public_key: String,
    val secret_key: String,
    val success_url: String
):Serializable