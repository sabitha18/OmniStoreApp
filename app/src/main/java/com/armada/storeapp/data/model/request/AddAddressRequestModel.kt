package com.armada.storeapp.data.model.request

/**
 * Created by User999 on 3/9/2018.
 */

data class AddAddressRequestModel(
    val customer_id: String? = null,
    val firstname: String? = null,
    val lastname: String? = null,
    val telephone: String? = null,
    val apartment_number: String? = null,
    val floor_number: String? = null,
    val building_number: String? = null,
    val street: String? = null,
    val block: String? = null,
    val city: String? = null,
    val region: String? = null,
    val region_id: String? = null,
    val country_id: String? = null,
    val phone_code: String? = null,
    val is_default_billing: Int? = null,
    val is_default_shipping: Int? = null,
    val notes: String? = null,
    val postcode: String? = null,
    val prefix: String? = null,
    val area: String? = null,
    val mobile_number: String? = null,
    val jeddah: String? = null,
    val short_address: String? = "",
    val latitude: String? = "",
    val longitude: String? = "",
    val address_line_1: String? = "",
    val address_line_2: String? = ""
)