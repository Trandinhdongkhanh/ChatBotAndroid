package com.example.chatbotappv2

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.chatbotappv2.data.AppContainer
import com.example.chatbotappv2.data.DefaultAppContainer
import com.example.chatbotappv2.data.UserPreferencesRepository

private const val USER_PREFERENCE = "user_preference"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = USER_PREFERENCE
)

class ChatBotApp : Application() {
    lateinit var userPreferencesRepository: UserPreferencesRepository
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        userPreferencesRepository = UserPreferencesRepository(dataStore)
        container = DefaultAppContainer()
    }
}