package com.armada.storeapp.data.model.response

data class Item(
    val brand: String,
    val configurable_option: List<Any>,
    val description: String,
    val final_price: String,
    val has_options: Int,
    val id: String,
    val image: String,
    val is_salable: String,
    val item_id: String,
    val name: String,
    val options: List<Any>,
    val parent_id: String,
    val price: String,
    val qty: Int,
    val remaining_qty: Int,
    val savings: Double,
    val short_description: String,
    val sku: String,
    val type: String
)