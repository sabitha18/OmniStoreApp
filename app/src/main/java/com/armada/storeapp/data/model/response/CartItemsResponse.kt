package com.armada.storeapp.data.model.response

data class CartItemsResponse(
    val billing_address: ArrayList<BillingAddres>,
    val cart_reward_points: CartRewardPoints,
    val free_shipping_amt: String,
    val items: ArrayList<CartItemModel>,
    val message: String,
    val shipping_address: ArrayList<ShippingAddres>,
    val shipping_message: String,
    val status: Int,
    val totals: Totals
){
    data class CartItemModel(
        val item_id: String? = null,
        val qty: Int? = null,
        val id: String? = null,
        val parent_id: String? = null,
        val sku: String? = null,
        val type: String? = null,
        val name: String? = null,
        val brand: String? = null,
        val price: String? = null,
        val final_price: String? = null,
        val description: String? = null,
        val short_description: String? = null,
        val image: String? = null,
        val is_salable: Boolean? = null,
        val has_options: Int? = null,
        val options: ArrayList<Any>? = null,
        val configurable_option: ArrayList<CartConfigurableOption>? = null,
        val remaining_qty: Int? = null
    )

    data class CartConfigurableOption(
        val attribute_id: Int? = null,
        val type: String? = null,
        val attribute_code: String? = null,
        val attributes: CartAttributes? = null
    )

    data class CartAttributes(
        val value: String? = null,
        val option_id: String? = null,
        val attribute_image_url: String? = null,
        val price: String? = null,
        val images: List<String>? = null
    )

}