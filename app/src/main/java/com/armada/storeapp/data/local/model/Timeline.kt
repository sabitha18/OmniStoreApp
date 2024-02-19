package com.armada.storeapp.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timeline")
data class Timeline(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val group_id: String,
    val timeline_id: String?
)
