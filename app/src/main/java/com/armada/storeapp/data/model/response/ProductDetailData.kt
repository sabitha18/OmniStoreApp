//package com.armada.storeapp.data.model.response
//
//import java.io.Serializable
////val match_look: List<ProductDetailData.MatchLook>? = null,
//data class ProductDetailData(
//    var entity_id: String? = null,
//    val type_id: String? = null,
//    val prod_type_id: String? = null,
//    val sku: String? = null,
//    val name: String? = null,
//    val meta_title: String? = null,
//    val meta_description: String? = null,
//    val rating: String? = null,
//    val description: String? = null,
//    val short_description: String? = null,
//    val meta_keyword: String? = null,
//    val tier_price: List<ConfigurableOption?>? = null,
//    val regular_price_with_tax: Double? = 0.0,
//    val regular_price_without_tax: Double? = 0.0,
//    val final_price_with_tax: Double? = 0.0,
//    val final_price_without_tax: Double? = 0.0,
//    val is_saleable: Int? = 0,
//    var image_url: String? = null,
//    val product_type: String? = null,
//    val configurable_option: ArrayList<ConfigurableOption?>? = null,
//    val total_reviews_count: String? = null,
//    val gallery_images: ArrayList<String>? = null,
//    val video_url: String? = null,
//    val url: String? = null,
//    val buy_now_url: String? = null,
//    val is_in_stock: String? = null,
//    val has_custom_options: Boolean? = false,
//    val custom_options: List<ConfigurableOption?>? = null,
//    val upsell: ArrayList<Upsell>? = null,
//    var dbOptions: String? = null,
//    var is_wishList: Boolean? = false,
//    var remaining_quantity: Int? = 0,
//    var show_sale_badge: Int? = 0
//) : Serializable {
//    data class Upsell(
//        val entity_id: String? = null,
//        val short_description: String? = null,
//        val name: String? = null,
//        val image_url: String? = null,
//        var regular_price: String? = null,
//        val final_price: String? = null,
//        val is_saleable: String? = null,
//        val prod_type_id: String? = null,
//        val product_type: String? = null,
//        val has_custom_options: Boolean? = false,
//        val custom_options: String? = null,
//        val url: String? = null,
//        val buy_now_url: String? = null,
//        val discount_percentage: String? = null,
//        var is_wishlist: Boolean? = false,
//        var show_sale_badge: Int? = 0,
//        var sale_img: String? = null,
//        var sale_img_h: String? = null,
//        var sale_img_w: String? = null
//
//
//    ) : Serializable
//
//    data class MatchLook(
//        val entity_id: String? = null,
//        val short_description: String? = null,
//        val name: String? = null,
//        val image_url: String? = null,
//        var regular_price: String? = null,
//        val final_price: String? = null,
//        val is_saleable: String? = null,
//        val prod_type_id: String? = null,
//        val product_type: String? = null,
//        val has_custom_options: Boolean? = false,
//        val custom_options: String? = null,
//        val url: String? = null,
//        val buy_now_url: String? = null,
//        val discount_percentage: String? = null,
//        var is_wishlist: Boolean? = false,
//        var show_sale_badge: Int? = 0,
//        var sale_img: String? = null,
//        var sale_img_h: String? = null,
//        var sale_img_w: String? = null
//
//    ) : Serializable
//
//    data class ConfigurableOption(
//        val type: String? = null,
//        val attribute_id: String? = null,
//        val attribute_code: String? = null,
//        val attributes: ArrayList<Attribute?>? = null,
//        val is_last: String? = null
//    ) : Serializable {
//        data class Attribute(
//            val value: String? = null,
//            val option_id: String? = null,
//            val should_select: Boolean? = false,
//            val image_url: String? = null,
//            val entity_id: String? = null,
//            val images: ArrayList<String>? = null,
//            var isSelected: Boolean? = false,
//            var isAvailable: Boolean? = true
//        ) : Serializable
//    }
//}
//
//
//data class ProductMatchUpsell(
//    val upsell: ArrayList<ProductDetailData.Upsell?>? = null,
//    val match_look: List<ProductDetailData.MatchLook>? = null
//) : Serializable
//
//data class ProductData(
//
//    val entity_id: String? = null,
//    val short_description: String? = null,
//    val name: String? = null,
//    val regular_price: String? = null,
//    val final_price: String? = null,
//    val is_saleable: String? = null,
//    val image_url: String? = null,
//    val has_custom_options: Boolean? = false
//) : Serializable