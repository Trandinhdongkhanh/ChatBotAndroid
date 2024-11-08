package com.example.chatbotappv2.ui.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.chatbotappv2.ChatBotApp
import com.example.chatbotappv2.data.UserRepo
import com.example.chatbotappv2.model.req.SignUpReq
import com.example.chatbotappv2.util.JsonConverter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

private const val TAG = "SignUpViewModel"

class SignUpViewModel(
    private val userRepo: UserRepo
) : ViewModel() {
    private var _signUpUiState = MutableStateFlow(SignUpUiState())
    val signUpUiState = _signUpUiState.asStateFlow()

    fun reset() {
        _signUpUiState.value = SignUpUiState()
    }

    fun onUsernameChange(username: String) {
        _signUpUiState.update {
            it.copy(username = username)
        }
    }

    fun onPasswordChange(password: String) {
        _signUpUiState.update {
            it.copy(password = password)
        }
    }

    fun onFullNameChange(fullName: String) {
        _signUpUiState.update {
            it.copy(fullName = fullName)
        }
    }

    fun onConfirmClick() {
        viewModelScope.launch {
            _signUpUiState.update {
                it.copy(isLoading = true)
            }

            // TODO: Call API
            delay(3000) //Stimulate network call
            try {
                val req = SignUpReq(
                    username = _signUpUiState.value.username,
                    password = _signUpUiState.value.password,
                    fullName = _signUpUiState.value.fullName
                )

                val apiRes = userRepo.signUpNewUser(
                    _signUpUiState.value.username,
                    _signUpUiState.value.password,
                    _signUpUiState.value.fullName
                )
                Log.i(TAG, apiRes.toString())
                _signUpUiState.value = SignUpUiState(isSignedUp = true)
                Log.i(TAG, "Login success")
            } catch (ex: HttpException) {
                val errorRes = JsonConverter.toErrorRes(ex.response()?.errorBody()?.string())
                errorRes?.let {
                    Log.i(TAG, it.toString())
                    Log.e(TAG, "HttpException: ${it.message}")
                    _signUpUiState.update { curState ->
                        curState.copy(errorMessage = errorRes.message, isLoading = false)
                    }
                }
            } catch (ex: IOException) {
                Log.e(TAG, "IOException: ${ex.message}")
                _signUpUiState.update {
                    it.copy(
                        errorMessage = "Network error, you may not have internet connection",
                        isLoading = false
                    )
                }
            } catch (ex: Exception) {
                Log.e(TAG, "Exception: ${ex.message}")
                _signUpUiState.update {
                    it.copy(
                        errorMessage = "Something went wrong, please try again later",
                        isLoading = false
                    )
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as ChatBotApp)
                SignUpViewModel(application.container.userRepo)
            }
        }
    }

    fun errorShown() {
        _signUpUiState.update {
            it.copy(errorMessage = null, isLoading = false)
        }
    }
}