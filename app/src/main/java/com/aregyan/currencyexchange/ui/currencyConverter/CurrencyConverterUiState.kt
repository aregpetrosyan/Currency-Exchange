package com.aregyan.currencyexchange.ui.currencyConverter

import androidx.annotation.StringRes

data class CurrencyConverterUiState(
    val balanceList: List<Pair<String, String>> = listOf(),
    val sellCurrencyList: List<String> = listOf(),
    val receiveCurrencyList: List<String> = listOf(),
    val sellCurrency: String = "",
    val receiveCurrency: String = "",
    val sellValue: String = "0.00",
    val receiveValue: String = "0.00",
    val offline: Boolean = false,
    val showDialog: Boolean = false,
    val dialogParams: List<String> = listOf(),
    @StringRes val dialogTitle: Int = 0,
    @StringRes val dialogMessage: Int = 0
)