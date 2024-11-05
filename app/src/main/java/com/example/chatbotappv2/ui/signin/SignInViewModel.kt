package com.example.chatbotappv2.ui.signin

import android.util.Log
import androidx.datastore.core.IOException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.chatbotappv2.ChatBotApp
import com.example.chatbotappv2.data.UserPreferencesRepository
import com.example.chatbotappv2.data.UserRepo
import com.example.chatbotappv2.network.req.LoginReq
import com.example.chatbotappv2.util.JsonConverter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException


private const val TAG = "SignInViewModel"

class SignInViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val userRepo: UserRepo
) : ViewModel() {
    private var _signInUiState = MutableStateFlow(SignInUiState())
    val signInUiState = _signInUiState.asStateFlow()

    fun onUsernameChange(username: String) {
        _signInUiState.update {
            it.copy(username = username)
        }
    }

    fun onPasswordChange(password: String) {
        _signInUiState.update {
            it.copy(password = password)
        }
    }


    fun onSignInClick() {
        viewModelScope.launch {
            _signInUiState.update {
                it.copy(isLoading = true)
            }
            val req = LoginReq(
                username = _signInUiState.value.username,
                password = _signInUiState.value.password
            )
            delay(3000) //Stimulate network call
            try {
                val apiRes = userRepo.login(_signInUiState.value.username, _signInUiState.value.password)
                Log.i(TAG, apiRes.toString())

                apiRes.data?.let {
                    userPreferencesRepository.saveAccToken(it.accessToken)
                    _signInUiState.value = SignInUiState(isLoggedIn = true)
                    Log.i(TAG, "Login success")
                }

            } catch (ex: HttpException) {
                val errorRes = JsonConverter.toErrorRes(ex.response()?.errorBody()?.string())
                errorRes?.let {
                    Log.i(TAG, it.toString())
                    Log.e(TAG, "HttpException: ${it.message}")
                    _signInUiState.update { curState ->
                        curState.copy(error = errorRes.message, isLoading = false)
                    }
                }
            } catch (ex: IOException) {
                Log.e(TAG, "IOException: ${ex.message}")
                _signInUiState.update {
                    it.copy(
                        error = "Network error, you may not have internet connection",
                        isLoading = false
                    )
                }
            } catch (ex: Exception) {
                Log.e(TAG, "Exception: ${ex.message}")
                _signInUiState.update {
                    it.copy(
                        error = "Something went wrong, please try again later",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun errorShown() {
        _signInUiState.update {
            it.copy(error = null)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as ChatBotApp)
                SignInViewModel(application.userPreferencesRepository, application.container.userRepo)
            }
        }
    }
}