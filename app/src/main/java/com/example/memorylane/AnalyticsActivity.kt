package com.example.memorylane

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.memorylane.data.JournalEntryDO
import com.example.memorylane.data.WeeklyStorageDO
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.query.Sort

val chartEntryModelProducer1 = ChartEntryModelProducer(getWeekWordCount())
val chartEntryModelProducer2 = ChartEntryModelProducer(getWeekHappiness())

@Composable
fun AnalyticsPage() {
    val weeks = getWeeklyAllEntries()
    var selectedWeek by remember { mutableStateOf(weeks.firstOrNull()) }

    val positives = getWeeklyPositives(selectedWeek)
    val negatives = getWeeklyNegatives(selectedWeek)
    val workOns = getWeeklyWorkOns(selectedWeek)

    Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(bottom = 64.dp)) {
        Text(text = "Week Character Count", textAlign = TextAlign.Center, color = MaterialTheme.colors.secondary, modifier = Modifier.padding(top = 16.dp).fillMaxWidth())
        Chart(
            chart = lineChart(),
            chartModelProducer = chartEntryModelProducer1,
            startAxis = startAxis(),
            bottomAxis = bottomAxis(),
        )
        Text(text = "Week Happiness", textAlign = TextAlign.Center, color = MaterialTheme.colors.secondary, modifier = Modifier.padding(top = 16.dp).fillMaxWidth())
        Chart(
            chart = columnChart(),
            chartModelProducer = chartEntryModelProducer2,
            startAxis = startAxis(),
            bottomAxis = bottomAxis(),
        )
        Text(text = "Recognized Entry Commonalities", textAlign = TextAlign.Center, color = MaterialTheme.colors.secondary, modifier = Modifier.padding(top = 16.dp).fillMaxWidth())
        ListDisplay(title = "Overlap Positives", items = positives)
        ListDisplay(title = "Overlap Issues", items = negatives)
        ListDisplay(title = "Overlap Areas To Work On", items = workOns)

        EmergencyContact()
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

fun getWeeklyAllEntries(): List<WeeklyStorageDO> {
    val config = RealmConfiguration.create(schema = setOf(WeeklyStorageDO::class))
    val realm = Realm.open(config)
    val items: RealmResults<WeeklyStorageDO> = realm.query<WeeklyStorageDO>().sort("weekStartDate", Sort.DESCENDING).find()
    return items.toList()
}

fun getWeeklyPositives(entry: WeeklyStorageDO?): List<String> {
    return entry?.continuingPositives?.toList() ?: emptyList()
}

fun getWeeklyNegatives(entry: WeeklyStorageDO?): List<String> {
    return entry?.problems?.toList() ?: emptyList()
}

fun getWeeklyWorkOns(entry: WeeklyStorageDO?): List<String> {
    return entry?.thingsToWorkOn?.toList() ?: emptyList()
}