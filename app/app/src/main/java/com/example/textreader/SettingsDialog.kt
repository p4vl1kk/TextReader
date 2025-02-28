package com.example.textreader

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.textreader.R

@Composable
fun SettingsDialog(onDismiss: () -> Unit) {
    var showLanguageDialog by remember { mutableStateOf(false) }
    if (showLanguageDialog) {
        LanguageDialog(onDismiss = { showLanguageDialog = false })
    }

    var showUpdateDialog by remember { mutableStateOf(false) }
    if (showUpdateDialog) {
        UpdateDialog(onDismiss = { showUpdateDialog = false })
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.settings_title),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        showLanguageDialog = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF80CBC4))
                ) {
                    Text(text = stringResource(R.string.language), fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {

                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF80CBC4))
                ) {
                    Text(text = stringResource(R.string.update), fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000))
                ) {
                    Text(text = stringResource(R.string.cancel), fontSize = 18.sp)
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}
