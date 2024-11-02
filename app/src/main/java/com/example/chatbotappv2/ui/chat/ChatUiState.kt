package com.example.chatbotappv2.ui.chat

data class ChatUiState(
    val input: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val messageList: List<Message> = emptyList()
)

data class Message(
    val message: String,
    val role: Role
)

enum class Role {
    user,
    model
}