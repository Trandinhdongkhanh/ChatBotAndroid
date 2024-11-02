package com.example.chatbotappv2.ui.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.chatbotappv2.ChatBotApp
import com.example.chatbotappv2.data.UserPreferencesRepository
import com.example.chatbotappv2.network.ChatBotAPI
import com.example.chatbotappv2.network.req.ChatReq
import com.example.chatbotappv2.util.JsonConverter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

private const val TAG = "ChatViewModel"

class ChatViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private var _chatUiState = MutableStateFlow(ChatUiState())
    val chatUiState = _chatUiState.asStateFlow()

    fun onInputChange(input: String) {
        _chatUiState.update { it.copy(input = input) }
    }

    fun sendMessage() {
        viewModelScope.launch {
            val input = _chatUiState.value.input
            val messageList = _chatUiState.value.messageList.toMutableList()
            messageList.add(Message(message = input, role = Role.user))
            _chatUiState.update { it.copy(isLoading = true, input = "", messageList = messageList) }

            // TODO: Call API
            delay(3000) //Stimulate network call
            try {
                val accToken = userPreferencesRepository.accToken.first()
                val body = ChatReq(question = input)
                val apiRes = ChatBotAPI.retrofitService.chat(token = "Bearer $accToken", req = body)
                Log.i(TAG, apiRes.toString())
                apiRes.data?.let {
                    messageList.add(Message(message = it.res, role = Role.model))
                    Log.i(TAG, "Chat success")
                    _chatUiState.update { curState ->
                        curState.copy(isLoading = false, messageList = messageList) }
                }
            } catch (ex: HttpException) {
                val errorRes = JsonConverter.toErrorRes(ex.response()?.errorBody()?.string())
                errorRes?.let {
                    Log.i(TAG, it.toString())
                    Log.e(TAG, "HttpException: ${it.message}")
                    _chatUiState.update { curState ->
                        curState.copy(errorMessage = errorRes.message, isLoading = false)
                    }
                }
            } catch (ex: IOException) {
                Log.e(TAG, "IOException: ${ex.message}")
                _chatUiState.update {
                    it.copy(
                        errorMessage = "Network error, you may not have internet connection",
                        isLoading = false
                    )
                }
            } catch (ex: Exception) {
                Log.e(TAG, "Exception: ${ex.message}")
                _chatUiState.update {
                    it.copy(
                        errorMessage = "Something went wrong, please try again later",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun errorShown() {
        _chatUiState.update { it.copy(errorMessage = null, isLoading = false) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as ChatBotApp)
                ChatViewModel(application.userPreferencesRepository)
            }
        }
    }
}