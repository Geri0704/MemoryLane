package com.example.memorylane

import androidx.compose.ui.text.font.FontStyle
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.memorylane.data.JournalEntryDO
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.query.Sort

@Composable
fun AnalyticsPageParent() {
    var showWeeklyAnalytics by remember { mutableStateOf(false) }

//    val entry = getEntry()
//    val positives = getPositives()
//    val negatives = getNegatives()
//    val workOns = getWorkOns()
    val entries = getAllEntries()
    var selectedEntry by remember { mutableStateOf(entries.first()) }

    val positives = getPositives(selectedEntry)
    val negatives = getNegatives(selectedEntry)
    val workOns = getWorkOns(selectedEntry)

    MaterialTheme(
        colors = if (MaterialTheme.colors.isLight) lightColors() else darkColors(),
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Button(
                    onClick = { showWeeklyAnalytics = false },
                    colors = ButtonDefaults.buttonColors(backgroundColor = if (showWeeklyAnalytics) Color.Gray else MaterialTheme.colors.primary),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Specific Analytics")
                }
                Spacer(modifier = Modifier.width(16.dp)) // to add space between the buttons
                Button(
                    onClick = { showWeeklyAnalytics = true },
                    colors = ButtonDefaults.buttonColors(backgroundColor = if (showWeeklyAnalytics) MaterialTheme.colors.primary else Color.Gray),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Weekly Analytics")
                }
            }
            if (showWeeklyAnalytics) {
                AnalyticsPage()
            } else {
                DropdownMenu(entries = entries, selectedEntry = selectedEntry, onSelectedEntryChanged = { selectedEntry = it })
                SpecificAnalyticsPage(selectedEntry.entry, positives, negatives, workOns)
            }
        }
    }
}

@Composable
fun SpecificAnalyticsPage(entry: String, positives: List<String>, negatives: List<String>, workOn: List<String>) {
    Column(
        modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Journal Entry",
            style = MaterialTheme.typography.h5,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 8.dp)
        )

        Text(
            text = entry,
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onSurface,
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
        Text(
            text = title,
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.onSurface,
            textAlign = TextAlign.Center
        )
        if (items.isEmpty()) {
            Text(
                text = "Waiting for more analytics",
                style = MaterialTheme.typography.body1.copy(fontStyle = FontStyle.Italic),
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.padding(top = 8.dp)
            )
        } else {
            items.forEach { item ->
                Text(
                    text = "• $item",
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun DropdownMenu(entries: List<JournalEntryDO>, selectedEntry: JournalEntryDO, onSelectedEntryChanged: (JournalEntryDO) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    // Create a map that groups entries by their date
    val entriesGroupedByDate = entries.groupBy { it.date }

    Box(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = selectedEntry.date.toString(), // here you might want to format the date appropriately
            onValueChange = {},
            label = { Text(text = "Selected Journal Entry") },
            readOnly = true,
            trailingIcon = {
                IconToggleButton(checked = expanded, onCheckedChange = { expanded = it }) {
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = "Dropdown icon")
                }
            },
            modifier = Modifier.fillMaxWidth().clickable { expanded = true }
        )

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            entries.forEach { entry ->
                val sameDateEntries = entriesGroupedByDate[entry.date] ?: emptyList()
                val order = sameDateEntries.indexOf(entry) + 1
                DropdownMenuItem(onClick = {
                    onSelectedEntryChanged(entry)
                    expanded = false
                }) {
                    Text(
                        text = "${entry.date} (${order})", // here you might want to format the date appropriately
                        color = if (entry.id == selectedEntry.id) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface
                    )
                }
            }
        }
    }
}

//fun getEntry(): String {
//    val config = RealmConfiguration.create(schema = setOf(JournalEntryDO::class))
//    val realm = Realm.open(config)
//    val items: RealmResults<JournalEntryDO> = realm.query<JournalEntryDO>().sort("date", Sort.DESCENDING)
//        .limit(1)
//        .find()
//
//    val entry: JournalEntryDO? = items.firstOrNull()
//
//    if (entry != null) {
//        return entry.entry
//    }
//
//    return "Failed to get entry..."
//}
//
//fun getPositives(): List<String> {
//    val config = RealmConfiguration.create(schema = setOf(JournalEntryDO::class))
//    val realm = Realm.open(config)
//    val items: RealmResults<JournalEntryDO> = realm.query<JournalEntryDO>()
//        .sort("date", Sort.DESCENDING)
//        .limit(1)
//        .find()
//
//    val entry: JournalEntryDO? = items.firstOrNull()
//
//    return entry?.positives?.toList() ?: emptyList()
//}
//
//fun getNegatives(): List<String> {
//    val config = RealmConfiguration.create(schema = setOf(JournalEntryDO::class))
//    val realm = Realm.open(config)
//    val items: RealmResults<JournalEntryDO> = realm.query<JournalEntryDO>()
//        .sort("date", Sort.DESCENDING)
//        .limit(1)
//        .find()
//
//    val entry: JournalEntryDO? = items.firstOrNull()
//
//    return entry?.negatives?.toList() ?: emptyList()
//}
//
//fun getWorkOns(): List<String> {
//    val config = RealmConfiguration.create(schema = setOf(JournalEntryDO::class))
//    val realm = Realm.open(config)
//    val items: RealmResults<JournalEntryDO> = realm.query<JournalEntryDO>()
//        .sort("date", Sort.DESCENDING)
//        .limit(1)
//        .find()
//
//    val entry: JournalEntryDO? = items.firstOrNull()
//
//    return entry?.workOn?.toList() ?: emptyList()
//}

fun getAllEntries(): List<JournalEntryDO> {
    val config = RealmConfiguration.create(schema = setOf(JournalEntryDO::class))
    val realm = Realm.open(config)
    val items: RealmResults<JournalEntryDO> = realm.query<JournalEntryDO>().sort("date", Sort.DESCENDING).find()
    return items.toList()
}

fun getPositives(entry: JournalEntryDO): List<String> {
    return entry.positives?.toList() ?: emptyList()
}

fun getNegatives(entry: JournalEntryDO): List<String> {
    return entry.negatives?.toList() ?: emptyList()
}

fun getWorkOns(entry: JournalEntryDO): List<String> {
    return entry.workOn?.toList() ?: emptyList()
}