package com.droidunplugged.nanobananaandorid.airuntime

import com.droidunplugged.nanobananaandorid.tooling.AgentLogger
import com.droidunplugged.nanobananaandorid.tooling.ExecutionEvent
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AdkOrchestratorTest {

    @Test
    fun testSummarizeAndDraftLogsEventsAndReturnsDraft() = runTest {
        val fakeClient = object : GeminiNanoClient {
            override suspend fun generateContent(prompt: String): String {
                return "Mock draft for prompt: $prompt"
            }
        }
        val realClientMock = object : GeminiNanoClient {
            override suspend fun generateContent(prompt: String): String {
                return "Real Mock draft for prompt: $prompt"
            }
        }
        val agentLogger = AgentLogger()
        val orchestrator = AdkOrchestrator(fakeClient, realClientMock, agentLogger)

        val events = mutableListOf<ExecutionEvent>()
        val collectJob = launch {
            agentLogger.events.collect {
                events.add(it)
            }
        }
        
        // Yield to allow the collector to register as a subscriber of the SharedFlow
        yield()

        val chatHistory = listOf("User: Hello", "Agent: Hi there")
        val result = orchestrator.summarizeAndDraft(chatHistory)

        // Allow any remaining collection work to complete
        runCurrent()
        collectJob.cancel()

        assertEquals("Mock draft for prompt: Based on this context: Conversation involves 2 messages about Android architecture., draft a professional reply.", result)

        assertEquals(5, events.size)
        assertTrue(events[0] is ExecutionEvent.AgentStarted)
        assertTrue(events[1] is ExecutionEvent.ToolExecutionStarted)
        assertEquals("ReadThreadContext", (events[1] as ExecutionEvent.ToolExecutionStarted).toolName)
        assertTrue(events[2] is ExecutionEvent.ToolExecutionCompleted)
        assertEquals("ReadThreadContext", (events[2] as ExecutionEvent.ToolExecutionCompleted).toolName)
        assertTrue(events[3] is ExecutionEvent.GeneratingDraft)
        assertTrue(events[4] is ExecutionEvent.AgentCompleted)
    }
}
