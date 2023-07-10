package com.example.memorylane

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.memorylane.data.JournalEntryDO
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entriesOf
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.query.Sort

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
        Text(text = "Frequently Recognized Problems", textAlign = TextAlign.Center, color = MaterialTheme.colors.secondary, modifier = Modifier.padding(top = 16.dp))
    }
}

fun getWeekWordCount(): List<FloatEntry> {
    val config = RealmConfiguration.create(schema = setOf(JournalEntryDO::class))
    val realm = Realm.open(config)
    val items: RealmResults<JournalEntryDO> = realm.query<JournalEntryDO>().sort("date", Sort.DESCENDING)
        .limit(7)
        .find()

    return items.mapIndexed { index, it ->
        FloatEntry(index.toFloat()+1, it.entry.length.toFloat())
    }
}

fun getWeekHappiness(): List<FloatEntry> = List(1) {
    val config = RealmConfiguration.create(schema = setOf(JournalEntryDO::class))
    val realm = Realm.open(config)
    val items: RealmResults<JournalEntryDO> = realm.query<JournalEntryDO>().sort("date", Sort.DESCENDING)
        .limit(7)
        .find()

    return items.map {
        println("Happiness: ${it.happinessRating}")
        println("Happiness: ${it.entry}")
        return items.mapIndexed { index, it ->
            FloatEntry(index.toFloat()+1, it.happinessRating)
        }
    }
}
