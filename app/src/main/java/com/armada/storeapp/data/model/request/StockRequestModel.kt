package com.armada.storeapp.data.model.request

import java.io.Serializable

/**
 * Created by User999 on 3/9/2018.
 */

data class StockRequestModel(
    val customer_id: String,
    var billing_address_id: String,
    var shipping_address_id: String,
//		val product_id: String,
//		val item_id: String,
//		val quantity: String,
//		var address_id: String,
//		var pickup_store_id :String,
		var payment_method:String
//		var shipping_method:String,
//		val lang: String,
//		val app_device_token:String,
//		val app_device_type:String,
//		val app_device_model:String,
//		val app_app_version:String,
//		val app_os_version:String,
//		val app_user_lang:String,
//		val app_user_country:String,
//		var civilid: String
) : Serializable