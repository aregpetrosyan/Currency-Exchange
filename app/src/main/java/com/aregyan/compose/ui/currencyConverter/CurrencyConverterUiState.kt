package com.aregyan.compose.ui.currencyConverter

data class CurrencyConverterUiState(
    val balanceList: List<Pair<String, Double>> = listOf(),
    val sellCurrencyList: List<String> = listOf(),
    val receiveCurrencyList: List<String> = listOf(),
    val sellCurrency: String = "",
    val receiveCurrency: String = "",
    val sellValue: String = "0.00",
    val receiveValue: String = "0.00",
    val offline: Boolean = false
)