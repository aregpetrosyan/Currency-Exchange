package com.aregyan.compose.ui.currencyConverter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.aregyan.compose.R
import com.aregyan.compose.ui.components.NoNetwork
import com.aregyan.compose.ui.theme.Green
import com.aregyan.compose.ui.theme.Red

@Composable
fun UsersScreen(
    onUserClick: (String) -> Unit
) {
    val viewModel = hiltViewModel<CurrencyConverterViewModel>()
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
                    color = Color.White
                )
            }
            Header(text = stringResource(id = R.string.my_balances))
            LazyRow {
                items(items = uiState.currencyList) {
                    BalanceRowItem(balance = 0.00, currency = it)
                }
            }
            Header(text = stringResource(id = R.string.currency_exchange))
            ExchangeColumnItem(isSell = true)
            SimpleDivider()
            ExchangeColumnItem(isSell = false)
            SimpleDivider()
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 16.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(MaterialTheme.colors.primary),
                onClick = { /*TODO*/ }
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = stringResource(id = R.string.submit),
                    style = MaterialTheme.typography.subtitle2,
                    color = Color.White
                )
            }
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
private fun ExchangeColumnItem(
    isSell: Boolean
) {
    var sumValue by remember { mutableStateOf("0.00") }
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 16.dp)
    ) {
        val (icon, action, sum, picker) = createRefs()
        Icon(
            modifier = Modifier
                .constrainAs(icon) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                }
                .size(40.dp)
                .background(if (isSell) Red else Green, CircleShape)
                .padding(8.dp),
            painter = painterResource(id = if (isSell) R.drawable.baseline_arrow_upward_24 else R.drawable.baseline_arrow_downward_24),
            tint = Color.White,
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
            text = stringResource(id = if (isSell) R.string.sell else R.string.receive),
            style = MaterialTheme.typography.subtitle2,
            color = MaterialTheme.colors.onBackground
        )
        Row(
            modifier = Modifier.constrainAs(picker) {
                top.linkTo(parent.top)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .padding(end = 2.dp),
                text = "EUR",
                style = MaterialTheme.typography.subtitle2,
                color = MaterialTheme.colors.onBackground
            )
            Icon(
                painter = painterResource(id = R.drawable.baseline_keyboard_arrow_down_24),
                tint = MaterialTheme.colors.onBackground,
                contentDescription = null
            )
        }
        BasicTextField(
            modifier = Modifier
                .constrainAs(sum) {
                    top.linkTo(parent.top)
                    end.linkTo(picker.start, 24.dp)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(action.end, 24.dp)
                    width = Dimension.fillToConstraints
                },
            value = sumValue,
            onValueChange = { sumValue = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End, color = MaterialTheme.colors.onBackground),
            singleLine = true
        )
    }
}

@Composable
private fun SimpleDivider() {
    Divider(
        modifier = Modifier.padding(start = 72.dp)
    )
}