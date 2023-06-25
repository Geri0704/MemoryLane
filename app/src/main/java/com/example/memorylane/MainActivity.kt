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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MemorylaneTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    mainScreen()
                }
            }
        }
    }
}

data class MockEntry(
    val date: String,
    val prompt: String,
    val entry: String,
    val happiness: Float,
)

val MOCK_ENTRIES = arrayOf(
    MockEntry(
        "2023-06-22",
        "What was something you are grateful for today?",
        "Today, I am grateful for the support and encouragement of my classmates and friends. Their presence and collaboration make the learning experience more enjoyable and meaningful. Whether it's studying together, exchanging ideas, or providing emotional support, their friendship and camaraderie create a positive atmosphere that motivates me to strive for success. Knowing that I have a strong support system in my fellow students fills me with gratitude and reminds me that I am not alone on this educational journey.",
        7f
    ),
    MockEntry(
        "2023-06-23",
        "Describe a recent moment of self-discovery that has had a profound impact on your personal growth.",
        "A recent moment of self-discovery that had a profound impact on my personal growth was when I took the initiative to step out of my comfort zone and join a public speaking club. Initially, I was nervous and doubted my abilities, but through consistent practice and supportive feedback, I realized my potential to communicate effectively. This experience boosted my confidence, enhanced my communication skills, and taught me the value of embracing challenges. It showed me that growth happens outside of comfort zones, and by pushing myself, I can unlock hidden strengths and continue to evolve as an individual.",
        8f
    ),
    MockEntry(
        "2023-06-24",
        "What was something you are grateful for today?",
        "Today, I experienced a setback when I received a lower grade on a test than I had hoped for. It was disappointing and disheartening, but it taught me valuable lessons. Firstly, it reminded me of the importance of thorough preparation and studying consistently. Secondly, it highlighted the significance of seeking help and clarification when facing difficulties. Lastly, it reinforced the need to embrace failure as an opportunity for growth rather than letting it define me. This setback served as a reminder to stay resilient, learn from my mistakes, and persevere in my academic journey.",
        4f
    )
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

@Composable
fun ElevatedCard(modifier: Modifier = Modifier, content: @Composable() () -> Unit) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
    ) {
        content()
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Base(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val CURRENT_DATE = LocalDate(Calendar.getInstance().get(Calendar.YEAR),
        Calendar.getInstance().get(Calendar.MONTH)+1,
        Calendar.getInstance().get(Calendar.DATE))
    var dateSelected by remember { mutableStateOf(CURRENT_DATE.toString())}

//        TODO: fetch days with journal entries
    var events: List<KalendarEvent> = ArrayList()
    // get events from db
    for (entry in MOCK_ENTRIES) {
        val (year, month, day) = entry.date.split('-')
        val date = LocalDate(year.toInt(), month.toInt(), day.toInt())

        val event = KalendarEvent(date, "test", "dse")
        events += event
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
        ElevatedCard {
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

        ElevatedCard (
            modifier = modifier.fillMaxSize()
        ) {
            Column(
                modifier = modifier.fillMaxSize()
                    .background(Color(255,255,255))
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
                    var entryFound: MockEntry? = null
                    for (entry in MOCK_ENTRIES) {
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
                        Text(text = "Happiness: " + entryFound.happiness + " " + if (entryFound.happiness < 5f) "😞" else "😀")
                        Spacer(modifier = modifier.height(16.dp))
                        Button(
                            onClick = {
                                TODO()
                            }
                        ) {
                            Text(text = "Open")
                        }
                    } else {
                        Text(
                            text = "You didn't enter a journal entry on this date",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
//                TODO("TICKET FOR THIS")
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
