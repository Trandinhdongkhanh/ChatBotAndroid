package com.example.chatbotappv2.network

import com.example.chatbotappv2.model.req.ChatReq
import com.example.chatbotappv2.model.req.LoginReq
import com.example.chatbotappv2.model.req.SignUpReq
import com.example.chatbotappv2.model.res.ApiRes
import com.example.chatbotappv2.model.res.ChatSuccess
import com.example.chatbotappv2.model.res.LoginSuccess
import com.example.chatbotappv2.model.res.SignUpSuccess
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ChatBotApiService {
    @POST("user/login")
    suspend fun login(@Body req: LoginReq): ApiRes<LoginSuccess>
    @POST("user")
    suspend fun signUp(@Body req: SignUpReq): ApiRes<SignUpSuccess>
    @POST("chat")
    suspend fun chat(@Header("Authorization") token: String, @Body req: ChatReq): ApiRes<ChatSuccess>
}
