package com.armada.storeapp.data.model.response

import java.io.Serializable

data class ProductDetailsResponse(
    val `data`: ProductDetailsData,
    val message: String,
    val status: Int
) : Serializable

data class ProductDetailsData(
    val attribute_set_id: String,
    val brand: String,
    val bundle_options: ArrayList<Any>,
    val configurable_option: ArrayList<ProductDetailsConfigurableOption>,
    val created_at: String,
    val current_store: String,
    val current_store_id: String,
    val description: String,
    val discount_percentage: String,
    val final_price: String,
    val has_options: String,
    val how_to_use: Any,
    var id: String,
    var image: String,
    val images: ArrayList<String>,
    val is_in_stock: Boolean,
    val is_salable: Boolean,
    val manufacturer: Any,
    val meta_description: Any,
    val meta_keyword: Any,
    val meta_title: Any,
    val name: String,
    val options: ArrayList<Any>,
    val price: String,
    val related: ArrayList<Related>,
    var remaining_qty: Int,
    val shipping_amount: String,
    val shipping_messsage: String,
    val short_description: String,
    val show_sale_badge: Int,
    val size_chart: String,
    val sku: String,
    val status: String,
    val type: String,
    val updated_at: String,
    val upsell: ArrayList<Related>,
    val video_url: String,
    val weight: String,
    val wishlist_item_id: Int,
    var is_wishlist: Boolean? = false,
    var dbOptions :  String? = null,
) : Serializable

data class Related(
    val barcode: Any,
    val brand: String,
    val configurable_option: ArrayList<ProductDetailsConfigurableOption>,
    val description: String,
    val enable_special_text: String,
    val final_price: String,
    val has_options: Int,
    var id: String,
    var image: String,
    var is_salable: Boolean,
    val name: String,
    val options: List<Any>,
    var price: String,
    var remaining_qty: Int,
    val sale_img: Any,
    val sale_img_h: Any,
    val sale_img_w: Any,
    val short_description: String,
    val sku: String,
    val special_text: Any,
    val type: String,
    val wishlist_item_id: Int,
    var is_wishlist: Boolean? = false,
    var is_added_to_cart:Boolean =false
) : Serializable

data class ProductDetailsConfigurableOption(
    val attribute_code: String,
    val attribute_id: Int,
    val attributes: ArrayList<ProductDetailsAttribute>,
    val type: String,
    var is_last: String? = null
) : Serializable

data class ConfigurableOption(
    val attribute_code: String,
    val attribute_id: Int,
    val attributes: ArrayList<Attribute>,
    val type: String
) : Serializable

data class ProductDetailsAttribute(
    val attribute_image_url: String,
    val color_code: String,
    val images: ArrayList<String>,
    val option_id: String,
    val price: String,
    var should_select: Boolean,
    val value: String,
    var isSelected: Boolean? = false,
    var isAvailable: Boolean? = true
) : Serializable

data class Attribute(
    val attribute_image_url: String,
    val color_code: String,
    val option_id: String,
    val value: String
) : Serializable