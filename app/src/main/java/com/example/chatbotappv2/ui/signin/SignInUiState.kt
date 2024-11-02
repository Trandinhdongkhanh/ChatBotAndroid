package com.example.chatbotappv2.ui.signin

data class SignInUiState(
    val username: String = "",
    val password: String = "",
    val error: String? = null,
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false
)