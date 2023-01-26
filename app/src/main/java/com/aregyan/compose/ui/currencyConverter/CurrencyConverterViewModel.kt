package com.aregyan.compose.ui.currencyConverter

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aregyan.compose.repository.ExchangeRatesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CurrencyConverterViewModel @Inject constructor(
    private val exchangeRatesRepository: ExchangeRatesRepository
) : ViewModel() {

    var uiState by mutableStateOf(CurrencyConverterUiState())
        private set

    private var exchangeRatesList = mutableMapOf<String, Double>()
    private val receiveCurrencyList = mutableListOf<String>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val exchangeRates = exchangeRatesRepository.fetchExchangeRates()
            exchangeRatesList = exchangeRates?.rates?.toMutableMap() ?: mutableMapOf()
            exchangeRatesList["EUR"] = 1.0
            val currencyList = exchangeRates?.rates?.map { it.key }
            val balanceList = mutableListOf<Pair<String, Double>>()

            currencyList?.forEach {
                if (it == "EUR") {
                    balanceList.add(0, Pair(it, 1000.0))
                } else {
                    balanceList.add(Pair(it, 0.0))
                }
            }
            val sellCurrencyList = mutableListOf<String>()
            balanceList.forEach {
                receiveCurrencyList.add(it.first)
                if (it.second > 0) {
                    sellCurrencyList.add(it.first)
                }
            }
            withContext(Dispatchers.Main) {
                uiState = if (exchangeRates == null) {
                    uiState.copy(offline = true)
                } else {
                    uiState.copy(
                        balanceList = balanceList,
                        sellCurrencyList = sellCurrencyList,
                        receiveCurrencyList = receiveCurrencyList
                    )
                }
            }
        }
    }

    fun setSellCurrency(currency: String) {
        val modifierReceiveCurrencyList = receiveCurrencyList
        modifierReceiveCurrencyList.remove(currency)
        uiState = uiState.copy(
            sellCurrency = currency,
            receiveCurrencyList = modifierReceiveCurrencyList
        )
        updateReceiveValue()
    }

    fun setReceiveCurrency(currency: String) {
        uiState = uiState.copy(receiveCurrency = currency)
        updateReceiveValue()
    }

    fun onInputValueChanged(value: String) {
        if (value.toDouble() <= 1000000000) {
            uiState = uiState.copy(sellValue = value)
            updateReceiveValue()
        }
    }

    private fun updateReceiveValue() {
        val sellRate = exchangeRatesList[uiState.sellCurrency]
        val receiveRate = exchangeRatesList[uiState.receiveCurrency]
        if (sellRate != null && receiveRate != null) {
            val receiveValue = String.format("%.2f", uiState.sellValue.toDouble() / sellRate * receiveRate)
            uiState = uiState.copy(receiveValue = receiveValue)
        }
    }

}