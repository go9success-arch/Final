package com.aim.lifemate.ui.screens.ai

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.aim.lifemate.viewmodel.AIViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIAssistantScreen(navController: NavHostController) {
    val viewModel: AIViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("AI Assistant") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Chat Messages
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                reverseLayout = true
            ) {
                items(state.messages.reversed()) { message ->
                    ChatMessageBubble(message)
                }
            }

            // Input Section
            InputSection(
                isLoading = state.isLoading,
                onSendMessage = viewModel::sendMessage,
                onVoiceInput = viewModel::startVoiceInput
            )
        }
    }
}

@Composable
fun ChatMessageBubble(message: AIMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            shape = RoundedCornerShape(
                topStart = if (message.isUser) 16.dp else 4.dp,
                topEnd = if (message.isUser) 4.dp else 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (message.isUser) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(16.dp),
                color = if (message.isUser) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun InputSection(isLoading: Boolean, onSendMessage: (String) -> Unit, onVoiceInput: () -> Unit) {
    var message by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            placeholder = { Text("Ask me anything...") },
            modifier = Modifier.weight(1f),
            trailingIcon = {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                }
            }
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = onVoiceInput,
            enabled = !isLoading
        ) {
            Icon(Icons.Default.Mic, "Voice Input")
        }

        IconButton(
            onClick = {
                if (message.isNotBlank()) {
                    onSendMessage(message)
                    message = ""
                }
            },
            enabled = message.isNotBlank() && !isLoading
        ) {
            Icon(Icons.Default.Send, "Send")
        }
    }
}