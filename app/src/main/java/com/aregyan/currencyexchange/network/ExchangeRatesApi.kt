package com.aregyan.currencyexchange.network

import com.aregyan.currencyexchange.network.model.ExchangeRatesApiModel
import retrofit2.http.GET

interface ExchangeRatesApi {

    @GET("/tasks/api/currency-exchange-rates")
    suspend fun getExchangeRates(): ExchangeRatesApiModel
}