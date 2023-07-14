import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memorylane.Notification
import com.example.memorylane.ui.theme.MemorylaneTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.shape.RoundedCornerShape
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object NotificationRepository {
    private lateinit var preferences: SharedPreferences
    private val gson = Gson()

    fun initialize(context: Context) {
        preferences = context.getSharedPreferences("notification_repo", Context.MODE_PRIVATE)
        _notifications.value = loadNotifications()
    }

    private val _notifications: MutableStateFlow<List<Notification>> = MutableStateFlow(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications

    fun addNotification(notification: Notification) {
        val currentList = _notifications.value.toMutableList()
        currentList.add(0, notification)
        _notifications.value = currentList
        saveNotifications(currentList)
    }

    fun deleteNotification(notification: Notification) {
        val currentList = _notifications.value.toMutableList()
        currentList.remove(notification)
        _notifications.value = currentList
        saveNotifications(currentList)
    }

    fun clearNotifications() {
        _notifications.value = emptyList()
        saveNotifications(_notifications.value)
    }

    private fun saveNotifications(notifications: List<Notification>) {
        preferences.edit().putString("notifications", gson.toJson(notifications)).apply()
    }

    private fun loadNotifications(): List<Notification> {
        val json = preferences.getString("notifications", "")
        val type = object : TypeToken<List<Notification>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NotificationInboxPage(dummyNotifications: List<Notification> = emptyList()) {
    val notificationList by NotificationRepository.notifications.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var deleteNotification by remember { mutableStateOf<Notification?>(null) }
    var clearAll by remember { mutableStateOf(false) }

    if(showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirm Delete") },
            text = {
                Text(
                    "Are you sure you want to delete this notification?",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(onClick = {
                    deleteNotification?.let { it1 -> NotificationRepository.deleteNotification(it1) }
                    showDialog = false
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if(clearAll) {
        AlertDialog(
            onDismissRequest = { clearAll = false },
            title = { Text("Confirm Delete All") },
            text = {
                Text(
                    "Are you sure you want to delete all notifications?",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(onClick = {
                    NotificationRepository.clearNotifications()
                    clearAll = false
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(onClick = { clearAll = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 50.dp)
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Notification Inbox",
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    Button(onClick = { clearAll = true }, enabled = notificationList.isNotEmpty()) {
                        Text("Clear All")
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                ) {
                    items(notificationList) { notification ->
                        NotificationItem(notification) {
                            deleteNotification = notification
                            showDialog = true
                        }
                        Divider()
                    }
                }
            }
        }
    }
}


    @Composable
fun NotificationItem(notification: Notification, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(5)
    ) {
        Column {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = notification.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = notification.message,
                        fontSize = 14.sp
                    )
                }
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete Notification")
                    }
                }
            }
            Text(
                text = "Received: ${notification.receivedAt}",
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp, top = 2.dp, bottom = 2.dp)
            )
        }
    }
}



@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview(showBackground = true)
@Composable
fun NotificationInboxPagePreview() {
    MemorylaneTheme {
        val dummyNotifications = List(20) {
            Notification("Title $it", "Message $it", "12:00 PM")
        }
        NotificationInboxPage(dummyNotifications)
    }
}
