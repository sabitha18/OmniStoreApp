package com.armada.storeapp.di

import android.content.Context
import androidx.room.Room
import com.armada.storeapp.data.local.RivaDatabase
import com.armada.storeapp.data.local.dao.OmniProductDao
import com.armada.storeapp.data.local.dao.RecentProductDao
import com.armada.storeapp.data.local.dao.ShoppingCartDao
import com.armada.storeapp.data.local.dao.WishlistDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideRivaDatabase(@ApplicationContext appContext: Context): RivaDatabase {
        return Room.databaseBuilder(
            appContext,
            RivaDatabase::class.java,
            "riva_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideWishlistDao(db: RivaDatabase): WishlistDao {
        return db.wishlistDao()
    }

    @Provides
    fun provideShoppingCartDao(db: RivaDatabase): ShoppingCartDao {
        return db.shoppingCartDao()
    }

    @Provides
    fun provideRecentProductDao(db: RivaDatabase): RecentProductDao {
        return db.recentProductDao()
    }

    @Provides
    fun provideOmniProductDao(db: RivaDatabase): OmniProductDao {
        return db.omniProductDao()
    }
}