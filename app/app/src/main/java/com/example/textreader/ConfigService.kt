package com.example.textreader

import android.content.Context
import android.util.Log
import org.json.JSONObject
import java.io.IOException

fun getAppConfig(context: Context): JSONObject {
    return try {
        val jsonString = context.assets.open("app_config.json").bufferedReader().use { it.readText() }
        Log.d("ConfigService", "Local JSON Loaded: $jsonString")
        JSONObject(jsonString)
    } catch (ioException: IOException) {
        ioException.printStackTrace()
        Log.e("ConfigService", "Error loading local JSON: ${ioException.message}")
        JSONObject()
    }
}

fun getAppVersion(context: Context): String {
    return try {
        val appConfig = getAppConfig(context)
        appConfig.optString("version", "Unknown")
    } catch (e: Exception) {
        e.printStackTrace()
        "Unknown"
    }
}