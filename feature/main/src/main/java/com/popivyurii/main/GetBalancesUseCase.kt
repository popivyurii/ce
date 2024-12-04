package com.popivyurii.main

import com.popivyurii.data.ExchangeRepository
import kotlinx.coroutines.flow.StateFlow

class GetBalancesUseCase(private val repository: ExchangeRepository) {
    val balances: StateFlow<Map<String, Double>> get() = repository.balances
}