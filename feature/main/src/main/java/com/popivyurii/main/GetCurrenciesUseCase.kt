package com.popivyurii.main

import com.popivyurii.data.ExchangeRepository
import kotlinx.coroutines.flow.StateFlow

class GetCurrenciesUseCase(private val repository: ExchangeRepository) {
    val currencies: StateFlow<List<String>> get() = repository.currencies
}