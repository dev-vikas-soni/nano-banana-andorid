package com.droidunplugged.nanobananaandorid.feature.agent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.droidunplugged.nanobananaandorid.tooling.ExecutionEvent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentObservabilitySheet(
    viewModel: ExecutionStateViewModel = hiltViewModel()
) {
    val events by viewModel.events.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f) // Half screen height for bottom sheet
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Agent Execution Flow",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Gemini Nano (On-Device)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (events.isEmpty()) {
            Text("Waiting for agent tasks...", style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyColumn {
                items(events) { event ->
                    EventRow(event)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun EventRow(event: ExecutionEvent) {
    val timeFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
    val timeStr = timeFormat.format(Date(event.timestamp))

    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "[$timeStr]",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            modifier = Modifier.width(90.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        
        Column {
            when (event) {
                is ExecutionEvent.AgentStarted -> {
                    Text("🤖 Agent Started", fontWeight = FontWeight.Bold)
                    Text("Task: ${event.taskId}", style = MaterialTheme.typography.bodySmall)
                }
                is ExecutionEvent.ToolExecutionStarted -> {
                    Text("🛠️ Executing Tool: ${event.toolName}")
                }
                is ExecutionEvent.ToolExecutionCompleted -> {
                    Text("✅ Tool Completed: ${event.toolName} (${event.durationMs}ms)")
                }
                is ExecutionEvent.GeneratingDraft -> {
                    Text("✍️ Generating ${event.target}...")
                }
                is ExecutionEvent.AgentCompleted -> {
                    Text("🎉 Agent Completed (${event.totalDurationMs}ms)", fontWeight = FontWeight.Bold)
                }
                is ExecutionEvent.Error -> {
                    Text("❌ Error: ${event.message}", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
