package com.aregyan.compose.repository

import com.aregyan.compose.network.ExchangeRatesApi
import com.aregyan.compose.network.model.ExchangeRatesApiModel
import timber.log.Timber
import javax.inject.Inject

class ExchangeRatesRepository @Inject constructor(
    private val exchangeRatesApi: ExchangeRatesApi
) {

    suspend fun fetchExchangeRates(): ExchangeRatesApiModel? {
        return try {
            exchangeRatesApi.getExchangeRates()
        } catch (e: Exception) {
            Timber.w(e)
            null
        }
    }
}