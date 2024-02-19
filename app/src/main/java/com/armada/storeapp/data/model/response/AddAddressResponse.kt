package com.armada.storeapp.data.model.response

import java.io.Serializable

/**
 * Created by User999 on 3/7/2018.
 */
data class AddAddressResponseModel(
    val `data`: ArrayList<AddAddressDataModel?>? = null,
    val message: String? = null,
    val status: Int? = null
):Serializable

data class AddAddressDataModel(
    val address_id: String? = null,
    val address_line_1: String? = null,
    val address_line_2: String? = null,
    val address_type: String? = null,
    val apartment_number: String? = null,
    val block: String? = null,
    val building_number: String? = null,
    val city: String? = null,
    val city_id: Int? = null,
    val country: String? = null,
    val country_id: String? = null,
    val firstname: String? = null,
    val floor_number: String? = null,
    val is_default_billing: Int? = null,
    val is_default_shipping: Int? = null,
    val jeddah: String? = null,
    val lastname: String? = null,
    val notes: String? = null,
    val phone_code: String? = null,
    val postcode: String? = null,
    val region: String? = null,
    val region_id: String? = null,
    val street: String? = null,
    val telephone: String? = null,
    val mobile_number: String? = null,
    val area: String? = null,
    var email:String? =null
): Serializable


