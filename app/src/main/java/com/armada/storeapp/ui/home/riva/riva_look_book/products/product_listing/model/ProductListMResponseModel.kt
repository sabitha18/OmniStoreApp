package com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing.model

import java.io.Serializable

data class ProductListMResponseModel(
    val all_filter: ArrayList<ProductListMAllFilterModel?>? = null,
    val `data`: ArrayList<ProductListMDataModel>? = null,
    val message: String? = null,
    val product_count: Int? = null,
    val sorting: ArrayList<ProductListMSortingModel?>? = null,
    val status: Int? = null,
    val total_page: Int? = null
)

data class ProductListMAllFilterModel(
    val attribute_id: String? = null,
    val code: String? = null,
    val name: String? = null,
    val options: ArrayList<ProductListMOptionModel>? = null
) : Serializable

data class ProductListMDataModel(
    val barcode: Any? = null,
    val brand: String? = null,
    val configurable_option: ArrayList<ProductListMConfigurableOptionModel>? = null,
    val description: String? = null,
    val enable_special_text: String? = null,
    val final_price: String? = null,
    val has_options: Int? = null,
    val id: String? = null,
    val image: String? = null,
    val is_salable: Boolean? = null,
    val name: String? = null,
    val options: List<Any>? = null,
    val ordered_qty: Any? = null,
    var price: String? = null,
    val remaining_qty: Int? = null,
    val sale_img: String? = null,
    val sale_img_h: String? = null,
    val sale_img_w: String? = null,
    val short_description: String? = null,
    val sku: String? = null,
    val special_text: String? = null,
    val type: String? = null,
    var wishlist_item_id: Int? = null,
    var is_wishList: Boolean? = false,
    var hasMargin: Boolean? = false,
    val has_custom_options: Boolean? = null,
    var arrListColors: ArrayList<String>? = ArrayList(),
    var mediaType: String? = null,
    val mediaFile: String? = null,
    val custom_options: String,
    val associated_products: ArrayList<AssociateProductModel>? = null,
    val show_container_grid: Int? = 0,
    var container_width: String? = "",
    var hasMultipleBannerInOne: Boolean? = false,
    var arrListMultipleProduct: ArrayList<ProductListMDataModel>? = null

) : Serializable

data class ProductListMSortingModel(
    val sort_name: String? = null,
    val sort_value: String? = null,
    var isSelected: Boolean? = false
) : Serializable

data class ProductListMOptionModel(
    val attribute_name: String? = null,
    val id: String? = null,
    var isSelected: Boolean? = null,
    var swatch_url: String = ""
) : Serializable

data class ProductListMConfigurableOptionModel(
    val attribute_code: String? = null,
    val attribute_id: Int? = null,
    val attributes: ArrayList<ProductListMAttributeModel>? = null,
    val type: String? = null
) : Serializable

data class ProductListMAttributeModel(
    val color_code: String? = null,
    val option_id: String? = null,
    val value: String? = null
) : Serializable