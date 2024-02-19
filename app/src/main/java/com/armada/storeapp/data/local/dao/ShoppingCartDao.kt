package com.armada.storeapp.data.local.dao

import android.content.ContentValues
import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.armada.storeapp.data.local.model.ShoppingCart

@Dao
interface ShoppingCartDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemToCart(cartItem: ShoppingCart)

    @Query("DELETE FROM shoppingcart")
    suspend fun deleteAllRecentProducts()

    @Query("SELECT DISTINCT * FROM shoppingcart")
    fun getCartProducts(): LiveData<List<ShoppingCart>>

    @Query("SELECT quantity FROM shoppingcart where entity_id=:productId AND celeb_id=:celebId")
    fun getQuantityOfProduct(productId: String, celebId: Int): Int

    @Query("SELECT COUNT(entity_id) FROM shoppingcart")
    suspend fun getCartCount(): Int

    @Query("UPDATE shoppingcart SET quantity = quantity + :newQty WHERE celeb_id =:celebid AND id=:cartId")
    suspend fun updateProductQuantityInCart(cartId: String, newQty: Int, celebid: Int)

    @Query("DELETE FROM shoppingcart WHERE entity_id=:entityId AND celeb_id=:celebid")
    fun removeProductFromCart(entityId: String, celebid: String)

    @Query("SELECT DISTINCT entity_id FROM shoppingcart")
    fun getCartProductsIds(): LiveData<List<String>>

    @Query("SELECT SUM(quantity) FROM shoppingcart")
    suspend fun getTotalCartQuantity(): Int?


}