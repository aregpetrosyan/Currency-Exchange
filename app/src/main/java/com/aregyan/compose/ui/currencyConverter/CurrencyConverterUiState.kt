package com.aregyan.compose.ui.currencyConverter

data class CurrencyConverterUiState(
    val balanceList: List<Pair<String, Double>> = listOf(),
    val currencyList: List<String> = listOf(),
    val offline: Boolean = false
)