package com.popivyurii.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.popivyurii.database.dao.ExchangeHistoryDao
import com.popivyurii.database.dao.ExchangeRateDao
import com.popivyurii.database.dao.WalletDao
import com.popivyurii.database.model.ExchangeHistoryDbo
import com.popivyurii.database.model.WalletDbo

class CurrencyExchangerDatabase internal constructor(private val database: AppDatabase) {
    val walletDao: WalletDao
        get() = database.walletDao()
    val exchangeHistoryDao: ExchangeHistoryDao
        get() = database.exchangeHistoryDao()
    val exchangeRateDao: ExchangeRateDao
        get() = database.exchangeRateDao()
}

@Database(entities = [WalletDbo::class, ExchangeHistoryDbo::class], version = 1)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun walletDao(): WalletDao
    abstract fun exchangeHistoryDao(): ExchangeHistoryDao
    abstract fun exchangeRateDao(): ExchangeRateDao
}

fun CurrencyExchangerDatabase(applicationContext: Context): CurrencyExchangerDatabase {
    val currencyConverterDatabase =
        Room.databaseBuilder(
            checkNotNull(applicationContext.applicationContext),
            AppDatabase::class.java,
            "currency-converter-database"
        ).build()
    return CurrencyExchangerDatabase(currencyConverterDatabase)
}