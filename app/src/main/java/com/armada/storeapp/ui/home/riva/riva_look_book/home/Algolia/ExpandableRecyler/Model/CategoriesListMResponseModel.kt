package com.armada.storeapp.ui.home.riva.riva_look_book.home.Algolia.ExpandableRecyler.Model

import java.io.Serializable

data class CategoriesListMResponseModel(
    val `data`: CategoriesListMDataModel? = null,
    val message: String? = null,
    val status: Int? = null
)

data class CategoriesListMDataModel(
    val banner_image_url: String? = null,
    var children_data: ArrayList<CategoriesListMDataModel?>? = null,
    val id: String? = null,
    val image: String? = null,
    val is_active: String? = null,
    var level: String? = null,
    val name: String? = null,
    val parent_id: String? = null,
    val position: Any? = null,
    val product_count: Int? = null,
    val thumbnail_url: String? = null,
    var is_selected: Boolean? = false,
    var is_visible: Boolean? = false,
    var is_last: Boolean? = false


) : Serializable
