package com.droidunplugged.nanobananaandorid.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val chatDao: ChatDao
) {
    val messages: Flow<List<MessageEntity>> = chatDao.getAllMessages()

    suspend fun addMessage(text: String, isFromUser: Boolean) {
        chatDao.insertMessage(MessageEntity(text = text, isFromUser = isFromUser))
    }

    suspend fun clearHistory() {
        chatDao.clearAllMessages()
    }
}
