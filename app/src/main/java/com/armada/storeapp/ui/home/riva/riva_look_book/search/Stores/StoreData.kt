package com.armada.storeapp.ui.home.riva.riva_look_book.search.Stores

import java.io.Serializable

/**
 * Created by PC1 on 01-03-2018.
 */

data class StoreDataResponseModel(
    val success: Boolean? = null,
    val status: Int? = null,
    val message: String? = null,
    val `data`: ArrayList<StoreData>? = null
):Serializable

data class StoreData(
    val country_id: String? = null,
    val country_name: String? = null,
    val store_code: String? = null,
    val store_id: String? = null,
    val store_name: String? = null,
    val phone: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
	val address: String? = null,
	var distance:Float
) : Serializable