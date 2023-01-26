package com.aregyan.compose.ui.currencyConverter

data class CurrencyConverterUiState(
    val balanceList: List<Pair<String, Double>> = listOf(),
    val sellCurrencyList: List<String> = listOf(),
    val receiveCurrencyList: List<String> = listOf(),
    val sellCurrency: String = "",
    val receiveCurrency: String = "",
    val offline: Boolean = false
)