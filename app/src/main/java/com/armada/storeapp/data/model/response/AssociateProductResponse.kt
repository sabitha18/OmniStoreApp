package com.armada.storeapp.data.model.response

import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing.model.AssociateProductModel

data class AssociateProductResponse(
    val data: HashMap<String, Data>,
    val message: String?,
    val status: Int?
) {
    data class Data(
        val sku: String,
        val associated: HashMap<String, AssociateProductModel>
    )
}