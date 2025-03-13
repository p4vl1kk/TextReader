package com.example.textreader

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.*
import java.net.*

private const val SERVER_JSON_URL = "http://127.0.0.1:5000/file/app_config.json"

@Composable
fun UpdateDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val (currentVersion, setCurrentVersion) = remember { mutableStateOf("Unknown") }
    val (latestVersion, setLatestVersion) = remember { mutableStateOf("Unknown") }

    LaunchedEffect(Unit) {
        val versions = fetchVersions(context)
        setCurrentVersion(versions.first)
        setLatestVersion(versions.second)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.update),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "${stringResource(R.string.current_version)}: $currentVersion")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "${stringResource(R.string.latest_version)}: $latestVersion")
                Spacer(modifier = Modifier.height(16.dp))
                if (currentVersion != latestVersion) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                updateApp(context)
                                onDismiss()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Update App")
                    }
                } else {
                    Text(text = stringResource(R.string.no_update_needed))
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )
}

suspend fun fetchJsonFromUrl(url: String): JSONObject? {
    return withContext(Dispatchers.IO) {
        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            val json = connection.inputStream.bufferedReader().use { it.readText() }
            JSONObject(json)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

suspend fun fetchVersions(context: Context): Pair<String, String> {
    return withContext(Dispatchers.IO) {
        val localVersion = getAppVersion(context)
        val serverConfig = fetchJsonFromUrl(SERVER_JSON_URL)
        val serverVersion = serverConfig?.optString("version", "Unknown") ?: "Unknown"
        Pair(localVersion, serverVersion)
    }
}

suspend fun updateApp(context: Context) {
    try {
        val serverConfig = fetchJsonFromUrl(SERVER_JSON_URL)
        val apkUrl = serverConfig?.optString("updateUrl")

        if (apkUrl.isNullOrEmpty()) {
            throw IllegalArgumentException("No valid APK URL provided in the server's app_config.json.")
        }

        val apkFile = downloadApk(context, apkUrl)
        installApk(context, apkFile)
    } catch (e: MalformedURLException) {
        // Неверный формат URL
        e.printStackTrace()
        println("Error: Malformed URL for APK download.")
    } catch (e: IOException) {
        // Ошибка сети или файлового ввода/вывода
        e.printStackTrace()
        println("Error: Network or file I/O issue while downloading APK.")
    } catch (e: SecurityException) {
        // Проблема с разрешением
        e.printStackTrace()
        println("Error: Permission issue, unable to access or install APK.")
    } catch (e: IllegalArgumentException) {
        // Неправильные или отсутствующие данные
        e.printStackTrace()
        println("Error: ${e.message}")
    } catch (e: Exception) {
        // Любая другая ошибка
        e.printStackTrace()
        println("Error: Unexpected issue occurred during the app update process.")
    }
}


suspend fun downloadApk(context: Context, apkUrl: String): File {
    return withContext(Dispatchers.IO) {
        val url = URL(apkUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        val apkFile = File(context.cacheDir, "new_app.apk")

        url.openStream().use { input ->
            apkFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        apkFile
    }
}

fun installApk(context: Context, apkFile: File) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(
            FileProvider.getUriForFile(context, "${context.packageName}.provider", apkFile),
            "application/vnd.android.package-archive"
        )
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(intent)
}