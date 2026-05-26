package com.droidunplugged.nanobananaandorid.airuntime

import kotlinx.coroutines.delay
import javax.inject.Inject

class FakeGeminiNanoClient @Inject constructor() : GeminiNanoClient {
    override suspend fun generateContent(prompt: String): String {
        // Simulate on-device inference delay
        delay(1500)
        return "This is a drafted response based on the conversation context. It sounds professional and addresses the points."
    }
}
