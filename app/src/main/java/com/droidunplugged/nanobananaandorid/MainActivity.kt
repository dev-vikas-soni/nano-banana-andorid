package com.droidunplugged.nanobananaandorid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.droidunplugged.nanobananaandorid.feature.agent.AgentObservabilitySheet
import com.droidunplugged.nanobananaandorid.feature.chat.ChatScreen
import com.droidunplugged.nanobananaandorid.ui.theme.NanoBananaAndoridTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NanoBananaAndoridTheme {
                var showObservabilitySheet by remember { mutableStateOf(false) }
                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChatScreen(
                        onShowAgentObservability = {
                            showObservabilitySheet = true
                        }
                    )

                    if (showObservabilitySheet) {
                        ModalBottomSheet(
                            onDismissRequest = { showObservabilitySheet = false },
                            sheetState = sheetState
                        ) {
                            AgentObservabilitySheet()
                        }
                    }
                }
            }
        }
    }
}