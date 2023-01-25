package com.aregyan.compose.ui.users

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aregyan.compose.repository.ExchangeRatesRepository
import com.aregyan.compose.repository.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val usersRepository: UsersRepository,
    private val exchangeRatesRepository: ExchangeRatesRepository
) : ViewModel() {

    var uiState by mutableStateOf(UsersUiState())
        private set

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val exchangeRates = exchangeRatesRepository.fetchExchangeRates()
            withContext(Dispatchers.Main) {
                uiState = if (exchangeRates == null) {
                    uiState.copy(offline = true)
                } else {
                    uiState.copy(currencyList = exchangeRates.rates.map { it.key })
                }
            }
        }
    }

}