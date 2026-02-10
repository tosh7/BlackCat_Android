package com.blackcat.android.di

import android.content.Context
import androidx.room.Room
import com.blackcat.android.data.local.BlackCatDatabase
import com.blackcat.android.data.local.dao.DeliveryDao
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
    fun provideDatabase(@ApplicationContext context: Context): BlackCatDatabase {
        return Room.databaseBuilder(
            context,
            BlackCatDatabase::class.java,
            "blackcat_database"
        ).build()
    }

    @Provides
    fun provideDeliveryDao(database: BlackCatDatabase): DeliveryDao {
        return database.deliveryDao()
    }
}
