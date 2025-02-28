package com.example.textreader

import android.content.Context
import android.util.Log
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.IOException

fun getAppConfig(context: Context): JSONObject {
    val jsonString: String
    try {
        jsonString = context.assets.open("app_config.json").bufferedReader().use { it.readText() }
        Log.d("ConfigService", "JSON Loaded: $jsonString")
    } catch (ioException: IOException) {
        ioException.printStackTrace()
        Log.e("ConfigService", "Error loading JSON: ${ioException.message}")
        return JSONObject()
    }
    return JSONObject(jsonString)
}

fun getAppVersion(context: Context): String {
    val config = getAppConfig(context)
    val version = config.optString("version", "Unknown")
    Log.d("ConfigService", "App Version: $version")
    return version
}

suspend fun fetchConfigs(context: Context): Pair<JSONObject, JSONObject> {
    return withContext(Dispatchers.IO) {
        val localConfig = getAppConfig(context)
        val serverConfig = getServerConfig()
        Pair(localConfig, serverConfig)
    }
}

fun getServerConfig(): JSONObject {
    val serverConfigJson = """
        {
            "appName": "TextReader",
            "version": "1.0.1",
            "developer": "Pavel Lapa",
            "description": "An app to read text files.",
            "updateUrl": "https://127.0.0.1:5000/file"
        }
    """.trimIndent()
    return JSONObject(serverConfigJson)
}
