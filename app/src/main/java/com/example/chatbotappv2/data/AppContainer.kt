package com.example.chatbotappv2.data

import com.example.chatbotappv2.network.ChatBotApiService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

interface AppContainer {
    val userRepo: UserRepo
    val chatRepo: ChatRepo
}

private const val HOST = "192.168.1.4"
private const val PORT = "8080"
private const val PREFIX = "api/v1/"
private const val APPLICATION_JSON = "application/json"

class DefaultAppContainer : AppContainer {
    private val baseUrl = "http://${HOST}:${PORT}/${PREFIX}"

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory(APPLICATION_JSON.toMediaType()))
        .baseUrl(baseUrl)
        .build()

    private val retrofitService by lazy { retrofit.create(ChatBotApiService::class.java) }

    override val userRepo: UserRepo by lazy { NetworkUserRepo(retrofitService) }
    override val chatRepo: ChatRepo by lazy { NetworkChatRepo(retrofitService) }
}