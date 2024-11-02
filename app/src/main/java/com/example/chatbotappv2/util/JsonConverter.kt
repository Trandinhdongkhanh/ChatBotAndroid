package com.example.chatbotappv2.util

import android.util.Log
import com.example.chatbotappv2.network.res.ErrorRes
import kotlinx.serialization.json.Json

private const val TAG = "JsonConverter"

class JsonConverter {
    companion object {
        fun toErrorRes(jsonString: String?): ErrorRes? {
            return try {
                Json.decodeFromString<ErrorRes>(jsonString ?: "")
            } catch (ex: Exception) {
                Log.e(TAG, "Exception: ${ex.message}")
                null
            }
        }
    }
}