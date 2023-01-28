package com.aregyan.currencyexchange.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aregyan.currencyexchange.ui.currencyConverter.CurrencyConverterScreen

@Composable
fun CurrencyExchangeApp() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Route.CURRENCY_CONVERTER
    ) {
        composable(Route.CURRENCY_CONVERTER) {
            CurrencyConverterScreen()
        }
    }
}

object Route {
    const val CURRENCY_CONVERTER = "currencyConverter"
}