package com.example.chatbotappv2.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val ACC_TOKEN = stringPreferencesKey("acc_token")
        const val TAG = "UserPreferencesRepo"
    }

    suspend fun saveAccToken(accToken: String) {
        dataStore.edit {
            it[ACC_TOKEN] = accToken
        }
    }

    val accToken: Flow<String> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map {
            it[ACC_TOKEN] ?: ""
        }
}