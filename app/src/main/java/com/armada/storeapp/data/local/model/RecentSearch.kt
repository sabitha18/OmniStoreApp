package com.armada.storeapp.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recentSearch")
data class RecentSearch(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val product_id: String,
    val entity_id: String?,
    val name: String?,
    val image_url: String?
)
