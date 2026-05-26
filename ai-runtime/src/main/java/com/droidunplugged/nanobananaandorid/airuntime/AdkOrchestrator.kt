package com.droidunplugged.nanobananaandorid.airuntime

import com.droidunplugged.nanobananaandorid.tooling.AgentLogger
import com.droidunplugged.nanobananaandorid.tooling.ExecutionEvent
import kotlinx.coroutines.delay
import java.util.UUID
import javax.inject.Inject

/**
 * Orchestrates the AI tasks, abstracting away the raw LLM calls.
 * This class coordinates context gathering, tool calling (simulated), and final generation.
 */
class AdkOrchestrator @Inject constructor(
    @FakeNano private val fakeNanoClient: GeminiNanoClient,
    @RealNano private val realNanoClient: GeminiNanoClient,
    private val logger: AgentLogger
) {

    suspend fun summarizeAndDraft(chatHistory: List<String>, useRealNano: Boolean = false): String {
        val taskId = UUID.randomUUID().toString()
        val startTime = System.currentTimeMillis()
        
        logger.logEvent(ExecutionEvent.AgentStarted(taskId))

        // Step 1: Read Thread Context (Simulated Tool)
        logger.logEvent(ExecutionEvent.ToolExecutionStarted("ReadThreadContext"))
        delay(500) // Simulate tool execution
        val contextSummary = "Conversation involves ${chatHistory.size} messages about Android architecture."
        logger.logEvent(ExecutionEvent.ToolExecutionCompleted("ReadThreadContext", 500, true))

        // Step 2: Generate Draft using Gemini Nano
        val clientName = if (useRealNano) "Real On-Device NPU" else "Simulated Nano"
        logger.logEvent(ExecutionEvent.GeneratingDraft("Professional Response via $clientName"))
        val prompt = "Based on this context: $contextSummary, draft a professional reply."
        
        val activeClient: GeminiNanoClient = if (useRealNano) realNanoClient else fakeNanoClient
        val draft = activeClient.generateContent(prompt)
        
        val totalDuration = System.currentTimeMillis() - startTime
        logger.logEvent(ExecutionEvent.AgentCompleted(taskId, totalDuration))

        return draft
    }
}
