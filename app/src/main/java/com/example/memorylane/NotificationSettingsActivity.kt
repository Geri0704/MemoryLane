package com.example.memorylane

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import com.example.memorylane.ui.theme.MemorylaneTheme
import java.lang.reflect.Array.set
import java.util.Calendar

class NotificationSettingsActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userPreferences = UserPreferences(applicationContext)

        setContent {
            MemorylaneTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NotificationSettingsPage(LocalContext.current, userPreferences)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationSettingsPage(context: Context, userPreferences: UserPreferences) {
    var time by remember { mutableStateOf(LocalTime.parse(userPreferences.notificationTime)) } // change type to LocalTime
    val weekDays = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    val selectedDays = userPreferences.notificationDays
    val daysOfWeek by remember {
        mutableStateOf(weekDays.map { CheckboxState(it, selectedDays.contains(it)) })
    }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Select Days")
        daysOfWeek.forEach { checkboxState ->
            CheckboxItem(
                text = checkboxState.name,
                checked = checkboxState.checked
            ) { isChecked ->
                checkboxState.checked = isChecked
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                showTimePicker(context, time) { newTime ->
                    time = newTime
                }
            }
        ) {
            Text("Select Time: ${time.format(DateTimeFormatter.ofPattern("h:mm a"))}")
        }

        Button(
            onClick = {
                userPreferences.notificationTime = time.format(DateTimeFormatter.ofPattern("HH:mm"))
                userPreferences.notificationDays = daysOfWeek.filter { it.checked }.map { it.name }.toSet()
                scheduleNotifications(context, userPreferences)
            }
        ) {
            Text("Save Settings")
        }

    }
}

class CheckboxState(val name: String, isChecked: Boolean) {
    var checked by mutableStateOf(isChecked)
}

@Composable
fun CheckboxItem(text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = { onCheckedChange(!checked) }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )

        Text(
            text = text,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun showTimePicker(context: Context, currentTime: LocalTime, onTimeSelected: (LocalTime) -> Unit) {
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            onTimeSelected(LocalTime.of(hourOfDay, minute))
        },
        currentTime.hour,
        currentTime.minute,
        false // 12 hour format
    )
    timePickerDialog.show()
}
@RequiresApi(Build.VERSION_CODES.O)
fun scheduleNotifications(context: Context, userPreferences: UserPreferences) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, ReminderBroadcast::class.java)

    // Define weekDays inside the function
    val weekDays = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

    // Cancel existing alarms
    for (i in 0 until 7) {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            i,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }

    // Create new alarms
    for ((i, dayOfWeek) in weekDays.withIndex()) {
        if (userPreferences.notificationDays.contains(dayOfWeek)) {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_WEEK, i + 1) // Calendar.DAY_OF_WEEK is 1-based
                set(Calendar.HOUR_OF_DAY, LocalTime.parse(userPreferences.notificationTime).hour)
                set(Calendar.MINUTE, LocalTime.parse(userPreferences.notificationTime).minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)

                // If the alarm time is in the past, set the alarm for next week
                if (before(Calendar.getInstance())) {
                    add(Calendar.WEEK_OF_YEAR, 1)
                }
            }

            val pendingIntent = PendingIntent.getBroadcast(context, i, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    }
     Log.d("NotificationSettingsActivity", "Alarms scheduled for ${userPreferences.notificationDays} at ${userPreferences.notificationTime}")

}

