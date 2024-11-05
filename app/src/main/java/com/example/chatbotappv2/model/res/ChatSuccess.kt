package com.example.chatbotappv2.model.res
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatSuccess(
    @SerialName("res")
    val res: String
)



