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
    private val balanceList = mutableListOf<Pair<String, String>>()

    private lateinit var fetchExchangeRatesJob: Job
    private var initialValuesNotSet = true
    private var conversionCount = 0

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
        } else if ((sellBalance?.second?.toDouble() ?: 0.0) < uiState.sellValue.toDouble()) {
            showDialog(title = R.string.conversion_failed, message = R.string.not_enough_funds)
        } else {
            conversionCount += 1
            val commissionFeeEnabled = conversionCount > 5

            val sellItem = balanceList.find { it.first == uiState.sellCurrency }
            val sellIndex = balanceList.indexOf(sellItem)
            val totalCommissionFee = COMMISSION_FEE * uiState.sellValue.toDouble()
            balanceList[sellIndex] = Pair(
                uiState.sellCurrency,
                ((sellItem?.second?.toDouble() ?: 0.0) - uiState.sellValue.toDouble()
                        - if (commissionFeeEnabled) totalCommissionFee else 0.0).format()
            )

            val receiveItem = balanceList.find { it.first == uiState.receiveCurrency }
            val receiveIndex = balanceList.indexOf(receiveItem)
            balanceList[receiveIndex] = Pair(
                uiState.receiveCurrency,
                ((receiveItem?.second?.toDouble() ?: 0.0) + uiState.receiveValue.toDouble()).format()
            )

            showDialog(
                title = R.string.currency_converted,
                message = if (commissionFeeEnabled) R.string.commission_fee else R.string.you_have_converted,
                params = listOf(
                    "${uiState.sellValue} ${uiState.sellCurrency}",
                    "${uiState.receiveValue} ${uiState.receiveCurrency}",
                    "${totalCommissionFee.format()} ${uiState.sellCurrency}"
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
                    balanceList.add(0, Pair(it, "1000.00"))
                } else {
                    balanceList.add(Pair(it, "0.00"))
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
            uiState = uiState.copy(
                receiveValue = (uiState.sellValue.toDouble() / sellRate * receiveRate).format()
            )
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

    private fun Double.format() = String.format("%.2f", this)

    companion object {
        private const val COMMISSION_FEE = 0.007 // 0.7%
    }

}