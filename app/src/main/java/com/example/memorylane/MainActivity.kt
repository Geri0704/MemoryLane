package com.example.memorylane

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.RelativeLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.clip

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





@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Base(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var dateSelected by remember { mutableStateOf("")}
    val CURRENT_DATE = LocalDate(Calendar.getInstance().get(Calendar.YEAR),
        Calendar.getInstance().get(Calendar.MONTH)+1,
        Calendar.getInstance().get(Calendar.DATE))

//        TODO: fetch days with journal entries
    var events: List<KalendarEvent> = ArrayList()
    // get events from db
    for (i in 1..5) {
        val date = LocalDate(
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH)+1,
            Calendar.getInstance().get(Calendar.DATE)-i*2,
        )

        val event = KalendarEvent(date, "test", "dse")
        events += event
    }

    var kalendarColors: List<KalendarColor> = ArrayList()
    for (i in 1 .. 12) {
        kalendarColors += KalendarColor(Color(255,255,255), Pink40, Pink40)
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Kalendar(
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

        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (dateSelected.compareTo(CURRENT_DATE.toString()) > 0) {
                Text(text = "The future is a blank canvas! Wait to see it!")

                Spacer(modifier = modifier.height(16.dp))

                Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock Icon")
            } else if (dateSelected.compareTo(CURRENT_DATE.toString()) == 0) {
                Text(text = "You haven't written your journal yet!")
                Button(
                    onClick = {
                        val intent = Intent(context, JournalActivity::class.java)
                        context.startActivity(intent)
                    }
                ) {
                    Text(text = "Next")
                }
            } else {
                Text(text = "Previous journal view page")
                TODO("TICKET FOR THIS")
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
