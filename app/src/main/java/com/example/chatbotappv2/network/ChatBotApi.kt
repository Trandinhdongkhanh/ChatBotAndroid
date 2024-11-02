package com.example.chatbotappv2.network

import com.example.chatbotappv2.network.req.ChatReq
import com.example.chatbotappv2.network.req.LoginReq
import com.example.chatbotappv2.network.req.SignUpReq
import com.example.chatbotappv2.network.res.ApiRes
import com.example.chatbotappv2.network.res.ChatSuccess
import com.example.chatbotappv2.network.res.LoginSuccess
import com.example.chatbotappv2.network.res.SignUpSuccess
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

private const val HOST  = "192.168.1.4"
private const val PORT = "8080"
private const val PREFIX = "api/v1/"
private const val BASE_URL = "http://${HOST}:${PORT}/${PREFIX}"
private const val APPLICATION_JSON = "application/json"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(Json.asConverterFactory(APPLICATION_JSON.toMediaType()))
    .baseUrl(BASE_URL)
    .build()

interface ChatBotApiService {
    @POST("user/login")
    suspend fun login(@Body req: LoginReq): ApiRes<LoginSuccess>
    @POST("user")
    suspend fun signUp(@Body req: SignUpReq): ApiRes<SignUpSuccess>
    @POST("chat")
    suspend fun chat(@Header("Authorization") token: String, @Body req: ChatReq): ApiRes<ChatSuccess>
}

object ChatBotAPI {
    val retrofitService: ChatBotApiService by lazy {
        retrofit.create(ChatBotApiService::class.java)
    }
}