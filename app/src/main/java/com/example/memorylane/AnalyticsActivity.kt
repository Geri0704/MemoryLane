package com.example.memorylane

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entriesOf

val chartEntryModelProducer1 = ChartEntryModelProducer(getWeekWordCount())
val chartEntryModelProducer2 = ChartEntryModelProducer(getWeekHappiness())

@Composable
fun AnalyticsPage() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Week Word Count", textAlign = TextAlign.Center, color = MaterialTheme.colors.secondary, modifier = Modifier.padding(top = 16.dp))
        Chart(
            chart = lineChart(),
            chartModelProducer = chartEntryModelProducer1,
            startAxis = startAxis(),
            bottomAxis = bottomAxis(),
        )
        Text(text = "Week Happiness", textAlign = TextAlign.Center, color = MaterialTheme.colors.secondary, modifier = Modifier.padding(top = 16.dp))
        Chart(
            chart = columnChart(),
            chartModelProducer = chartEntryModelProducer2,
            startAxis = startAxis(),
            bottomAxis = bottomAxis(),
        )
    }
}

fun getWeekWordCount() = List(16) { entriesOf(500, 300, 450, 100, 900, 450, 333, 633, 767, 820, 932, 934, 855, 744, 203, 555) }

fun getWeekHappiness() = List(1) { entriesOf(5f, 6.5f, 7f, 5f, 3f, 4f, 5f, 6f, 7f, 8f, 9.3f, 9.4f, 8.4f, 7f, 8.5f, 7f) }
