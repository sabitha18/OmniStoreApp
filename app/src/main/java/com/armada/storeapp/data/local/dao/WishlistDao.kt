package com.armada.storeapp.data.local.dao

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.armada.storeapp.data.local.model.Wishlist

@Dao
interface WishlistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductToWishlist(wishlist: Wishlist)

    @Query("SELECT DISTINCT * FROM wishlist")
    fun getWishlistedProducts(): LiveData<List<Wishlist>>

    @Query("SELECT DISTINCT entity_id from wishlist")
    fun getWishlistProductsIds(): LiveData<List<String>>

    @Query("SELECT DISTINCT item_id from wishlist WHERE entity_id=:productId")
    suspend fun getItemIdFromWishList(productId: String): String?

    @Query("DELETE FROM wishlist WHERE entity_id=:entityId")
    suspend fun removeProductFromWishlist(entityId: String)

    @Query("SELECT COUNT(entity_id) FROM wishlist")
    suspend fun getCountFromWishlist(): Int

    @Query("DELETE FROM wishlist WHERE id in (SELECT id FROM wishlist ORDER BY id LIMIT 1)")
    suspend fun deleteFirstWishlistProduct()

    @Query("DELETE FROM wishlist")
    suspend fun deleteAllWishlist()

}