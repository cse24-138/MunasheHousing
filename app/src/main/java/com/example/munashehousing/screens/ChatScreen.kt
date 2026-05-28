package com.example.munashehousing.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.munashehousing.R
import com.example.munashehousing.data.database.MessageEntity
import com.example.munashehousing.models.UserRole
import com.example.munashehousing.ui.viewmodels.PropertyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    agentName: String,
    viewModel: PropertyViewModel,
    userRole: UserRole, // ADDED: Matches the new parameter passed from MainActivity
    onBack: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }

    // Requirement E: Persistent Chat History
    val chatHistory by viewModel.getChatHistory(agentName).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(agentName, fontWeight = FontWeight.Bold)
                        // Shows the role of the person you are talking to
                        Text(
                            text = if (userRole == UserRole.STUDENT) "Landlord / Agent" else "Student",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text(stringResource(R.string.type_a_message)) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp)
                )
                IconButton(onClick = {
                    if (messageText.isNotBlank()) {
                        // Pass the message to the viewmodel
                        viewModel.sendMessage(agentName, messageText)
                        messageText = ""
                    }
                }) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chatHistory) { msg: MessageEntity ->
                ChatBubble(
                    text = msg.text,
                    // If the senderId is NOT the agentName, it means the current user sent it
                    isFromUser = msg.senderId != agentName,
                    status = if (msg.isSeen) "seen" else "sent"
                )
            }
        }
    }
}

@Composable
fun ChatBubble(text: String, isFromUser: Boolean, status: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isFromUser) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (isFromUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isFromUser) 16.dp else 0.dp,
                bottomEnd = if (isFromUser) 0.dp else 16.dp
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = text,
                    color = if (isFromUser) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (isFromUser) {
                    Row(
                        modifier = Modifier.align(Alignment.End),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = status,
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Default.DoneAll,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}