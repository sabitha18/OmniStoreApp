package com.armada.storeapp.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "wishlist")
data class Wishlist(
    @PrimaryKey(autoGenerate = true
    )
    val id:Int,
    val item_id: String,
    val entity_id: String?,
    val name: String?,
    val short_description: String?,
    val regular_price_with_tax: String?,
    val final_price_with_tax: String?,
    val is_saleable: String?,
    val image_url: String?,
    val sku: String?
)