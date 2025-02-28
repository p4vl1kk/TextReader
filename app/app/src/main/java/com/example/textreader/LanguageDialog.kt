package com.example.textreader

import android.app.Activity
import android.content.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.textreader.R
import java.util.*

@Composable
fun LanguageDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.language),
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
                Button(
                    onClick = {
                        changeLanguage(context, "ru")
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Русский")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        changeLanguage(context, "en")
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("English")
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}

fun changeLanguage(context: Context, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)

    val resources = context.resources
    val config = resources.configuration
    config.setLocale(locale)
    config.setLayoutDirection(locale)

    resources.updateConfiguration(config, resources.displayMetrics)

    val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
    sharedPreferences.edit().putString("AppLanguage", languageCode).apply()

    if (context is Activity) {
        context.recreate()
    }
}
