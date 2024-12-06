package com.popivyurii.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.popivyurii.ui_logic.ExchangeViewModel


@Composable
internal fun CurrencyConversionScreen(
    modifier: Modifier = Modifier
) {
    val viewModel: ExchangeViewModel = viewModel()
    val balances by viewModel.balances.collectAsState()
    val currencies by viewModel.currencies.collectAsState()
    val conversionResult by viewModel.conversionResult.collectAsState()
    CurrencyExchangeContent(
        ConverterDataUI(
            balances = balances,
            currencies = currencies,
            onConvert = viewModel::onConvert,
            conversionResult = conversionResult
        )
    )
}

