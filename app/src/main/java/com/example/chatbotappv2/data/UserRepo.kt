package com.example.chatbotappv2.data

import com.example.chatbotappv2.network.ChatBotApiService
import com.example.chatbotappv2.model.req.LoginReq
import com.example.chatbotappv2.model.req.SignUpReq
import com.example.chatbotappv2.model.res.ApiRes
import com.example.chatbotappv2.model.res.LoginSuccess
import com.example.chatbotappv2.model.res.SignUpSuccess

interface UserRepo {
    suspend fun signUpNewUser(
        username: String,
        password: String,
        fullName: String
    ): ApiRes<SignUpSuccess>
    suspend fun login(username: String, password: String) : ApiRes<LoginSuccess>
}

class NetworkUserRepo(
    private val chatBotApiService: ChatBotApiService
) : UserRepo {
    override suspend fun signUpNewUser(
        username: String,
        password: String,
        fullName: String
    ): ApiRes<SignUpSuccess> {
        val req = SignUpReq(fullName, password, username)
        return chatBotApiService.signUp(req)
    }

    override suspend fun login(username: String, password: String): ApiRes<LoginSuccess> {
        val req = LoginReq(password, username)
        return chatBotApiService.login(req)
    }
}