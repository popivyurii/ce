package com.popivyurii.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.popivyurii.database.dao.ExchangeHistoryDao
import com.popivyurii.database.dao.WalletDao
import com.popivyurii.database.model.ExchangeHistoryDbo
import com.popivyurii.database.model.WalletDbo

@Database(entities = [WalletDbo::class, ExchangeHistoryDbo::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun walletDao(): WalletDao
    abstract fun exchangeHistoryDao(): ExchangeHistoryDao
}