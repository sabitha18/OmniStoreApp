package com.armada.storeapp.ui.home.riva.riva_look_book.search.BarcodeScanner

import com.armada.storeapp.data.model.response.ProductDetailsData

/**
 * Created by User999 on 3/14/2018.
 */

data class BarcodeResponseModel(
		val success: String,
		val status: Int,
		val message: String,
		val data: Data
) {
	data class Data(
			val products: List<ProductDetailsData>
	) {
	}
}