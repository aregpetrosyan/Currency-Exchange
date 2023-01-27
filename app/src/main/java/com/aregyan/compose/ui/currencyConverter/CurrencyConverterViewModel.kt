package com.aregyan.compose.ui.currencyConverter

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import com.aregyan.compose.R
import com.aregyan.compose.network.model.ExchangeRatesApiModel
import com.aregyan.compose.repository.ExchangeRatesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class CurrencyConverterViewModel @Inject constructor(
    private val exchangeRatesRepository: ExchangeRatesRepository
) : ViewModel() {

    var uiState by mutableStateOf(CurrencyConverterUiState())
        private set

    private var exchangeRatesList = mutableMapOf<String, Double>()
    private var currencyList = listOf<String>()
    private val balanceList = mutableListOf<Pair<String, Double>>()

    private lateinit var fetchExchangeRatesJob: Job
    private var initialValuesNotSet = true

    fun setSellCurrency(currency: String) {
        val modifierReceiveCurrencyList = mutableListOf<String>()
        modifierReceiveCurrencyList.addAll(currencyList)
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

    fun onSubmitClicked() {
        val sellBalance = balanceList.find { it.first == uiState.sellCurrency }
        if (uiState.sellValue.toDouble() == 0.0 || uiState.sellCurrency.isEmpty() || uiState.receiveCurrency.isEmpty()) {
            showDialog(title = R.string.conversion_failed, message = R.string.values_missing)
        } else if ((sellBalance?.second ?: 0.0) < uiState.sellValue.toDouble()) {
            showDialog(title = R.string.conversion_failed, message = R.string.not_enough_funds)
        } else {
            val sellItem = balanceList.find { it.first == uiState.sellCurrency }
            val sellIndex = balanceList.indexOf(sellItem)
            balanceList[sellIndex] = Pair(
                uiState.sellCurrency,
                (sellItem?.second ?: 0.0) - uiState.sellValue.toDouble()
                        - if (COMMISSION_ENABLED) COMMISSION_FEE.toDouble() else 0.0
            )

            val receiveItem = balanceList.find { it.first == uiState.receiveCurrency }
            val receiveIndex = balanceList.indexOf(receiveItem)
            balanceList[receiveIndex] = Pair(
                uiState.receiveCurrency,
                (receiveItem?.second ?: 0.0) + uiState.receiveValue.toDouble()
            )

            showDialog(
                title = R.string.currency_converted,
                message = if (COMMISSION_ENABLED) R.string.commission_fee else R.string.you_have_converted,
                params = listOf(
                    "${uiState.sellValue} ${uiState.sellCurrency}",
                    "${uiState.receiveValue} ${uiState.receiveCurrency}",
                    "$COMMISSION_FEE ${uiState.sellCurrency}"
                )
            )
        }
    }

    fun dismissDialog() {
        uiState = uiState.copy(showDialog = false)
    }

    fun onStart() {
        fetchExchangeRatesJob = viewModelScope.launch(Dispatchers.IO) {
            flow {
                while (true) {
                    emit(exchangeRatesRepository.fetchExchangeRates())
                    delay(5000)
                }
            }.collectLatest {
                handleResponse(it)
            }
        }
        fetchExchangeRatesJob.start()
    }

    fun onStop() {
        fetchExchangeRatesJob.cancel()
    }

    private fun handleResponse(exchangeRatesApiModel: ExchangeRatesApiModel?) {
        exchangeRatesList = exchangeRatesApiModel?.rates?.toMutableMap() ?: mutableMapOf()
        exchangeRatesList["EUR"] = 1.0
        if (initialValuesNotSet) {
            currencyList = exchangeRatesApiModel?.rates?.map { it.key } ?: listOf()

            currencyList.forEach {
                if (it == "EUR") {
                    balanceList.add(0, Pair(it, 1000.0))
                } else {
                    balanceList.add(Pair(it, 0.0))
                }
            }

            uiState = if (exchangeRatesApiModel == null) {
                uiState.copy(offline = true)
            } else {
                uiState.copy(
                    balanceList = balanceList,
                    sellCurrencyList = currencyList,
                    receiveCurrencyList = currencyList
                )
            }
            initialValuesNotSet = false
        }
    }

    private fun updateReceiveValue() {
        val sellRate = exchangeRatesList[uiState.sellCurrency]
        val receiveRate = exchangeRatesList[uiState.receiveCurrency]
        if (sellRate != null && receiveRate != null) {
            val receiveValue =
                String.format("%.2f", uiState.sellValue.toDouble() / sellRate * receiveRate)
            uiState = uiState.copy(receiveValue = receiveValue)
        }
    }

    private fun showDialog(
        @StringRes title: Int,
        @StringRes message: Int,
        params: List<String> = listOf()
    ) {
        uiState = uiState.copy(
            showDialog = true,
            dialogTitle = title,
            dialogMessage = message,
            dialogParams = params
        )
    }

    companion object {
        private const val COMMISSION_FEE = "0.70"
        private const val COMMISSION_ENABLED = true
    }

}