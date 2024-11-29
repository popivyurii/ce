package com.popivyurii.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallet")
class WalletDbo (
    @PrimaryKey(autoGenerate = true)
    val currencyCode: String,
    val balance: Double,
)