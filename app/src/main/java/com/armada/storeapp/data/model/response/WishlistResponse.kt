package com.armada.storeapp.data.model.response

/**
 * Created by PC1 on 06-03-2018.
 */

data class WishlistResponse(
    val status: String? = null,
    val success: String? = null,
    val message: String? = null,
    val `data`: ArrayList<WishlistItem>? = null
)

data class WishlistItem(
    val wishlist_item_id: String? = null,
    val product_id: String? = null,
    val description: String? = null,
    val name: String? = null,
    val image_url: String? = null,
    var regular_price: String? = null,
    var final_price: String? = null,
    var is_saleable: String? = null,
    var qty: Int? = 1,
    val product_type: String? = null,
    val qty_available: Int? = 12,
    val product: Product? = null,
    val store_id: String? = null,
    val wishlist_id: String? = null,
    val added_at: String? = null,
)

data class Product(
    val barcode: Any? = null,
    val brand: String? = null,
    val configurable_option: List<ConfigurableOption>? = null,
    val description: String? = null,
    val enable_special_text: String? = null,
    val final_price: String? = null,
    val has_options: Int? = null,
    val id: String? = null,
    val image: String? = null,
    val is_salable: Boolean? = null,
    val name: String? = null,
    val options: List<Any>? = null,
    val price: String? = null,
    val remaining_qty: Int? = null,
    val sale_img: Any? = null,
    val sale_img_h: Any? = null,
    val sale_img_w: Any? = null,
    val short_description: String? = null,
    val sku: String? = null,
    val special_text: Any? = null,
    val type: String? = null
)




