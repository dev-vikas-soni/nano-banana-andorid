package com.droidunplugged.nanobananaandorid.feature.chat

import com.droidunplugged.nanobananaandorid.airuntime.AdkOrchestrator
import com.droidunplugged.nanobananaandorid.airuntime.GeminiNanoClient
import com.droidunplugged.nanobananaandorid.data.ChatDao
import com.droidunplugged.nanobananaandorid.data.ChatRepository
import com.droidunplugged.nanobananaandorid.data.MessageEntity
import com.droidunplugged.nanobananaandorid.tooling.AgentLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private class FakeChatDao : ChatDao {
        val _messages = MutableStateFlow<List<MessageEntity>>(emptyList())
        override fun getAllMessages(): Flow<List<MessageEntity>> = _messages

        override suspend fun insertMessage(message: MessageEntity) {
            _messages.value = _messages.value + message
        }

        override suspend fun clearAllMessages() {
            _messages.value = emptyList()
        }
    }

    private lateinit var fakeChatDao: FakeChatDao
    private lateinit var repository: ChatRepository
    private lateinit var orchestrator: AdkOrchestrator
    private lateinit var viewModel: ChatViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeChatDao = FakeChatDao()
        repository = ChatRepository(fakeChatDao)
        
        val fakeClient = object : GeminiNanoClient {
            override suspend fun generateContent(prompt: String): String {
                return "Drafted response for: $prompt"
            }
        }
        val logger = AgentLogger()
        orchestrator = AdkOrchestrator(fakeClient, fakeClient, logger)
        viewModel = ChatViewModel(repository, orchestrator)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testSendMessageAddsMessageToRepository() = runTest(testDispatcher) {
        viewModel.sendMessage("Hello World")
        testDispatcher.scheduler.advanceUntilIdle()

        val messagesList = fakeChatDao._messages.value
        assertEquals(1, messagesList.size)
        assertEquals("Hello World", messagesList[0].text)
        assertTrue(messagesList[0].isFromUser)
    }

    @Test
    fun testGenerateReplySetsDraftAndLoadingStates() = runTest(testDispatcher) {
        val currentMessages = listOf(
            MessageEntity(text = "Hi", isFromUser = true),
            MessageEntity(text = "Hello", isFromUser = false)
        )

        assertFalse(viewModel.isGenerating.value)
        assertEquals("", viewModel.draft.value)

        viewModel.generateReply(currentMessages)
        
        // Advance until the orchestrator is running but before it completes
        runCurrent()
        // verify that generating state is true
        assertTrue(viewModel.isGenerating.value)

        // Advance all tasks to complete the flow
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.isGenerating.value)
        assertTrue(viewModel.draft.value.contains("Drafted response for:"))
    }

    @Test
    fun testSendDraftClearsDraftAndAddsMessageToRepository() = runTest(testDispatcher) {
        viewModel.updateDraft("This is a draft reply")
        assertEquals("This is a draft reply", viewModel.draft.value)

        viewModel.sendDraft()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("", viewModel.draft.value)
        val messagesList = fakeChatDao._messages.value
        assertEquals(1, messagesList.size)
        assertEquals("This is a draft reply", messagesList[0].text)
        assertFalse(messagesList[0].isFromUser)
    }

    @Test
    fun testClearHistoryClearsRepository() = runTest(testDispatcher) {
        fakeChatDao.insertMessage(MessageEntity(text = "Test", isFromUser = true))
        assertEquals(1, fakeChatDao._messages.value.size)

        viewModel.clearHistory()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(0, fakeChatDao._messages.value.size)
    }
}
