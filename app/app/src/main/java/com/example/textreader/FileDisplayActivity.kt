package com.example.textreader

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.*
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.core.view.WindowCompat
import java.io.*
import java.util.Locale

@Suppress("DEPRECATION")
class FileDisplayActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val languageCode = sharedPreferences.getString("AppLanguage", Locale.getDefault().language)
        setAppLocale(languageCode ?: "en")

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val fileUriString = intent.getStringExtra("fileUri")
        val fileContentFromIntent = intent.getStringExtra("fileContent") ?: ""

        val fileUri: Uri? = when {
            fileUriString != null -> Uri.parse(fileUriString)
            intent.data != null -> intent.data
            else -> null
        }

        val fileContent = fileUri?.let { readTextFromUri(it) } ?: fileContentFromIntent

        setContent {
            var editableFileContent by remember { mutableStateOf(fileContent) }

            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                FileDisplayScreen(
                    fileContent = editableFileContent,
                    onFileContentChange = { newContent -> editableFileContent = newContent },
                    onSaveClick = {
                        fileUri?.let { saveFileContent(it, editableFileContent) }
                        finish()
                    },
                    onDiscardClick = { finish() }
                )
            }
        }
    }

    private fun setAppLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    @Composable
    private fun FileDisplayScreen(
        fileContent: String,
        onFileContentChange: (String) -> Unit,
        onSaveClick: () -> Unit,
        onDiscardClick: () -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.editor_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 8.dp)
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 32.dp),
                thickness = 2.dp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { onDiscardClick() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = stringResource(R.string.back),
                        tint = Color(0xFF000000)
                    )
                }
                Row {
                    IconButton(
                        onClick = { onDiscardClick() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.dont_save),
                            tint = Color(0xFFFF0000)
                        )
                    }
                    IconButton(
                        onClick = { onSaveClick() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = stringResource(R.string.save),
                            tint = Color(0xFF23FF00)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            BasicTextField(
                value = fileContent,
                onValueChange = onFileContentChange,
                textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.White)
                    .padding(8.dp)
            )
        }
    }

    private fun readTextFromUri(uri: Uri): String {
        return contentResolver.openInputStream(uri)?.bufferedReader().use { it?.readText() } ?: "Error reading file"
    }

    private fun saveFileContent(uri: Uri, content: String) {
        try {
            val outputStream = contentResolver.openOutputStream(uri)
            val writer = BufferedWriter(OutputStreamWriter(outputStream))
            writer.write(content)
            writer.flush()
            writer.close()
            outputStream?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
