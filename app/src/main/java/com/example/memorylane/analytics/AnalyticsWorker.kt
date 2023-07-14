package com.example.memorylane.analytics

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.memorylane.client.WorkerAIClient
import com.example.memorylane.data.JournalEntryDO
import com.example.memorylane.data.WeeklyStorageDO
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.query.Sort
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

class AnalyticsWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        try {
            val gptRequest = WorkerAIClient()

            val config = RealmConfiguration.create(schema = setOf(JournalEntryDO::class))
            val realm = Realm.open(config)

            val items: RealmResults<JournalEntryDO> = realm.query<JournalEntryDO>("positives.@size = 0 OR negatives.@size = 0 OR workOn.@size = 0")
                .sort("date", Sort.DESCENDING)
                .find()

            if (items.isEmpty()) return Result.failure(workDataOf("Failed" to "No data needing analytics"))

            for (entry in items) {
                val response = gptRequest.makeGptRequest(entry.entry)

                println(response)

                val positives = if (response.first.isNotEmpty()) response.first.map { it.capitalize() } else listOf("None recognized")
                val negatives = if (response.second.isNotEmpty()) response.second.map { it.capitalize() } else listOf("None recognized")
                val workOns = if (response.third.isNotEmpty()) response.third.map { it.capitalize() } else listOf("None recognized")

                realm.writeBlocking {
                    findLatest(entry)?.apply {
                        findLatest(entry)?.positives?.addAll(positives)
                        findLatest(entry)?.negatives?.addAll(negatives)
                        findLatest(entry)?.workOn?.addAll(workOns)
                    }
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
