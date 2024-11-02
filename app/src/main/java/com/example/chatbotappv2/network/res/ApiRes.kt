package com.example.chatbotappv2.network.res

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName


@Serializable
data class ApiRes<T>(
    @SerialName("code")
    val code: Int,
    @SerialName("data")
    val data: T?
)

