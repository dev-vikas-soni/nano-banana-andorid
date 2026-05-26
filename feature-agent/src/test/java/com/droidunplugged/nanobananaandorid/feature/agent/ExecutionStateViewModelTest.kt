package com.droidunplugged.nanobananaandorid.feature.agent

import com.droidunplugged.nanobananaandorid.tooling.AgentLogger
import com.droidunplugged.nanobananaandorid.tooling.ExecutionEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExecutionStateViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testViewModelCollectsEventsFromLogger() = runTest(testDispatcher) {
        val logger = AgentLogger()
        val viewModel = ExecutionStateViewModel(logger)

        // Run queued tasks (specifically the ViewModel's collection launch block) to register the subscriber
        runCurrent()

        // Verify initial state is empty
        assertEquals(0, viewModel.events.value.size)

        // Log some events
        val event1 = ExecutionEvent.AgentStarted("task-123")
        val event2 = ExecutionEvent.ToolExecutionStarted("ReadContext")

        logger.logEvent(event1)
        logger.logEvent(event2)

        // Trigger coroutines to run
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify viewmodel has captured the events
        assertEquals(2, viewModel.events.value.size)
        assertTrue(viewModel.events.value[0] is ExecutionEvent.AgentStarted)
        assertEquals("task-123", (viewModel.events.value[0] as ExecutionEvent.AgentStarted).taskId)
        assertTrue(viewModel.events.value[1] is ExecutionEvent.ToolExecutionStarted)
        assertEquals("ReadContext", (viewModel.events.value[1] as ExecutionEvent.ToolExecutionStarted).toolName)

        // Test clear
        viewModel.clearEvents()
        assertEquals(0, viewModel.events.value.size)
    }
}
