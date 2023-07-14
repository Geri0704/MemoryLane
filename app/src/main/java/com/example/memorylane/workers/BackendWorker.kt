package com.example.memorylane.workers

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.memorylane.BuildConfig.BASE_URL
import com.example.memorylane.JournalResponse
import com.example.memorylane.MOCK_TOKEN
import com.example.memorylane.client.BackendClient
import com.example.memorylane.client.HTTPClient
import com.example.memorylane.client.WorkerAIClient
import com.example.memorylane.client.WorkerHTTPClient
import com.example.memorylane.data.JournalEntryDO
import com.example.memorylane.data.JournalEntryResponseDO
import com.example.memorylane.data.WeeklyStorageDO
import com.google.gson.Gson
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.query.Sort
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.UUID

class JournalsRequest(
    val journals: ArrayList<JournalEntryResponseDO>
)
class BackendWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        try {
            var failed = false
            var err: Exception = Exception()
            try {
                sync_cloud_db()
            } catch (e: Exception) {
                failed = true
                err = e
            }
            try {
                sync_local_realm()
            } catch (e: Exception){
                failed = true
                err = e
            }
            if (failed) { throw err }
            return Result.success(workDataOf("Success" to "Local database updated"))
        } catch (e: Exception) {
            println(e)
            return Result.failure(workDataOf("Failed" to "Sync local database process failed"))
        }
    }

    private fun sync_local_realm() {
        val httpClient = WorkerHTTPClient()

        val journalResponse = httpClient.get("$BASE_URL/journal", MOCK_TOKEN)

        val gson = Gson()
        val journalResponseObject = gson.fromJson(journalResponse, JournalResponse::class.java)
        var journalEntries = journalResponseObject.journals

        val config = RealmConfiguration.create(schema = setOf(JournalEntryDO::class))
        val realm = Realm.open(config)

        for (journal in journalEntries) {
            val item = realm.query<JournalEntryDO>("date == '${journal.date}'").first().find()

            if (item != null) {
                realm.writeBlocking {
                    item.apply {
                        prompt = journal.prompt
                        entry = journal.entry
                        happinessRating = journal.happinessRating
                    }
                }
            } else {
                realm.writeBlocking {
                    copyToRealm(JournalEntryDO().apply {
                        date = journal.date
                        prompt = journal.prompt
                        entry = journal.entry
                        happinessRating = journal.happinessRating
                    })
                }
            }
        }

        realm.close()
    }

    private fun sync_cloud_db() {
        val config = RealmConfiguration.create(schema = setOf(JournalEntryDO::class))
        val realm = Realm.open(config)
        val items = realm.query<JournalEntryDO>().find()
        var journals: ArrayList<JournalEntryResponseDO> = ArrayList()

        val httpClient = WorkerHTTPClient()
        val gson = Gson()

        for (journal in items) {
            val j = JournalEntryResponseDO().apply {
                id = journal.id
                date = journal.date
                prompt = journal.prompt
                entry= journal.entry
                happinessRating = journal.happinessRating
            }
            journals.add(j)
        }

        val json = gson.toJson(JournalsRequest(journals=journals) )

        realm.close()

        val saveMultipleResponse = httpClient.makeRequest("$BASE_URL/journal/save_multiple", MOCK_TOKEN, json)

        println(saveMultipleResponse)
    }
}