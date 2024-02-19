package com.armada.storeapp.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shoppingcart")
data class ShoppingCart(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val item_id: String,
    val entity_id: String?,
    val name: String?,
    val short_description: String?,
    val regular_price_with_tax: String?,
    val final_price_with_tax: String?,
    val is_saleable: String?,
    val image_url: String?,
    val quantity: Int?,
    val exclusive: String?,
    val category: String?,
    val video_id: String?,
    val remain_quantity: Int?,
    val enable_product: String?,
    val enable_index: String?,
    val celeb_id: Int?
)