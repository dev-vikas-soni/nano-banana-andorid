package com.droidunplugged.nanobananaandorid.tooling

import java.util.UUID

/**
 * Represents internal events emitted by the AI Orchestrator.
 * These are used to power the Agent Observability UI.
 */
sealed class ExecutionEvent {
    val id: String = UUID.randomUUID().toString()
    val timestamp: Long = System.currentTimeMillis()

    data class AgentStarted(val taskId: String) : ExecutionEvent()
    data class ToolExecutionStarted(val toolName: String) : ExecutionEvent()
    data class ToolExecutionCompleted(val toolName: String, val durationMs: Long, val success: Boolean) : ExecutionEvent()
    data class GeneratingDraft(val target: String = "Response") : ExecutionEvent()
    data class AgentCompleted(val taskId: String, val totalDurationMs: Long) : ExecutionEvent()
    data class Error(val message: String) : ExecutionEvent()
}
