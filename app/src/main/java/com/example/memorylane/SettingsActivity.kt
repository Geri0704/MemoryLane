package com.example.memorylane

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.memorylane.ui.theme.MemorylaneTheme

class SettingsActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun SettingsPage() {
    val context = LocalContext.current
    val userPreferences = UserPreferences(context)
    var aiPromptsText by remember { mutableStateOf((if (userPreferences.aiPrompts) "Disable" else "Enable") + " AI Generated Prompts") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Settings", Modifier.padding(vertical=40.dp))

            Button(onClick = {}) {
                Icon(imageVector = Icons.Outlined.Person, contentDescription = "Person Icon")

                Text(text = "Account",Modifier.padding(start = 10.dp))
            }

            Button(onClick = {
                val intent = Intent(context, NotificationSettingsActivity::class.java)
                context.startActivity(intent)
            }) {
                Icon(imageVector = Icons.Outlined.Notifications, contentDescription = "Notifications Icon")
                Text(text = "Notifications", Modifier.padding(start = 10.dp))
            }

            Button(onClick = {
                ReminderBroadcast().sendNotification(context, "Test Title", "Test Message")
            }) {
                Text(text = "Trigger Notification")
            }

            Button(onClick = {
                userPreferences.aiPrompts = !userPreferences.aiPrompts
                aiPromptsText = (if (userPreferences.aiPrompts) "Disable" else "Enable") + " AI Generated Prompts"
            }) {
                Text(text = aiPromptsText)
            }

            Button(onClick = {}) {
                Icon(imageVector = Icons.Outlined.Close, contentDescription = "Close Icon")

                Text(text = "Logout",Modifier.padding(start = 10.dp))
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview(showBackground = true)
@Composable
fun SettingsPagePreview() {
    MemorylaneTheme {
        SettingsPage()
    }
}

