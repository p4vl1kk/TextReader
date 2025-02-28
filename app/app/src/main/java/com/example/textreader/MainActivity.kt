package com.example.textreader

import android.content.*
import android.net.Uri
import android.os.Bundle
import androidx.activity.*
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.core.view.WindowCompat
import java.util.*

class MainActivity : ComponentActivity() {
    private val pickTxtFile = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            val intent = Intent(this, FileDisplayActivity::class.java).apply {
                putExtra("fileUri", it.toString())
                putExtra("fileContent", readTextFromUri(it))
            }
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val languageCode = sharedPreferences.getString("AppLanguage", Locale.getDefault().language)
        setAppLocale(languageCode ?: "en")

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                var showDialog by remember { mutableStateOf(false) }

                if (showDialog) {
                    SettingsDialog(onDismiss = { showDialog = false })
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.systemBars)
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.app_title),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 8.dp)
                    )
                    Divider(
                        color = Color.Gray,
                        thickness = 2.dp,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MainScreenContent(
                        onOpenFileClick = {
                            pickTxtFile.launch(arrayOf("text/plain"))
                        },
                        onSettingsClick = {
                            showDialog = true
                        },
                        onExitClick = {
                            finish()
                        },
                        version = getAppVersion(this@MainActivity)
                    )
                }
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

    private fun readTextFromUri(uri: Uri): String {
        return contentResolver.openInputStream(uri)?.bufferedReader().use { it?.readText() } ?: "Error reading file"
    }
}

@Composable
fun MainScreenContent(
    onOpenFileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onExitClick: () -> Unit,
    version: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onOpenFileClick,
                modifier = Modifier
                    .width(220.dp)
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF80CBC4))
            ) {
                Text(text = stringResource(R.string.open_file), fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onSettingsClick,
                modifier = Modifier
                    .width(220.dp)
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF80CBC4))
            ) {
                Text(text = stringResource(R.string.settings), fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onExitClick,
                modifier = Modifier
                    .width(220.dp)
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(text = stringResource(R.string.exit), fontSize = 18.sp)
            }
        }

        Text(
            text = "Version: $version",
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}
