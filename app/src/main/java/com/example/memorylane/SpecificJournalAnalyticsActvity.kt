package com.example.memorylane

import androidx.compose.ui.text.font.FontStyle
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withAnnotation
import androidx.compose.ui.text.withStyle
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

    val entries = getAllEntries()
    var selectedEntry by remember { mutableStateOf(entries.firstOrNull()) }

    MaterialTheme(
        colors = if (MaterialTheme.colors.isLight) lightColors() else darkColors(),
    ) {
        Column {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
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
                selectedEntry?.let { DropdownMenu(entries = entries, selectedEntry = it, onSelectedEntryChanged = { selectedEntry = it }) }
                SpecificAnalyticsPage(selectedEntry?.entry ?: "No entries yet...", selectedEntry)
            }
        }
    }
}

@Composable
fun SpecificAnalyticsPage(entry: String, selectedEntry: JournalEntryDO?) {
    var positives by remember { mutableStateOf(getPositives(selectedEntry)) }
    var negatives by remember { mutableStateOf(getNegatives(selectedEntry)) }
    var workOns by remember { mutableStateOf(getWorkOns(selectedEntry)) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 64.dp)
            .fillMaxWidth()
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

        Button(onClick = {
            clearAnalyticsForEntry(selectedEntry)
            positives = getPositives(selectedEntry)
            negatives = getNegatives(selectedEntry)
            workOns = getWorkOns(selectedEntry)
        },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF90EE90))
        ) {
            Text("Reanalyze?")
        }

        ListDisplay(title = "Positives", items = positives)
        Spacer(modifier = Modifier.height(16.dp))

        ListDisplay(title = "Negatives", items = negatives)
        Spacer(modifier = Modifier.height(16.dp))

        ListDisplay(title = "Things to work on", items = workOns)

        EmergencyContact()
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun EmergencyContact() {
    var emergencyContactVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = { emergencyContactVisible = !emergencyContactVisible },
            modifier = Modifier.padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Transparent,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(50)
        ) {
            Text("Emergency Contact")
        }
    }

    if (emergencyContactVisible) {
        Text(
            text = "Help Contact Information",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
        )
        Text(
            text = "Phone: 1-888-668-6810",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
        )
        val uriHandler = LocalUriHandler.current
        val annotatedLinkString = buildAnnotatedString {
            withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                withAnnotation(tag = "", annotation = "https://www.canada.ca/en/public-health/services/mental-health-services/mental-health-get-help.html") {
                    append("https://www.canada.ca/en/public-health/services/mental-health-services/mental-health-get-help.html")
                }
            }
        }
        ClickableText(
            text = annotatedLinkString,
            style = TextStyle(textAlign = TextAlign.Center),
            onClick = { offset ->
                annotatedLinkString.getStringAnnotations("URL", offset, offset).firstOrNull()?.let { annotation ->
                    uriHandler.openUri(annotation.item)
                }
            },
        )
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

    val entriesGroupedByDate = entries.groupBy { it.date }

    Box(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = selectedEntry.date.toString(),
            onValueChange = {},
            label = { Text(text = "Selected Journal Entry") },
            readOnly = true,
            trailingIcon = {
                IconToggleButton(checked = expanded, onCheckedChange = { expanded = it }) {
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = "Dropdown icon")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
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
                        text = "${entry.date} (${order})",
                        color = if (entry.id == selectedEntry.id) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface
                    )
                }
            }
        }
    }
}

fun getAllEntries(): List<JournalEntryDO> {
    val config = RealmConfiguration.create(schema = setOf(JournalEntryDO::class))
    val realm = Realm.open(config)
    val items: RealmResults<JournalEntryDO> = realm.query<JournalEntryDO>().sort("date", Sort.DESCENDING).find()
    return items.toList()
}

fun getEntryById(id: String?): JournalEntryDO? {
    val config = RealmConfiguration.create(schema = setOf(JournalEntryDO::class))
    val realm = Realm.open(config)

    return realm.query<JournalEntryDO>("id=='$id'").find().firstOrNull()
}

fun getPositives(entry: JournalEntryDO?): List<String> {
    val latestEntry = getEntryById(entry?.id)
    return latestEntry?.positives?.toList() ?: emptyList()
}

fun getNegatives(entry: JournalEntryDO?): List<String> {
    val latestEntry = getEntryById(entry?.id)
    return latestEntry?.negatives?.toList() ?: emptyList()
}

fun getWorkOns(entry: JournalEntryDO?): List<String> {
    val latestEntry = getEntryById(entry?.id)
    return latestEntry?.workOn?.toList() ?: emptyList()
}
fun clearAnalyticsForEntry(entry: JournalEntryDO?) {
    if (entry == null) {
        return
    }

    val config = RealmConfiguration.create(schema = setOf(JournalEntryDO::class))
    val realm = Realm.open(config)

    realm.writeBlocking {
        findLatest(entry)?.apply {
            positives?.clear()
            negatives?.clear()
            workOn?.clear()
        }
    }
}