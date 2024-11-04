package com.example.chatbotappv2.ui.chat

import android.graphics.Bitmap
import android.net.Uri

data class ChatUiState(
    val input: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val messageList: List<Message> = emptyList(),
    val selectedMediaUri: Uri? = null,
    val thumbnail: Bitmap? = null
)

data class Message(
    val message: String,
    val role: Role
)

enum class Role {
    user,
    model
}