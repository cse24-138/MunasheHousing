package com.example.munashehousing.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.munashehousing.R

@Composable
fun LeaseAgreementScreen(
    onAgree: () -> Unit,
    onBack: () -> Unit
) {

    var fullPayment by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Text(text = stringResource(R.string.lease_agreement), style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = stringResource(R.string.lease_min_stay))
        Text(text = stringResource(R.string.lease_pay_deposit))
        Text(text = stringResource(R.string.lease_notice))
        Text(text = stringResource(R.string.lease_penalty))

        Spacer(modifier = Modifier.height(20.dp))

        Row {
            Checkbox(
                checked = fullPayment,
                onCheckedChange = { fullPayment = it }
            )
            Text(text = stringResource(R.string.lease_full_payment))
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = onAgree) {
            Text(text = stringResource(R.string.agree_continue))
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = onBack) {
            Text(text = stringResource(R.string.back))
        }
    }
}