package com.example.memorylane.analytics

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.memorylane.R
import com.example.memorylane.client.WorkerAIClient
import com.example.memorylane.data.JournalEntryDO
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.query.Sort
import io.realm.kotlin.types.RealmList
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AnalyticsWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {

    override fun doWork(): Result {
        try {
            val gptRequest = WorkerAIClient()

            val config = RealmConfiguration.create(schema = setOf(JournalEntryDO::class))
            val realm = Realm.open(config)
            val items: RealmResults<JournalEntryDO> = realm.query<JournalEntryDO>().sort("date", Sort.DESCENDING)
                .limit(1)
                .find()

            val entry: JournalEntryDO = items.firstOrNull() ?: return Result.failure()

            val response = gptRequest.makeGptRequest(entry.entry)

            val positives = processResponseForPositives(response)
            val negatives = processResponseForNegatives(response)
            val workOns = processResponseForWorkOns(response)

            realm.writeBlocking {
                entry.positives = positives as RealmList<String>
                entry.negatives = negatives as RealmList<String>
                entry.workOn = workOns as RealmList<String>
            }

            realm.close()

            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }
}

private fun processResponseForPositives(response: String): List<String> {
    return listOf("Motivated", "Happy", "Grateful")
}

private fun processResponseForNegatives(response: String): List<String> {
    return listOf("Anxious", "Stressed", "Disappointed")
}

private fun processResponseForWorkOns(response: String): List<String> {
    return listOf("Focus", "Communication", "Organization")
}