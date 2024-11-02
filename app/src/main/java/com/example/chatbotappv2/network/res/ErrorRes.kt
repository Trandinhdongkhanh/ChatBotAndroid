package com.example.chatbotappv2.network.res

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorRes(
    @SerialName("code")
    val code: Int,
    @SerialName("message")
    val message: String,
    @SerialName("status")
    val status: String
)