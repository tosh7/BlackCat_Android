package com.blackcat.android.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.blackcat.android.data.local.entity.DeliveryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeliveryDao {

    @Query("SELECT * FROM deliveries ORDER BY registeredDate DESC")
    fun getAllDeliveries(): Flow<List<DeliveryEntity>>

    @Query("SELECT * FROM deliveries WHERE id = :id")
    suspend fun getDeliveryById(id: Long): DeliveryEntity?

    @Query("SELECT * FROM deliveries WHERE trackingNumber = :trackingNumber AND carrier = :carrier")
    suspend fun getDeliveryByTrackingNumber(trackingNumber: String, carrier: String): DeliveryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDelivery(delivery: DeliveryEntity): Long

    @Update
    suspend fun updateDelivery(delivery: DeliveryEntity)

    @Delete
    suspend fun deleteDelivery(delivery: DeliveryEntity)

    @Query("DELETE FROM deliveries WHERE id = :id")
    suspend fun deleteDeliveryById(id: Long)

    @Query("DELETE FROM deliveries")
    suspend fun deleteAllDeliveries()

    @Query("SELECT COUNT(*) FROM deliveries")
    suspend fun getDeliveryCount(): Int
}
