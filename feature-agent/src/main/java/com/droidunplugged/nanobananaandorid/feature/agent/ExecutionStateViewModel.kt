package com.droidunplugged.nanobananaandorid.feature.agent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidunplugged.nanobananaandorid.tooling.AgentLogger
import com.droidunplugged.nanobananaandorid.tooling.ExecutionEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExecutionStateViewModel @Inject constructor(
    private val agentLogger: AgentLogger
) : ViewModel() {

    private val _events = MutableStateFlow<List<ExecutionEvent>>(emptyList())
    val events: StateFlow<List<ExecutionEvent>> = _events.asStateFlow()

    init {
        viewModelScope.launch {
            agentLogger.events.collect { event ->
                _events.value = _events.value + event
            }
        }
    }
    
    fun clearEvents() {
        _events.value = emptyList()
    }
}
