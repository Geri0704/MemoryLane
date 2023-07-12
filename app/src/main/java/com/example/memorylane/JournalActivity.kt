package com.example.memorylane

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.memorylane.client.AIClient
import com.example.memorylane.client.BackendClient
import com.example.memorylane.data.JournalEntryDO
import com.example.memorylane.ui.theme.MemorylaneTheme
import com.google.accompanist.flowlayout.FlowRow
import com.google.gson.Gson
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface GptResponseListener {
    fun onGptResponse(response: String)
    fun onGptThemeResponse(response: List<String>)
    fun onGptFailure(e: Exception)
}

class JournalActivity : ComponentActivity(), GptResponseListener, LocationListener {
    // AI
    lateinit var gptRequest: AIClient
    lateinit var textFieldLabel: MutableState<String>
    lateinit var journalThemes: MutableState<List<String>>

    // DB
    lateinit var realm: Realm

    //location
    lateinit var locationManager: LocationManager
    lateinit var locationText: MutableState<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userPreferences = UserPreferences(applicationContext)
        gptRequest = AIClient(this)
        textFieldLabel = mutableStateOf("")
        if (userPreferences.aiPrompts) {
            gptRequest.makeGptRequest(getString(R.string.ai_request_1))
            textFieldLabel = mutableStateOf("Loading prompt...")
        }
        journalThemes = mutableStateOf(listOf())

        val config = RealmConfiguration.create(schema = setOf(JournalEntryDO::class))
        realm = Realm.open(config)

        locationText = mutableStateOf("")
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request runtime permissions if not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                2
            )
        }
        // Start listening for location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 50000, 100f, this)

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

    override fun onGptThemeResponse(response: List<String>) {
        runOnUiThread {
            journalThemes.value = response
        }
    }

    override fun onGptFailure(e: Exception) {
        runOnUiThread {
            textFieldLabel.value = "Failed to get prompt..."
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onLocationChanged(location: Location) {
        val geocoder = Geocoder(baseContext, Locale.getDefault())
        geocoder.getFromLocation(location.latitude, location.longitude,1, @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        object : Geocoder.GeocodeListener{
            override fun onGeocode(addresses: MutableList<Address>) {
                //on success
                if (addresses != null && addresses.isNotEmpty()) {
                    locationText.value = addresses[0].locality
                }
            }
            override fun onError(errorMessage: String?) {
                super.onError(errorMessage)
                locationText.value = "Failed to get city location..."
            }

        })
    }
}

data class SaveJournalResponse(val message: String = "")

@Composable
fun JournalPage(modifier: Modifier = Modifier) {
    val client = BackendClient()

    var msg by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    fun saveToDB(journalToSave: JournalEntryDO) {
        client.saveJournal(MOCK_TOKEN, journalToSave) { saveJournalResponse, exception ->
            scope.launch {
                if (exception != null) {
                    msg = exception.toString()
                }

                val gson = Gson()
                val saveJournalResponseObject =
                    gson.fromJson(saveJournalResponse?.body?.string(), SaveJournalResponse::class.java)

                if (saveJournalResponse?.isSuccessful == true) {
                    msg = "Successfully saved journal to cloud"
                }
                else {
                    msg = saveJournalResponseObject.message
                }
            }
        }
    }




    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val context = LocalContext.current
        val userPreferences = UserPreferences(context)
        // entry and happiness rating store
        var journalEntry by remember { mutableStateOf("") }
        var happiness by remember { mutableStateOf(5f) }
        val journalActivity = LocalContext.current as JournalActivity
        val textFieldLabel = journalActivity.textFieldLabel.value
        val realm = journalActivity.realm
        val journalThemes = journalActivity.journalThemes.value
        val gptRequest = journalActivity.gptRequest

        Box(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = journalEntry,
                onValueChange = { journalEntry = it },
                label = { Text(textFieldLabel) },
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
            )

            Text(
                text = SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date()),
                color = Color.LightGray,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp)
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

        Text(text ="City: " + journalActivity.locationText.value)

        Button(
            onClick = {
                if (journalEntry.isNotEmpty()) {
                    realm.writeBlocking {
                        copyToRealm(JournalEntryDO().apply {
                            date = SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date())
                            prompt = textFieldLabel
                            entry = journalEntry
                            happinessRating = happiness
                        })
                    }

                    val journalToSave = JournalEntryDO().apply {
                        date = SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date())
                        prompt = textFieldLabel
                        entry = journalEntry
                        happinessRating = happiness
                    }

                    saveToDB(journalToSave)
                }

                if (userPreferences.aiPrompts) {
                    val request = journalActivity.getString(R.string.ai_request_2)
                    gptRequest.makeGptRequest("$request $journalEntry", 1)
                }

                // val items: RealmResults<JournalEntryDO> = realm.query<JournalEntryDO>().find()
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Submit")
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Filled.Check, contentDescription = "Submit")
        }

        JournalThemes(journalThemes)
    }
}

@Composable
fun JournalThemes(journalThemes : List<String>) {
    if (journalThemes.isNotEmpty()) {
        Text(text ="Recognized journal themes:", modifier = Modifier.padding(top = 16.dp))
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FlowRow(
            modifier = Modifier.padding(top = 4.dp),
            mainAxisSpacing = 8.dp,
            crossAxisSpacing = 8.dp
        ) {
            journalThemes.forEach { theme ->
                ThemeTag(theme = theme)
            }
        }
    }
}

@Composable
fun ThemeTag(theme: String) {
    val customGreen = Color(0xbfe3b4)

    Card(
        modifier = Modifier.padding(end = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Surface(color = customGreen) {
            Text(
                text = theme,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                color = Color.LightGray
            )
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
