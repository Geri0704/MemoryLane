package com.example.memorylane

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import com.example.memorylane.ui.theme.MemorylaneTheme
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
    var time by remember { mutableStateOf(LocalTime.parse(userPreferences.notificationTime)) }
    val weekDays = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    val selectedDays = userPreferences.notificationDays
    val daysOfWeek by remember {
        mutableStateOf(weekDays.map { CheckboxState(it, selectedDays.contains(it)) })
    }
    var isCustomSelected by remember { mutableStateOf(false) }
    var isWeekdaysOnlySelected by remember { mutableStateOf(false) }
    var isWeekendsOnlySelected by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Select Reminder Days")

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)) {
            Button(
                onClick = {
                    // Weekdays Only (Monday - Friday)
                    daysOfWeek.forEach { checkboxState ->
                        checkboxState.checked = checkboxState.name in listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")
                    }
                    isCustomSelected = false
                    isWeekdaysOnlySelected = true
                    isWeekendsOnlySelected = false
                },
                enabled = !isWeekdaysOnlySelected,
                modifier = Modifier.weight(1f)
            ) {
                Text("Weekdays Only")
            }

            Spacer(Modifier.width(8.dp))

            Button(
                onClick = {
                    // Weekends Only (Sunday, Saturday)
                    daysOfWeek.forEach { checkboxState ->
                        checkboxState.checked = checkboxState.name in listOf("Sunday", "Saturday")
                    }
                    isCustomSelected = false
                    isWeekdaysOnlySelected = false
                    isWeekendsOnlySelected = true
                },
                enabled = !isWeekendsOnlySelected,
                modifier = Modifier.weight(1f)
            ) {
                Text("Weekends Only")
            }
        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)) {

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 1.dp),
                onClick = {
                    // Custom
                    isCustomSelected = true
                    isWeekdaysOnlySelected = false
                    isWeekendsOnlySelected = false
                },
                enabled = !isCustomSelected
            ) {
                Text("Custom")
            }
        }

        if (isCustomSelected) {
            Spacer(Modifier.height(1.dp))
            daysOfWeek.forEach { checkboxState ->
                CheckboxItem(
                    text = checkboxState.name,
                    checked = checkboxState.checked
                ) { isChecked ->
                    checkboxState.checked = isChecked
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Select Reminder Time")

        Button(
            onClick = {
                showTimePicker(context, time) { newTime ->
                    time = newTime
                }
            }
        ) {
            Text("Time: ${time.format(DateTimeFormatter.ofPattern("h:mm a"))}")
        }

        Spacer(modifier = Modifier.weight(1f))
        val days = daysOfWeek.filter { it.checked }.map { it.name }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally // Center the children horizontally
        ) {
            if (days.toSet() == userPreferences.notificationDays && time.format(DateTimeFormatter.ofPattern("HH:mm")) == userPreferences.notificationTime) {
                Text(
                    text = "Reminders set to ${time.format(DateTimeFormatter.ofPattern("h:mm a"))} on ${days.joinToString()}.",
                    fontSize = 10.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                Text(
                    text = "Reminders will be sent at ${time.format(DateTimeFormatter.ofPattern("h:mm a"))} on ${
                        days.joinToString()
                    } once saved.",
                    fontSize = 10.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Button(
                onClick = {
                    userPreferences.notificationTime = time.format(DateTimeFormatter.ofPattern("HH:mm"))
                    userPreferences.notificationDays = daysOfWeek.filter { it.checked }.map { it.name }.toSet()

                    val title = "Reminder to Journal"
                    val message = "Click here to begin your journal!"

                    scheduleNotifications(context, userPreferences, title, message)
                    (context as Activity).finish()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .weight(1f)
            ) {
                Text("Save")
            }

            Spacer(Modifier.width(8.dp))

            Button(
                onClick = { (context as Activity).finish() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .weight(1f) // Assign equal weight to both buttons
            ) {
                Text("Cancel")
            }
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
fun scheduleNotifications(context: Context, userPreferences: UserPreferences, title: String, message: String) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, ReminderBroadcast::class.java).apply {
        putExtra("title", title)
        putExtra("message", message)
    }

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


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview(showBackground = true)
@Composable
fun NotificationSettingsActivityPreview() {
    var context = ContextThemeWrapper(LocalContext.current, R.style.Theme_Memorylane)
    MemorylaneTheme {
        NotificationSettingsPage(userPreferences = UserPreferences(context = context ), context= context)
    }
}

