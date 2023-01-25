package com.aregyan.compose.ui.users

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aregyan.compose.R
import com.aregyan.compose.ui.components.NoNetwork

@Composable
fun UsersScreen(
    onUserClick: (String) -> Unit
) {
    val viewModel = hiltViewModel<UsersViewModel>()
    val uiState = viewModel.uiState

    if (uiState.offline) {
        NoNetwork()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(MaterialTheme.colors.primary)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = stringResource(id = R.string.currency_converter),
                    style = MaterialTheme.typography.subtitle1,
                    color = MaterialTheme.colors.onBackground
                )
            }
            Header(text = stringResource(id = R.string.my_balances).uppercase())
            Header(text = stringResource(id = R.string.currency_exchange).uppercase())
        }
    }
}

@Composable
fun Header(
    text: String,
) {
    Text(
        modifier = Modifier.padding(16.dp),
        text = text,
        style = MaterialTheme.typography.subtitle2,
        color = MaterialTheme.colors.onBackground
    )
}