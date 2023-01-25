package com.aregyan.compose.network

import com.aregyan.compose.network.model.ExchangeRatesApiModel
import retrofit2.http.GET

interface ExchangeRatesApi {

    @GET("/tasks/api/currency-exchange-rates")
    suspend fun getExchangeRates(): ExchangeRatesApiModel
}