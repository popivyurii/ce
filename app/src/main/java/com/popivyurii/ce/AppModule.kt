package com.popivyurii.ce

import android.content.Context
import com.popivyurii.ceapi.CurrencyExchangeApi
import com.popivyurii.database.CurrencyExchangerDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideCurrencyApi(): CurrencyExchangeApi {
        return CurrencyExchangeApi(
            baseUrl = BuildConfig.BASE_URL,
        )
    }

    @Provides
    @Singleton
    fun provideCurrencyExchangerDatabase(
        @ApplicationContext context: Context
    ): CurrencyExchangerDatabase {
        return CurrencyExchangerDatabase(context)
    }

}