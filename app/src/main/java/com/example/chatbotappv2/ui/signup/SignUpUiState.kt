package com.example.chatbotappv2.ui.signup

data class SignUpUiState(
    val username: String = "",
    val password: String = "",
    val fullName: String = "",
    val isError: Boolean = false,
    val isLoading: Boolean = false,
    val isSignedUp: Boolean = false,
    val errorMessage: String? = null
)
