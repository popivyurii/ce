package com.popivyurii.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp

@Preview
@Composable
internal fun CurrencyExchangeContent(
    @PreviewParameter(CurrencyConversionScreenPreviewProvider::class)
    converterDataUI: ConverterDataUI,
) {
    var fromCurrency by remember { mutableStateOf("") }
    var toCurrency by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var isFromCurrencyDropdownExpanded by remember { mutableStateOf(false) }
    var isToCurrencyDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(text = "Currency Converter", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "My Balances", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            converterDataUI.balances.forEach { (currency, balance) ->
                item {
                    BalanceCard(currency, balance)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Input Field for Amount
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Enter amount") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Dropdown for "From Currency"
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = fromCurrency,
                onValueChange = {},
                readOnly = true,
                label = { Text("From Currency") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isFromCurrencyDropdownExpanded = true }
            )
            DropdownMenu(
                expanded = isFromCurrencyDropdownExpanded,
                onDismissRequest = { isFromCurrencyDropdownExpanded = false }
            ) {
                converterDataUI.currencies.forEach { currency ->
                    DropdownMenuItem(text = {
                        Text(text = currency)
                    }, onClick = {
                        fromCurrency = currency
                        isFromCurrencyDropdownExpanded = false
                    })
                }
            }
        }

        // Dropdown for "To Currency"
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = toCurrency,
                onValueChange = {},
                readOnly = true,
                label = { Text("To Currency") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isToCurrencyDropdownExpanded = true }
            )
            DropdownMenu(
                expanded = isToCurrencyDropdownExpanded,
                onDismissRequest = { isToCurrencyDropdownExpanded = false }
            ) {
                converterDataUI.currencies.forEach { currency ->
                    DropdownMenuItem(text = {
                        Text(text = currency)
                    }, onClick = {
                        toCurrency = currency
                        isToCurrencyDropdownExpanded = false
                    })
                }
            }
        }

        // Submit Button
        Button(
            onClick = {
                val amountValue = amount.toDoubleOrNull()
                if (amountValue != null && fromCurrency.isNotEmpty() && toCurrency.isNotEmpty()) {
                    converterDataUI.onConvert(fromCurrency, toCurrency, amountValue)
                } else {
                    // Handle input validation here
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Convert")
        }

        if (converterDataUI.conversionResult?.getOrNull() != null) {
            AlertDialog(
                onDismissRequest = { /* Handle dismiss */ },
                title = { Text("Currency Converted") },
                text = {
                    Text(converterDataUI.conversionResult.getOrNull() ?: "Conversion failed!")
                },
                confirmButton = {
                    Button(onClick = { /* Dismiss */ }) {
                        Text("Done")
                    }
                }
            )
        }
    }
}

@Composable
fun BalanceCard(currency: String, balance: Double) {
    Card(
        modifier = Modifier
            .height(80.dp)
            .width(120.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = currency, style = MaterialTheme.typography.bodyMedium)
            Text(text = "%.2f".format(balance), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

private class CurrencyConversionScreenPreviewProvider(
) : PreviewParameterProvider<ConverterDataUI> {

    override val values = sequenceOf(
        ConverterDataUI(
            balances = mapOf<String, Double>(
                "USD" to 100.0,
                "EUR" to 50.0,
                "GBP" to 7.0),
            currencies = listOf("USD", "EUR", "GBP", "JPY"),
            onConvert = { _, _, _ -> },
            conversionResult = null
        )
    )

}
