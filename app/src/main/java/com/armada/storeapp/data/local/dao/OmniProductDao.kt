package com.armada.storeapp.data.local.dao

import android.content.ContentValues
import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.*
import com.armada.storeapp.data.local.model.RecentProduct
import com.armada.storeapp.data.model.response.ScannedItemDetailsResponse
import com.armada.storeapp.data.model.response.SkuMasterTypes

@Dao
interface OmniProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOmniProduct(product: SkuMasterTypes)

    @Query("SELECT DISTINCT * FROM omni_product")
    fun getAllOmniProducts(): LiveData<List<SkuMasterTypes>>

    @Query("DELETE FROM omni_product WHERE skuCode=:skuCode")
    suspend fun removeOmniProduct(skuCode: String)

    @Query("DELETE FROM omni_product")
    suspend fun deleteAll()

    @Query("UPDATE omni_product SET quantity =:newQty WHERE skuCode=:skuCode")
    suspend fun updateOmniProductQuantity(skuCode: String, newQty: Int)

}