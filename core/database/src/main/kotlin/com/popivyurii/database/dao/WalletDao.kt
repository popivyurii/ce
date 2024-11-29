package com.popivyurii.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.popivyurii.database.model.WalletDbo


@Dao
interface WalletDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(wallet: WalletDbo)

    @Query("SELECT * FROM wallet WHERE currencyCode = :currencyCode")
    suspend fun getBalance(currencyCode: String): WalletDbo?

    @Query("SELECT * FROM wallet")
    suspend fun getAllBalances(): List<WalletDbo>

    @Query("UPDATE wallet SET balance = balance + :amount WHERE currencyCode = :currencyCode")
    suspend fun adjustBalance(currencyCode: String, amount: Double)

    @Transaction
    suspend fun <R> performTransaction(block: suspend () -> R): R {
        return block()
    }

    @Delete
    suspend fun delete(wallet: WalletDbo)

}
