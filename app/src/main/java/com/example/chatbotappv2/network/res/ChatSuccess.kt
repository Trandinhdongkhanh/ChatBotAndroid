package com.example.chatbotappv2.network.res
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatSuccess(
    @SerialName("res")
    val res: String
)



