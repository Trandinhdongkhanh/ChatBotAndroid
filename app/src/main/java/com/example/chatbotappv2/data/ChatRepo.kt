package com.example.chatbotappv2.data

import com.example.chatbotappv2.network.ChatBotApiService
import com.example.chatbotappv2.model.req.ChatReq
import com.example.chatbotappv2.model.res.ApiRes
import com.example.chatbotappv2.model.res.ChatSuccess

interface ChatRepo {
    suspend fun chat(input: String, accToken: String): ApiRes<ChatSuccess>
}

class NetworkChatRepo(
    private val chatBotApiService: ChatBotApiService,
) : ChatRepo {
    override suspend fun chat(input: String, accToken: String): ApiRes<ChatSuccess> {
        val jsonBody = ChatReq(input)
        val header = "Bearer $accToken"
        return chatBotApiService.chat(header, jsonBody)
    }
}