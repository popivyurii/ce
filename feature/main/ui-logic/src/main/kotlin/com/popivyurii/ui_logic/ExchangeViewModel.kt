package com.popivyurii.ui_logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Provider

@HiltViewModel
class ExchangeViewModel @Inject internal constructor(
    getBalancesUseCase: Provider<GetBalancesUseCase>,
    getCurrenciesUseCase: Provider<GetCurrenciesUseCase>,
    convertCurrencyUseCase: Provider<GetCurrenciesUseCase>,
) : ViewModel() {

    val balances: StateFlow<Map<String, Double>> = getBalancesUseCase.get().balances
    val currencies: StateFlow<List<String>> = getCurrenciesUseCase.get().currencies

    private val _conversionResult = MutableStateFlow<Result<String>?>(null)
    val conversionResult: StateFlow<Result<String>?> get() = _conversionResult

    fun onConvert(fromCurrency: String, toCurrency: String, amount: Double) {
        viewModelScope.launch {
            try {
                val result = convertCurrencyUseCase.convertCurrency(fromCurrency, toCurrency, amount)
                _conversionResult.value = result
            } catch (e: Exception) {
                _conversionResult.value = Result.failure(e)
            }
        }
    }
}
