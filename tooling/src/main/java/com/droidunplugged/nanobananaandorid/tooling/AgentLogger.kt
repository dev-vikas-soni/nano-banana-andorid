package com.droidunplugged.nanobananaandorid.tooling

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A singleton logger to track agent execution states.
 * This powers the Agent Observability UI.
 */
@Singleton
class AgentLogger @Inject constructor() {
    private val _events = MutableSharedFlow<ExecutionEvent>(extraBufferCapacity = 50)
    val events: SharedFlow<ExecutionEvent> = _events.asSharedFlow()

    fun logEvent(event: ExecutionEvent) {
        _events.tryEmit(event)
    }
}
