package com.example.chatbotappv2.network.req

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignUpReq(
    @SerialName("full_name")
    val fullName: String,
    @SerialName("password")
    val password: String,
    @SerialName("username")
    val username: String
)