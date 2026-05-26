package com.droidunplugged.nanobananaandorid.airuntime

import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * Interface representing the on-device Gemini Nano capabilities.
 * In a real implementation, this would wrap Google's AICore or ML Kit APIs.
 */
interface GeminiNanoClient {
    suspend fun generateContent(prompt: String): String
}