package com.droidunplugged.nanobananaandorid.airuntime

import android.content.Context
import android.util.Log
import com.google.ai.edge.aicore.GenerativeModel
import com.google.ai.edge.aicore.GenerationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RealGeminiNanoClient(private val context: Context) : GeminiNanoClient {
    override suspend fun generateContent(prompt: String): String {
        return withContext(Dispatchers.IO) {
            try {
                // Configure the real on-device model
                val builder = GenerationConfig.Builder()
                builder.temperature = 0.7f
                val config = builder.build()
                    
                val generativeModel = GenerativeModel(
                    generationConfig = config
                )

                val response = generativeModel.generateContent(prompt)
                response.text ?: "Error: Received empty response from Gemini Nano."
            } catch (e: Exception) {
                Log.e("RealGeminiNanoClient", "Error running Gemini Nano", e)
                "Error: Gemini Nano failed or is not supported on this device.\n\nException: ${e.message}\n\nDid you remember to test on a supported device (e.g., Pixel 9) with AICore installed?"
            }
        }
    }
}
