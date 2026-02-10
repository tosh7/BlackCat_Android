package com.blackcat.android.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.blackcat.android.data.local.dao.DeliveryDao
import com.blackcat.android.data.local.entity.DeliveryEntity

@Database(
    entities = [DeliveryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class BlackCatDatabase : RoomDatabase() {
    abstract fun deliveryDao(): DeliveryDao
}
