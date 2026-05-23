package com.example.munashehousing.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.munashehousing.R

@Composable
fun LeaseAgreementScreen(
    onAgree: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    var payUpfront by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.lease_agreement),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = stringResource(R.string.lease_min_stay), style = MaterialTheme.typography.bodyLarge)
                Text(text = stringResource(R.string.lease_pay_deposit), style = MaterialTheme.typography.bodyLarge)
                Text(text = stringResource(R.string.lease_notice), style = MaterialTheme.typography.bodyLarge)
                Text(text = stringResource(R.string.lease_penalty), style = MaterialTheme.typography.bodyLarge)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Choose Your Payment Plan:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(text = "Choose the option that works best for you.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(16.dp))

        // Option 1: Monthly
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { payUpfront = false },
            color = if (!payUpfront) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.medium,
            tonalElevation = if (!payUpfront) 4.dp else 0.dp,
            shadowElevation = if (!payUpfront) 2.dp else 0.dp
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = !payUpfront, onClick = { payUpfront = false })
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text("Standard Monthly Plan", fontWeight = FontWeight.Bold)
                    Text("Pay deposit + first month's rent now.", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Option 2: Upfront
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { payUpfront = true },
            color = if (payUpfront) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.medium,
            tonalElevation = if (payUpfront) 4.dp else 0.dp,
            shadowElevation = if (payUpfront) 2.dp else 0.dp
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = payUpfront, onClick = { payUpfront = true })
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text("Upfront Saver Plan", fontWeight = FontWeight.Bold)
                    Text("Pay 5 months rent + deposit, get 1 month FREE!", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onAgree(payUpfront) },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text(text = stringResource(R.string.agree_continue), fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text(text = stringResource(R.string.back))
        }
    }
}
