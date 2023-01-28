package com.aregyan.currencyexchange.network.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExchangeRatesApiModel(
    @Json(name = "base")
    val base: String,
    @Json(name = "date")
    val date: String,
    @Json(name = "rates")
    val rates: Map<String, Double>
)