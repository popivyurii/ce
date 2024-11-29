package com.popivyurii.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.popivyurii.database.model.ExchangeHistoryDbo

@Dao
interface ExchangeHistoryDao {

    @Insert
    suspend fun insert(exchangeHistory: ExchangeHistoryDao)

    @Query("SELECT * FROM exchange_history ORDER BY timestamp DESC")
    suspend fun getAllTransactions(): List<ExchangeHistoryDao>

    @Query("SELECT * FROM exchange_history WHERE fromCurrencyCode = " +
            ":fromCurrencyCode AND toCurrencyCode = :toCurrencyCode ORDER BY timestamp DESC LIMIT 1")
    suspend fun getMostRecentTransaction(
        fromCurrencyCode: String, toCurrencyCode: String
    ): ExchangeHistoryDao?


    suspend fun insertTransaction(transaction: ExchangeHistoryDbo)


    @Query("SELECT COUNT(*) FROM exchange_history")
    suspend fun getTotalTransactionCount(): Int
}
