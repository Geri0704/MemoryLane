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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

class BackendWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        try {
//            val httpClient = WorkerHTTPClient()
//
//            val journalResponse = httpClient.get(BASE_URL+"/journal", MOCK_TOKEN)
//            val gson = Gson()
//            val journalResponseObject = gson.fromJson(journalResponse, JournalResponse::class.java)
//            var journalEntries = journalResponseObject.journals
//
//
//            val config = RealmConfiguration.create(schema = setOf(JournalEntryDO::class))
//            val realm = Realm.open(config)
//
//            for (journal in journalEntries) {
//                val item: RealmResults<JournalEntryDO> =
//                realm.query<JournalEntryDO>("date == '${journal.date}'")
//                    .sort("date", Sort.DESCENDING)
//                    .find()
//            }
//
//
//            realm.close()



//            for (entry in items) {
//                val response = gptRequest.makeGptRequest(entry.entry)
//
//                println(response)
//
//                realm.writeBlocking {
//                    findLatest(entry)?.apply {
//                        findLatest(entry)?.positives?.addAll(positives)
//                        findLatest(entry)?.negatives?.addAll(negatives)
//                        findLatest(entry)?.workOn?.addAll(workOns)
//                    }
//                }
//            }

            return Result.success(workDataOf("Success" to "Local database updated"))
        } catch (e: Exception) {
            println(e)
            return Result.failure(workDataOf("Failed" to "Sync local database process failed"))
        }
    }
}