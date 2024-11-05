package com.example.chatbotappv2.model.req

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginReq(
    @SerialName("password")
    val password: String,
    @SerialName("username")
    val username: String
)