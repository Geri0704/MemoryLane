package com.example.memorylane

import android.content.Intent
import android.os.Bundle
import android.widget.CalendarView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.memorylane.ui.theme.MemorylaneTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MemorylaneTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Base()
                }
            }
        }
    }
}

@Composable
fun Base(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        AndroidView(
            { CalendarView(it) },
            modifier = Modifier.wrapContentWidth(),
//            update = { views ->
//                views.date = scheduleViewModel.selectedCalender.value.timeInMillis
//                views.setOnDateChangeListener { calendarView, year, month, dayOfMonth ->
//                    val cal = Calendar.getInstance()
//                    cal.set(year, month, dayOfMonth)
//                    scheduleViewModel.onEvent(ScheduleEvent.DateSelected(cal))
//                    onDateSelect()
//
//                }
//            }
        )

        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Base page")

            Spacer(modifier = modifier.height(16.dp))

            Button(
                onClick = {
                    val intent = Intent(context, JournalActivity::class.java)
                    context.startActivity(intent)
                }
            ) {
                Text(text = "Next")
            }
        }
    }


}

@Preview(showBackground = true)
@Composable
fun PagePreview() {
    MemorylaneTheme {
        Base()
    }
}
