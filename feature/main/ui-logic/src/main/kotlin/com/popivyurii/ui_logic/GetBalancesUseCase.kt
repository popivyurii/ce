package com.popivyurii.ui_logic

import com.popivyurii.data.ExchangeRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.StateFlow

class GetBalancesUseCase @Inject constructor(private val repository: ExchangeRepository) {
    val balances: StateFlow<Map<String, Double>> get() = repository.balances
}