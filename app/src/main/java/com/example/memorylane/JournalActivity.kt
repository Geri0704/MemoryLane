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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.memorylane.client.AIClient
import com.example.memorylane.data.JournalEntryDO
import com.example.memorylane.ui.theme.MemorylaneTheme
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface GptResponseListener {
    fun onGptResponse(response: String)
    fun onGptFailure(e: Exception)
}

class JournalActivity : ComponentActivity(), GptResponseListener {
    // AI
    private lateinit var gptRequest: AIClient
    lateinit var textFieldLabel: MutableState<String>

    // DB
    lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gptRequest = AIClient(this);
        gptRequest.makeGptRequest("Give me a short innovative journal entry prompt about today (No quotes around prompt)")
        textFieldLabel = mutableStateOf("Loading prompt...")

        val config = RealmConfiguration.create(schema = setOf(JournalEntryDO::class))
        realm = Realm.open(config)

        setContent {
            MemorylaneTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    JournalPage()
                }
            }
        }
    }

    override fun onGptResponse(response: String) {
        runOnUiThread {
            textFieldLabel.value = response
        }
    }

    override fun onGptFailure(e: Exception) {
        runOnUiThread {
            textFieldLabel.value = "Failed to get prompt..."
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
        val journalActivity = LocalContext.current as JournalActivity
        val textFieldLabel = journalActivity.textFieldLabel.value
        val realm = journalActivity.realm

        Box(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = journalEntry,
                onValueChange = { journalEntry = it },
                label = { Text(textFieldLabel) },
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.5f)
            )

            Text(
                text = SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date()),
                color = Color.LightGray,
                modifier = Modifier.align(Alignment.BottomEnd).padding(6.dp)
            )
        }

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
                    realm.writeBlocking {
                        copyToRealm(JournalEntryDO().apply {
                            date = SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date())
                            prompt = textFieldLabel
                            entry = journalEntry
                            happiness = happiness
                        })
                    }

                    journalEntry = ""
                    happiness = 5f
                }

                // val items: RealmResults<JournalEntryDO> = realm.query<JournalEntryDO>().find()
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
