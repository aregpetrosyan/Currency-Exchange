package com.aregyan.compose.ui.users

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.aregyan.compose.R
import com.aregyan.compose.ui.components.NoNetwork
import com.aregyan.compose.ui.theme.Red

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
            LazyRow {
                items(items = uiState.currencyList) {
                    BalanceRowItem(balance = 0.00, currency = it)
                }
            }
            Header(text = stringResource(id = R.string.currency_exchange).uppercase())
            ExchangeColumnItem()
        }
    }
}

@Composable
private fun Header(
    text: String,
) {
    Text(
        modifier = Modifier.padding(16.dp),
        text = text,
        style = MaterialTheme.typography.subtitle2,
        color = MaterialTheme.colors.onBackground
    )
}

@Composable
private fun BalanceRowItem(balance: Double, currency: String) {
    Text(
        modifier = Modifier.padding(16.dp),
        text = "$balance $currency",
        style = MaterialTheme.typography.subtitle2,
        color = MaterialTheme.colors.onBackground
    )
}

@Composable
private fun ExchangeColumnItem() {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        val (icon, action, sum, currency, line) = createRefs()
        Icon(
            modifier = Modifier
                .constrainAs(icon) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                }
                .size(48.dp)
                .background(Red, CircleShape)
                .padding(8.dp),
            painter = painterResource(id = R.drawable.baseline_arrow_upward_24),
            tint = MaterialTheme.colors.background,
            contentDescription = null
        )
        Text(
            modifier = Modifier
                .constrainAs(action) {
                    top.linkTo(parent.top)
                    start.linkTo(icon.end)
                    bottom.linkTo(parent.bottom)
                }
                .padding(16.dp),
            text = stringResource(id = R.string.sell),
            style = MaterialTheme.typography.subtitle2,
            color = MaterialTheme.colors.onBackground
        )
    }
}