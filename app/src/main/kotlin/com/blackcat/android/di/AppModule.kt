package com.blackcat.android.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.blackcat.android.data.local.dao.DeliveryDao
import com.blackcat.android.data.remote.service.CarrierTrackingService
import com.blackcat.android.data.repository.DeliveryRepositoryImpl
import com.blackcat.android.data.repository.UserPreferencesRepository
import com.blackcat.android.domain.repository.DeliveryRepository
import com.blackcat.android.domain.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideDeliveryRepository(
        deliveryDao: DeliveryDao,
        carrierTrackingService: CarrierTrackingService
    ): DeliveryRepository {
        return DeliveryRepositoryImpl(deliveryDao, carrierTrackingService)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(
        dataStore: DataStore<Preferences>
    ): SettingsRepository {
        return UserPreferencesRepository(dataStore)
    }
}
