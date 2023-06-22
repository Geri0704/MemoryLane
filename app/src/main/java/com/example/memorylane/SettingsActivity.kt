package com.example.memorylane

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.memorylane.ui.theme.MemorylaneTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MemorylaneTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SettingsPage()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column() {
            Text(text = "Settings")
            Text(text = "Account")
            Text(text = "Notifications")
            Text(text = "Logout")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPagePreview() {
    MemorylaneTheme {
        SettingsPage()
    }
}
