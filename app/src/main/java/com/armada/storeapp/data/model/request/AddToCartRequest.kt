package com.armada.storeapp.data.model.request

data class AddToCartRequest(
    val cart:CartData
){
    data class CartData(val customer_id: String? = null,
                        val product_id: String? = null,
                        val parent_id: String? = null,
                        val qty: String? = null,
                        val gift_data: String? = null)
//    "cart": {
//        "product_id": "85786",
//        "customer_id": "{{CUSTOMER_ID}}",
//        "qty": "1",
//        "parent_id": "85858"
//    }
}