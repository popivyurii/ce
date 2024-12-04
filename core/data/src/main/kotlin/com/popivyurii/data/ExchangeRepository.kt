package com.popivyurii.data

import com.popivyurii.ceapi.CEApi
import com.popivyurii.database.dao.ExchangeHistoryDao
import com.popivyurii.database.dao.ExchangeRateDao
import com.popivyurii.database.dao.WalletDao
import com.popivyurii.database.model.ExchangeHistoryDbo
import com.popivyurii.database.model.ExchangeRatesDbo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.concurrent.TimeUnit

public class ExchangeRepository(
    private val walletDao: WalletDao,
    private val exchangeHistoryDao: ExchangeHistoryDao,
    private val exchangeRateDao: ExchangeRateDao,
    private val ceApi: CEApi,
) {

    private val _balances = MutableStateFlow<Map<String, Double>>(emptyMap())
    public val balances: StateFlow<Map<String, Double>> get() = _balances

    private val _currencies = MutableStateFlow<List<String>>(emptyList())
    public val currencies: StateFlow<List<String>> get() = _currencies

    init {
        CoroutineScope(Dispatchers.IO).launch {

            launch {
                walletDao.observeAllBalances()
                    .map { list -> list.associate { it.currencyCode to it.balance } }
                    .collect { balanceMap ->
                        _balances.value = balanceMap
                    }
            }

            launch {
                combine(
                    walletDao.observeAllBalances().map { list -> list.map { it.currencyCode } },
                    exchangeRateDao.observeExchangeRates("USD") // TODO: Replace "USD" with your app base currency
                        .map { ratesDbo -> ratesDbo?.rates?.keys?.toList() ?: emptyList() }
                ) { walletCurrencies, exchangeCurrencies ->
                    (walletCurrencies + exchangeCurrencies).distinct()
                }.collect { currencyList ->
                    _currencies.value = currencyList
                }
            }
        }
    }


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

        val rateResult = fetchExchangeRate(fromCurrency, toCurrency).first()

        if (rateResult.isFailure) {
            return Result.failure(rateResult.exceptionOrNull() ?: IllegalStateException("Unknown error"))
        }

        val rate = rateResult.getOrNull()
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

    private val cacheExpirationTime = TimeUnit.MINUTES.toMillis(10)

    private suspend fun fetchExchangeRate(fromCurrency: String, toCurrency: String): Flow<Result<Double>> = flow {

        val cachedRates = exchangeRateDao.getExchangeRates(fromCurrency)

        if (cachedRates != null && System.currentTimeMillis() - cachedRates.timestamp < cacheExpirationTime) {
            // If cached rates are fresh, return the rate from Room
            val cachedRate = cachedRates.rates[toCurrency]
            if (cachedRate != null) {
                emit(Result.success(cachedRate))
            } else {
                emit(Result.failure(ExchangeRateError.NoRateFoundError("Rate not found for $toCurrency")))
            }
        } else {
            // Cache expired or not found, fetch new rates from API
            try {
                val result = ceApi.getExchangeRates()
                if (result.isSuccess) {
                    val exchangeRates = result.getOrNull()
                    if (exchangeRates != null) {
                        val rate = exchangeRates.rates?.get(toCurrency)
                        if (rate != null) {
                            // Cache the fetched rates
                            exchangeRateDao.insertExchangeRates(
                                ExchangeRatesDbo(
                                    baseCurrency = fromCurrency,
                                    rates = exchangeRates.rates!!,
                                    date = exchangeRates.date ?: "",
                                    timestamp = System.currentTimeMillis()
                                )
                            )
                            emit(Result.success(rate))
                        } else {
                            emit(Result.failure(ExchangeRateError.NoRateFoundError("Rate not found for $toCurrency")))
                        }
                    } else {
                        emit(Result.failure(ExchangeRateError.APIError("Failed to fetch exchange rates")))
                    }
                }
            } catch (e: IOException) {
                emit(Result.failure(ExchangeRateError.NetworkError("Network error: ${e.message}")))
            } catch (e: Exception) {
                emit(Result.failure(ExchangeRateError.UnknownError("Unknown error: ${e.message}")))
            }
        }
    }
}

public sealed class ExchangeRateError(public override val message: String): Throwable() {
    public class NetworkError(message: String) : ExchangeRateError(message)
    public class APIError(message: String) : ExchangeRateError(message)
    public class NoRateFoundError(message: String) : ExchangeRateError(message)
    public class UnknownError(message: String) : ExchangeRateError(message)
}
