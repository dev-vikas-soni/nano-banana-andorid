package com.droidunplugged.nanobananaandorid.feature.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    onShowAgentObservability: () -> Unit
) {
    val messages by viewModel.messages.collectAsState(initial = emptyList())
    val draft by viewModel.draft.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val useRealNano by viewModel.useRealNano.collectAsState()

    var inputText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agent Architecture Demo") },
                actions = {
                    TextButton(onClick = { viewModel.clearHistory() }) {
                        Text("Clear", color = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Message List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                reverseLayout = false
            ) {
                items(messages) { msg ->
                    MessageBubble(
                        text = msg.text,
                        isUser = msg.isFromUser
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Draft Editor Area (Only shows when a draft is ready)
            if (draft.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Drafted Reply", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                        OutlinedTextField(
                            value = draft,
                            onValueChange = { viewModel.updateDraft(it) },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { viewModel.updateDraft("") }) {
                                Text("Discard")
                            }
                            Button(onClick = { viewModel.sendDraft() }) {
                                Text("Send Reply")
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Action Buttons Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = useRealNano,
                        onCheckedChange = { viewModel.setUseRealNano(it) }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Real NPU", style = MaterialTheme.typography.labelSmall)
                }
                
                Button(
                    onClick = { 
                        onShowAgentObservability()
                        viewModel.generateReply(messages) 
                    },
                    enabled = messages.isNotEmpty() && !isGenerating && draft.isEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Icon(Icons.Default.Build, contentDescription = "Generate")
                    Spacer(Modifier.width(8.dp))
                    Text(if (isGenerating) "Generating..." else "Generate Reply")
                }
            }

            // User Input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message...") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        viewModel.sendMessage(inputText)
                        inputText = ""
                    },
                    modifier = Modifier.background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(50))
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun MessageBubble(text: String, isUser: Boolean) {
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val bgColor = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
    val textColor = if (isUser) Color.White else MaterialTheme.colorScheme.onSecondaryContainer

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(bgColor)
                .padding(12.dp)
        ) {
            Text(text = text, color = textColor)
        }
    }
}
