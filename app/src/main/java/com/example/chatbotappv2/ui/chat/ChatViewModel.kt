package com.example.chatbotappv2.ui.chat

import android.content.Context
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
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
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

private const val TAG = "ChatViewModel"
private const val modelName = "gemini-1.5-flash-001"
private const val apiKey = "AIzaSyAwKSOk62qnqVLOSzS3s3Sm0j2I4UZJ39I"
private val model = GenerativeModel(
    modelName = modelName,
    apiKey = apiKey
)

class ChatViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private var _chatUiState = MutableStateFlow(ChatUiState())
    val chatUiState = _chatUiState.asStateFlow()

    fun onInputChange(input: String) {
        _chatUiState.update { it.copy(input = input) }
    }

    fun setSelectedMediaUri(uri: Uri) {
        _chatUiState.update { it.copy(selectedMediaUri = uri) }
    }

    fun generateThumbnail(context: Context, uri: Uri) {
        val thumbnail = ThumbnailUtils.createVideoThumbnail(
            uri.path ?: "", MediaStore.Images.Thumbnails.MINI_KIND
        )
        _chatUiState.update { it.copy(thumbnail = thumbnail) }
    }

    private fun getMimeType(context: Context, uri: Uri?): String {
        return uri?.let { context.contentResolver.getType(it) } ?: "application/octet-stream"
    }

    private fun convertUriToByteArray(context: Context, uri: Uri?): ByteArray? {
        val inputStream = uri?.let { context.contentResolver.openInputStream(it) }
        return inputStream?.readBytes()
    }

    private fun generateMultiTurnChat(messageList: List<Message>): Chat {
        return model.startChat(
            history = messageList.map {
                if (it.role == Role.user) {
                    content(role = "user") { text(it.message) }
                } else {
                    content(role = "model") { text(it.message) }
                }
            }
        )
    }

    private suspend fun generateTextFromMedia(
        mimeType: String,
        input: String,
        fileData: ByteArray?
    ): GenerateContentResponse {
        return model.generateContent(
            content {
                if (fileData != null) {
                    blob(mimeType, fileData)
                }
                text(input)
            })
    }

    fun chatWithGemini(context: Context) {
        viewModelScope.launch {
            val input = _chatUiState.value.input
            val messageList = _chatUiState.value.messageList.toMutableList()
            messageList.add(Message(message = input, role = Role.user))
            _chatUiState.update { it.copy(isLoading = true, input = "", messageList = messageList) }

            val fileData =
                _chatUiState.value.selectedMediaUri?.let { convertUriToByteArray(context, it) }

            val mimeType = getMimeType(context, _chatUiState.value.selectedMediaUri)

            // TODO: Call API
            delay(3000) //Stimulate network call
            try {
                val response =
                    if (_chatUiState.value.selectedMediaUri == null) generateMultiTurnChat(
                        messageList
                    ).sendMessage(input)
                    else generateTextFromMedia(mimeType, input, fileData)

                Log.i(TAG, response.toString())
                messageList.add(
                    Message(
                        message = response.text ?: "Something went wrong",
                        role = Role.model
                    )
                )
                _chatUiState.update {
                    it.copy(
                        isLoading = false,
                        messageList = messageList,
                        selectedMediaUri = null,
                        thumbnail = null
                    )
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
                    _chatUiState.value = ChatUiState(messageList = messageList)
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
        _chatUiState.update {
            it.copy(
                errorMessage = null, isLoading = false, selectedMediaUri = null, thumbnail = null
            )
        }
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