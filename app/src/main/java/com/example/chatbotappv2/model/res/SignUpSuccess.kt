package com.example.chatbotappv2.model.res

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignUpSuccess(
    @SerialName("username")
    val username: String,
    @SerialName("full_name")
    val fullName: String
)