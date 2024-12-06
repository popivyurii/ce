package com.popivyurii.main

public data class ConverterDataUI(
    val balances: Map<String, Double>,
    val currencies: List<String>,
    val onConvert: (fromCurrency: String, toCurrency: String, amount: Double) -> Unit,
    val conversionResult: Result<String>?
)