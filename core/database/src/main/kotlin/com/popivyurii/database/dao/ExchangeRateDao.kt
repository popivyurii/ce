package com.popivyurii.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.popivyurii.database.model.ExchangeRatesDbo

@Dao
interface ExchangeRateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExchangeRates(exchangeRateEntity: ExchangeRatesDbo)

    @Query("SELECT * FROM exchange_rates WHERE baseCurrency = :baseCurrency")
    suspend fun getExchangeRates(baseCurrency: String): ExchangeRatesDbo?

    @Query("DELETE FROM exchange_rates WHERE timestamp < :timestamp")
    suspend fun deleteOldRates(timestamp: Long)
}
