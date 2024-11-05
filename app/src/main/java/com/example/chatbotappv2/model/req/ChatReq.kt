package com.example.chatbotappv2.model.req

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ChatReq(
    @SerialName("question")
    val question: String
)