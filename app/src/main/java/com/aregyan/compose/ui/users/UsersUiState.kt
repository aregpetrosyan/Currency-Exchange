package com.aregyan.compose.ui.users

import com.aregyan.compose.domain.User

data class UsersUiState(
    val currencyList: List<String> = listOf(),
    val offline: Boolean = false
)