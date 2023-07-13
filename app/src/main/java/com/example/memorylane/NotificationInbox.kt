import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memorylane.Notification
import com.example.memorylane.ui.theme.MemorylaneTheme

object NotificationRepository {
    private val notifications: MutableList<Notification> = mutableListOf()

    fun addNotification(notification: Notification) {
        notifications.add(notification)
    }

    fun getNotifications(): List<Notification> {
        return notifications.toList()
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NotificationInboxPage() {
    val notificationList by remember { mutableStateOf(NotificationRepository.getNotifications()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp)
            .navigationBarsPadding()
    ) {
        Text(
            text = "Notification Inbox",
            modifier = Modifier.padding(vertical = 40.dp),
            fontSize = 20.sp
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(notificationList) { notification ->
                NotificationItem(notification)
                Divider()
            }
        }
    }
}

@Composable
fun NotificationItem(notification: Notification) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = notification.title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = notification.message,
            fontSize = 14.sp
        )
    }
}



@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview(showBackground = true)
@Composable
fun NotificationInboxPagePreview() {
    MemorylaneTheme {
        NotificationInboxPage()
    }
}
