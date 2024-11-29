package com.popivyurii.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exchange_history")
class ExchangeHistoryDbo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fromCurrencyCode: String,
    val toCurrencyCode: String,
    val amount: Double,
    val convertedAmount: Double,
    val commissionFee: Double,
    val timestamp: Long = System.currentTimeMillis() // Default to current time
)