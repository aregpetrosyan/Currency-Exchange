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

            exchangeRatesRepository.fetchExchangeRates()

//            usersRepository.refreshUsers()
//            usersRepository.users.collect { list ->
//                withContext(Dispatchers.Main) {
//                    uiState = if (list.isNullOrEmpty()) {
//                        uiState.copy(offline = true)
//                    } else {
//                        uiState.copy(
//                            list = list,
//                            offline = false
//                        )
//                    }
//                }
//            }
        }
    }

}