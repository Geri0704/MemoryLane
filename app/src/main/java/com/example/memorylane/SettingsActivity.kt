package com.example.memorylane

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
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
    val context = LocalContext.current
    Column(
        modifier = modifier.fillMaxWidth(),
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

            Button(onClick = {}) {
                Icon(imageVector = Icons.Outlined.Notifications, contentDescription = "Notifications Icon")

                Text(text = "Notifications",Modifier.padding(start = 10.dp))
            }

            Button(onClick = {
                val intent = Intent(context, ReminderBroadcast::class.java)
                context.sendBroadcast(intent)
            }) {
                Text(text = "Trigger Notification")
            }

            Button(onClick = {}) {
                Icon(imageVector = Icons.Outlined.Close, contentDescription = "Close Icon")

                Text(text = "Logout",Modifier.padding(start = 10.dp))
            }
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
