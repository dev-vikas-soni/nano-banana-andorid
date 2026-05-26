package com.droidunplugged.nanobananaandorid.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidunplugged.nanobananaandorid.airuntime.AdkOrchestrator
import com.droidunplugged.nanobananaandorid.data.ChatRepository
import com.droidunplugged.nanobananaandorid.data.MessageEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository,
    private val orchestrator: AdkOrchestrator
) : ViewModel() {

    val messages = repository.messages

    private val _draft = MutableStateFlow("")
    val draft: StateFlow<String> = _draft.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _useRealNano = MutableStateFlow(false)
    val useRealNano: StateFlow<Boolean> = _useRealNano.asStateFlow()

    fun setUseRealNano(value: Boolean) {
        _useRealNano.value = value
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.addMessage(text, isFromUser = true)
        }
    }

    fun generateReply(currentMessages: List<MessageEntity>) {
        if (currentMessages.isEmpty()) return
        
        viewModelScope.launch {
            _isGenerating.value = true
            
            // Format history for the agent
            val history = currentMessages.map { 
                val sender = if (it.isFromUser) "User" else "Agent"
                "$sender: ${it.text}"
            }
            
            // Invoke the AI Orchestrator
            val generatedDraft = orchestrator.summarizeAndDraft(history, _useRealNano.value)
            
            _draft.value = generatedDraft
            _isGenerating.value = false
        }
    }

    fun updateDraft(text: String) {
        _draft.value = text
    }

    fun sendDraft() {
        if (_draft.value.isBlank()) return
        viewModelScope.launch {
            repository.addMessage(_draft.value, isFromUser = false)
            _draft.value = ""
        }
    }
    
    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}
