package com.example.memorylane.analytics

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.memorylane.R
import com.example.memorylane.client.WorkerAIClient
import com.example.memorylane.data.JournalEntryDO
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.UpdatePolicy
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

            val items: RealmResults<JournalEntryDO> = realm.query<JournalEntryDO>("themes.@size = 0 OR positives.@size = 0 OR negatives.@size = 0 OR workOn.@size = 0")
                .sort("date", Sort.DESCENDING)
                .find()

            if (items.isEmpty()) return Result.failure(workDataOf("Failed" to "No data needing analytics"))

            for (entry in items) {
                println(entry)

                val response = gptRequest.makeGptRequest(entry.entry)

                println(response)

                val positives = response.first
                val negatives = response.second
                val workOns = response.third

                realm.writeBlocking {
                    findLatest(entry)?.positives?.addAll(positives)
                    findLatest(entry)?.negatives?.addAll(negatives)
                    findLatest(entry)?.workOn?.addAll(workOns)
                }
            }

            realm.close()

            return Result.success(workDataOf("Success" to "Analytics updated"))
        } catch (e: Exception) {
            println(e)
            return Result.failure(workDataOf("Failed" to "Analytics process failed"))
        }
    }
}
