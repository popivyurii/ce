package com.popivyurii.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "exchange_rates")
class ExchangeRatesDbo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val baseCurrency: String,
    val date: String,
    val rates: Map<String, Double>,
    val timestamp: Long = System.currentTimeMillis(),
)