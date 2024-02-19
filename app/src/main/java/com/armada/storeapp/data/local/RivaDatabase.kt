package com.armada.storeapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.armada.storeapp.data.local.dao.OmniProductDao
import com.armada.storeapp.data.local.dao.RecentProductDao
import com.armada.storeapp.data.local.dao.ShoppingCartDao
import com.armada.storeapp.data.local.dao.WishlistDao
import com.armada.storeapp.data.local.model.*
import com.armada.storeapp.data.model.response.SkuMasterTypes

@Database(
    entities = [Notification::class, RecentProduct::class, RecentSearch::class, ShoppingCart::class,
        Timeline::class, Wishlist::class,SkuMasterTypes::class], version = 10
)
abstract class RivaDatabase : RoomDatabase() {
    abstract fun wishlistDao(): WishlistDao
    abstract fun recentProductDao(): RecentProductDao
    abstract fun shoppingCartDao(): ShoppingCartDao
    abstract fun omniProductDao(): OmniProductDao
}