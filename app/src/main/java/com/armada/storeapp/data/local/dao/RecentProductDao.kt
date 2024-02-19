package com.armada.storeapp.data.local.dao

import android.content.ContentValues
import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.armada.storeapp.data.local.model.RecentProduct

@Dao
interface RecentProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductToRecent(product: RecentProduct)

    @Query("SELECT DISTINCT * FROM recentproducts")
    fun getRecentProducts(): LiveData<List<RecentProduct>>

    @Query("DELETE FROM recentproducts")
    suspend fun deleteAllRecentProducts()

    @Query("DELETE FROM recentproducts WHERE item_id=:itemId")
    suspend fun removeProductFromRecent(itemId: String)
}