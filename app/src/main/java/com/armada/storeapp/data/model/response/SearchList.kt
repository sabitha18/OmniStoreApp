package com.armada.storeapp.data.model.response

data class SearchList(
    val id: String? = null,
    val name: String? = null,
    val product_total: Int? = null,
    val type: String? = null,
    val image: String? = null,
    var regular_price: Double? = null,
    val final_price: Double? = null,
    val results_count: String ?= null,
    var objectID: String?= null,
    var thumbnail_url: String?= null,
    var image_url: String?= null,
)