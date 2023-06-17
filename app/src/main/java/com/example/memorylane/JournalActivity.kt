package com.example.memorylane

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.TextField
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.memorylane.ui.theme.MemorylaneTheme

class JournalActivity : ComponentActivity() {
//    TODO
//    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MemorylaneTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    JournalPage()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalPage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // entry and happiness rating store
        var journalEntry by remember { mutableStateOf("") }
        var happiness by remember { mutableStateOf(5f) }

        TextField(
            value = journalEntry,
            onValueChange = { journalEntry = it },
            label = { Text("Enter today's entry") },
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.5f)
        )

        Spacer(modifier = modifier.height(16.dp))

        Text(text ="How was today?")

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("😞")
            Slider(
                value = happiness,
                onValueChange = { happiness = it },
                valueRange = 0f..10f,
                modifier = Modifier.weight(1f)
            )
            Text("😀")
        }

        Button(
            onClick = {
                if (journalEntry.isNotEmpty()) {
                    // TODO: save entry
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Submit")
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Filled.Check, contentDescription = "Submit")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun JournalPagePreview() {
    MemorylaneTheme {
        JournalPage()
    }
}
