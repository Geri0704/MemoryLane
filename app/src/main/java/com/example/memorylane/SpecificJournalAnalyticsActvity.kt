package com.example.memorylane

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

@Composable
fun SpecificAnalyticsPage(entry: String, positives: List<String>, negatives: List<String>, workOn: List<String>) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Journal Entry",
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = entry,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        ListDisplay(title = "Positives", items = positives)
        Spacer(modifier = Modifier.height(16.dp))  // to add spacing between sections

        ListDisplay(title = "Negatives", items = negatives)
        Spacer(modifier = Modifier.height(16.dp))  // to add spacing between sections

        ListDisplay(title = "Things to work on", items = workOn)
    }
}

@Composable
fun ListDisplay(title: String, items: List<String>) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = title, style = MaterialTheme.typography.h6)
        items.forEach { item ->
            Text(
                text = item,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}