package com.example.textreader

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import org.json.JSONObject

@Composable
fun UpdateDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val (localConfig, setLocalConfig) = remember { mutableStateOf<JSONObject?>(null) }
    val (serverConfig, setServerConfig) = remember { mutableStateOf<JSONObject?>(null) }

    LaunchedEffect(Unit) {
        val configs = fetchConfigs(context)
        setLocalConfig(configs.first)
        setServerConfig(configs.second)
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
                if (localConfig != null && serverConfig != null) {
                    Text(text = "Current Version: ${localConfig.optString("version", "Unknown")}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Latest Version: ${serverConfig.optString("version", "Unknown")}")
                    Spacer(modifier = Modifier.height(16.dp))
                    if (localConfig.optString("version") != serverConfig.optString("version")) {
                        Button(
                            onClick = {
                                // Logic to update the app
                                onDismiss()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Update App")
                        }
                    }
                } else {
                    CircularProgressIndicator()
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}