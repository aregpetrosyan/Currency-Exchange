package com.aregyan.compose.ui.currencyConverter

data class CurrencyConverterUiState(
    val currencyList: List<String> = listOf(),
    val offline: Boolean = false
)