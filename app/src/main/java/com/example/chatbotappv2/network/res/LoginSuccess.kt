package com.example.chatbotappv2.network.res

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginSuccess(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("access_token_expiry")
    val accessTokenExpiry: Int,
    @SerialName("refresh_token")
    val refreshToken: String,
    @SerialName("token_type")
    val tokenType: String
)