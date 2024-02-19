package com.armada.storeapp.data.model.request

/**
 * Created by User999 on 7/18/2018.
 */

data class UpdateSizeRequest(
	val customer_id: String? = null,
	val item_id: String? = null,
	val new_product_id: String? = null,
	val parent_id: String? = null,
	val qty: String? = null
)