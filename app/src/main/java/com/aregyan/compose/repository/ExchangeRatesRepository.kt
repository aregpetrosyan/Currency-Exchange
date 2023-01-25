package com.aregyan.compose.repository

import com.aregyan.compose.network.ExchangeRatesApi
import com.aregyan.compose.network.model.ExchangeRatesApiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class ExchangeRatesRepository @Inject constructor(
    private val exchangeRatesApi: ExchangeRatesApi
) {

    var exchangeRates: Flow<ExchangeRatesApiModel>? = null

    suspend fun fetchExchangeRates() {
        try {
            exchangeRates = flow { exchangeRatesApi.getExchangeRates() }
        } catch (e: Exception) {
            Timber.w(e)
        }
    }
}