package com.popivyurii.ui_logic

import com.popivyurii.data.ExchangeRepository
import javax.inject.Inject

class ConvertCurrencyUseCase @Inject constructor(private val repository: ExchangeRepository) {

    suspend fun convertCurrency(
        fromCurrency: String, toCurrency: String, amount: Double
    ): Result<String> {
        return repository.convertCurrency(fromCurrency, toCurrency, amount)
    }
}