package com.example.memorylane

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.memorylane.ui.theme.MemorylaneTheme
import com.example.memorylane.ui.theme.Pink40
import com.himanshoe.kalendar.Kalendar
import com.himanshoe.kalendar.KalendarEvent
import com.himanshoe.kalendar.KalendarEvents
import com.himanshoe.kalendar.KalendarType
import com.himanshoe.kalendar.color.KalendarColor
import com.himanshoe.kalendar.color.KalendarColors
import com.himanshoe.kalendar.ui.firey.DaySelectionMode
import kotlinx.datetime.LocalDate
import java.util.Calendar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.rememberNavController
import com.example.memorylane.client.BackendClient
import com.example.memorylane.ui.components.CustomCard
import com.google.gson.Gson
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.memorylane.analytics.AnalyticsWorker
import com.example.memorylane.data.JournalEntryDO
import com.example.memorylane.data.JournalEntryResponseDO
import com.example.memorylane.workers.BackendWorker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MemorylaneTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RootNavigationGraph(navController = rememberNavController())
                }
            }
        }

        setupGptRequestWork()
    }

    private fun setupGptRequestWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val repeatInterval = 15L
        val repeatIntervalTimeUnit = TimeUnit.MINUTES

        val dbUpdateWorkRequest = PeriodicWorkRequestBuilder<BackendWorker>(repeatInterval, repeatIntervalTimeUnit)
            .setConstraints(constraints)
            .build()

        val gptRequestWorkRequest = PeriodicWorkRequestBuilder<AnalyticsWorker>(repeatInterval, repeatIntervalTimeUnit)
//        val gptRequestWorkRequest = OneTimeWorkRequestBuilder<AnalyticsWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "gptRequestWork",
            ExistingPeriodicWorkPolicy.KEEP,
            gptRequestWorkRequest
        )

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "dbUpdateWork",
            ExistingPeriodicWorkPolicy.KEEP,
            dbUpdateWorkRequest
        )
        WorkManager.getInstance(this).enqueue(gptRequestWorkRequest)
    }
}

data class JournalResponse(
    val message: String = "",
    val journals: ArrayList<JournalEntryResponseDO> = ArrayList()
)

val MONTH_MAPPING: HashMap<String, String> = hashMapOf(
    "01" to "JAN",
    "02" to "FEB",
    "03" to "MAR",
    "04" to "APR",
    "05" to "MAY",
    "06" to "JUN",
    "07" to "JUL",
    "08" to "AUG",
    "09" to "SEP",
    "10" to "OCT",
    "11" to "NOV",
    "12" to "DEC",
)

val MOCK_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6InRlc3RAdGVzdC5jb20iLCJpYXQiOjE2ODg1MDcxMzV9.zEZvb-V7LSlNwdFeOxlLZfz90FQnhOLMyenee7LlKcE"

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Base(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val CURRENT_DATE = LocalDate(Calendar.getInstance().get(Calendar.YEAR),
        Calendar.getInstance().get(Calendar.MONTH)+1,
        Calendar.getInstance().get(Calendar.DATE))
    var dateSelected by remember { mutableStateOf(CURRENT_DATE.toString())}
    var journalEntries by remember { mutableStateOf(ArrayList<JournalEntryResponseDO>()) }

    // API calls
    val client = BackendClient()

    val response = remember {
        mutableStateOf("")
    }
    var msg by remember { mutableStateOf("") }

    LaunchedEffect(response) {
        client.getJournals(MOCK_TOKEN) { journalResponse, exception ->
            if (exception != null) {
                msg = exception.toString()
            }

            val gson = Gson()
            val journalResponseObject =
                gson.fromJson(journalResponse?.body?.string(), JournalResponse::class.java)

            if (journalResponse?.isSuccessful == true) {
                msg = "Successfully saved journal to cloud"
                journalEntries = journalResponseObject.journals
            }
            else {
                msg = journalResponseObject.message
            }
        }
    }

    var events: List<KalendarEvent> = ArrayList()
//    // get events from db
    if (journalEntries.size != 0) {
        for (entry in journalEntries) {
            val (year, month, day) = entry.date.split('-')
//          TODO: change back when time
//            val (year, month, day) = "2022-07-10".split('-')
            val date = LocalDate(year.toInt(), month.toInt(), day.toInt())

            val event = KalendarEvent(date, "test", "test")
            events += event
        }
    }


    val kalendarColors: MutableList<KalendarColor> = ArrayList<KalendarColor>().toMutableList()
    for (i in 1 .. 12) {
        kalendarColors += KalendarColor(Color(255,255,255), Pink40, Pink40)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(10.dp, 10.dp, 10.dp, 100.dp),
    ) {
        CustomCard {
            Box (
                modifier = modifier
                    .height(400.dp)
                    .background(Color(255, 255, 255))
            ) {
                Kalendar(
                    //need to set size for scrollable
                    currentDay = null,
                    kalendarType = KalendarType.Firey,
                    showLabel = true,
                    events = KalendarEvents(events),
                    kalendarColors = KalendarColors(kalendarColors),
                    daySelectionMode = DaySelectionMode.Single,
                    onDayClick = { selectedDay, events ->
                        dateSelected = selectedDay.toString()
                    },
                )
            }
        }

        Spacer(modifier = modifier.height(32.dp))

        val (year, month, day) = dateSelected.split('-')
        Text(
            text = MONTH_MAPPING[month] + " " + day + ", " + year,
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = modifier.height(16.dp))

        CustomCard (
            modifier = modifier.fillMaxSize()
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color(255, 255, 255))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (dateSelected.compareTo(CURRENT_DATE.toString()) > 0) {
                    Text(
                        text = "The future is a blank canvas! Wait to see it!",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = modifier.height(16.dp))
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock Icon")
                } else if (dateSelected.compareTo(CURRENT_DATE.toString()) == 0) {
                    Text(
                        text = "Looks like you haven't written your journal yet!",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = modifier.height(16.dp))
                    Button(
                        onClick = {
                            val intent = Intent(context, JournalActivity::class.java)
                            context.startActivity(intent)
                        }
                    ) {
                        Text(text = "Next")
                    }
                } else {
                    var entryFound: JournalEntryResponseDO? = null
                    for (entry in journalEntries) {
                        if (entry.date == dateSelected) {
                            entryFound = entry
                        }
                    }

                    if (entryFound != null) {
                        Text(
                            text = entryFound.prompt,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = modifier.height(16.dp))
                        Text(
                            text = entryFound.entry,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = modifier.height(16.dp))
                        Text(text = "Happiness: " + entryFound.happinessRating + " " + if (entryFound.happinessRating < 5f) "😞" else "😀")
                        Spacer(modifier = modifier.height(16.dp))
//                        Button(
//                            onClick = {
//                                TODO()
//                            }
//                        ) {
//                            Text(text = "Open")
//                        }
                    } else {
                        if (dateSelected.compareTo(CURRENT_DATE.toString()) == 0) {
                            Text(
                                text = "Looks like you haven't written your journal yet!",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = modifier.height(16.dp))
                            Button(
                                onClick = {
                                    val intent = Intent(context, JournalActivity::class.java)
                                    context.startActivity(intent)
                                }
                            ) {
                                Text(text = "Next")
                            }
                        } else {
                            Text(
                                text = "You didn't enter a journal entry on this date",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                    }
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun PagePreview() {
    MemorylaneTheme {
        Base()
    }
}
