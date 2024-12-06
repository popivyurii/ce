package com.popivyurii.ui_logic

import com.popivyurii.data.ExchangeRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.StateFlow

class GetCurrenciesUseCase @Inject constructor(private val repository: ExchangeRepository) {
    val currencies: StateFlow<List<String>> get() = repository.currencies
}