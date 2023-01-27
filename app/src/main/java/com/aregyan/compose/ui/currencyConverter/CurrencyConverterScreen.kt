package com.aregyan.compose.ui.currencyConverter

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.aregyan.compose.ui.components.LifecycleEventHandler
import com.aregyan.compose.ui.components.NoNetwork
import com.aregyan.compose.ui.theme.AmountGreen
import com.aregyan.compose.ui.theme.Green
import com.aregyan.compose.ui.theme.Red

@Composable
fun CurrencyConverterScreen() {
    val viewModel = hiltViewModel<CurrencyConverterViewModel>()
    val uiState = viewModel.uiState

    LifecycleEventHandler(
        onResume = viewModel::onStart,
        onPause = viewModel::onStop
    )

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
                items(items = uiState.balanceList) {
                    BalanceRowItem(balance = it.second, currency = it.first)
                }
            }
            Header(text = stringResource(id = R.string.currency_exchange))
            ExchangeColumnItem(
                isSell = true,
                selectedCurrency = uiState.sellCurrency,
                currencyList = uiState.sellCurrencyList,
                numericValue = uiState.sellValue,
                setCurrency = viewModel::setSellCurrency,
                onInputValueChange = viewModel::onInputValueChanged
            )
            SimpleDivider()
            ExchangeColumnItem(
                isSell = false,
                selectedCurrency = uiState.receiveCurrency,
                currencyList = uiState.receiveCurrencyList,
                numericValue = uiState.receiveValue,
                setCurrency = viewModel::setReceiveCurrency,
                onInputValueChange = {}
            )
            SimpleDivider()
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(MaterialTheme.colors.primary),
                onClick = viewModel::onSubmitClicked
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = stringResource(id = R.string.submit),
                    style = MaterialTheme.typography.subtitle2,
                    color = Color.White
                )
            }

        }

        if (uiState.showDialog) {
            Dialog(
                dialogTitle = uiState.dialogTitle,
                dialogMessage = uiState.dialogMessage,
                dialogParams = uiState.dialogParams,
                onDismiss = viewModel::dismissDialog
            )
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
        color = MaterialTheme.colors.onBackground.copy(alpha = 0.6f)
    )
}

@Composable
private fun BalanceRowItem(balance: String, currency: String) {
    Text(
        modifier = Modifier.padding(16.dp),
        text = "$balance $currency",
        style = MaterialTheme.typography.subtitle2,
        color = MaterialTheme.colors.onBackground
    )
}

@Composable
private fun ExchangeColumnItem(
    isSell: Boolean,
    selectedCurrency: String,
    currencyList: List<String>,
    numericValue: String,
    setCurrency: (String) -> Unit,
    onInputValueChange: (String) -> Unit
) {
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
        var showDropDown by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier.constrainAs(picker) {
                top.linkTo(parent.top)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }
        ) {
            Row(
                modifier = Modifier.clickable { showDropDown = true },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .padding(end = 2.dp),
                    text = selectedCurrency,
                    style = MaterialTheme.typography.subtitle2,
                    color = MaterialTheme.colors.onBackground
                )
                Icon(
                    painter = painterResource(id = R.drawable.baseline_keyboard_arrow_down_24),
                    tint = MaterialTheme.colors.onBackground,
                    contentDescription = null
                )
            }
            DropdownMenu(
                expanded = showDropDown,
                onDismissRequest = { showDropDown = false },
            ) {
                currencyList.forEach {
                    DropdownMenuItem(onClick = {
                        setCurrency(it)
                        showDropDown = false
                    }) {
                        Text(text = it)
                    }
                }
            }
        }
        if (isSell) {
            BasicTextField(
                modifier = Modifier
                    .constrainAs(sum) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end, 80.dp)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(action.end, 24.dp)
                        width = Dimension.fillToConstraints
                    },
                value = numericValue,
                onValueChange = onInputValueChange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.End,
                    color = MaterialTheme.colors.onBackground
                ),
                singleLine = true
            )
        } else {
            Text(
                modifier = Modifier
                    .constrainAs(sum) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end, 80.dp)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(action.end, 24.dp)
                        width = Dimension.fillToConstraints
                    },
                text = if (numericValue.toDouble() > 0) "+ $numericValue" else numericValue,
                style = MaterialTheme.typography.subtitle1,
                color = if (numericValue.toDouble() > 0) AmountGreen else MaterialTheme.colors.onBackground,
                textAlign = TextAlign.End,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun Dialog(
    @StringRes dialogTitle: Int,
    @StringRes dialogMessage: Int,
    dialogParams: List<String>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(id = dialogTitle),
                style = MaterialTheme.typography.subtitle2,
                color = MaterialTheme.colors.onBackground
            )
        },
        text = {
            Text(
                text = stringResource(
                        id = dialogMessage,
                        dialogParams.firstOrNull() ?: "",
                        dialogParams.getOrNull(1) ?: "",
                        dialogParams.getOrNull(2) ?: ""
                    ),
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onBackground
            )
        },
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text(
                        text = stringResource(id = R.string.done),
                        style = MaterialTheme.typography.button,
                        color = MaterialTheme.colors.primary
                    )
                }
            }
        }
    )
}

@Composable
private fun SimpleDivider() {
    Divider(
        modifier = Modifier.padding(start = 72.dp)
    )
}