package com.popivyurii.data

import com.popivyurii.database.dao.ExchangeHistoryDao
import com.popivyurii.database.dao.WalletDao
import com.popivyurii.database.model.ExchangeHistoryDbo

public class ExchangeRepository(
    private val walletDao: WalletDao,
    private val exchangeHistoryDao: ExchangeHistoryDao
) {
    public suspend fun getBalance(currencyCode: String): Double {
        return walletDao.getBalance(currencyCode)?.balance ?: 0.0
    }

    public suspend fun getExchangeHistory(): List<ExchangeHistoryDao> {
        return exchangeHistoryDao.getAllTransactions()
    }

    public suspend fun convertCurrency(
        fromCurrency: String,
        toCurrency: String,
        amount: Double
    ): Result<String> {

        val fromBalance = walletDao.getBalance(fromCurrency)?.balance ?: 0.0
        if (fromBalance < amount) return Result.failure(IllegalArgumentException("Insufficient funds"))

        val rate = fetchExchangeRate(fromCurrency, toCurrency)
        if (rate == null) return Result.failure(IllegalStateException("Exchange rate unavailable"))

        val convertedAmount = amount * rate

        val exchangeCount = exchangeHistoryDao.getTotalTransactionCount()
        val commission = if (exchangeCount >= 5) 0.007 * amount else 0.0
        val netAmount = amount + commission

        if (fromBalance < netAmount) return Result.failure(IllegalArgumentException("Insufficient funds for commission"))

        // Perform transaction atomically.
        walletDao.performTransaction {
            walletDao.adjustBalance(fromCurrency, -netAmount)
            walletDao.adjustBalance(toCurrency, convertedAmount)
            exchangeHistoryDao.insertTransaction(
                ExchangeHistoryDbo(
                    fromCurrencyCode = fromCurrency,
                    toCurrencyCode = toCurrency,
                    amount = amount,
                    rate = rate,
                    convertedAmount = convertedAmount,
                    commissionFee = commission,
                    timestamp = System.currentTimeMillis()
                )
            )
        }

        return Result.success("Successfully converted $amount $fromCurrency to $convertedAmount $toCurrency. Commission: $commission $fromCurrency.")
    }

    private suspend fun fetchExchangeRate(fromCurrency: String, toCurrency: String): Double? {
        //TODO Fetch rate from API or cache
    }
}
