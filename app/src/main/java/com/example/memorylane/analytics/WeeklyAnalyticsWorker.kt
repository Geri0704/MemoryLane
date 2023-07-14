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

class WeeklyAnalyticsWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        val config = RealmConfiguration.create(schema = setOf(JournalEntryDO::class, WeeklyStorageDO::class))
        val realm = Realm.open(config)

        val currentDate = LocalDate.now()
        val startDate = currentDate.minusDays(currentDate.dayOfWeek.value.toLong())
        val endDate = startDate.plusDays(6)

        val weekStartDateString = startDate.format(DateTimeFormatter.ofPattern("dd/MM"))
        val weekEndDateString = endDate.format(DateTimeFormatter.ofPattern("dd/MM"))

        println(weekStartDateString)
        println(weekEndDateString)

        val items: RealmResults<JournalEntryDO> = realm.query<JournalEntryDO>()
            .sort("date", Sort.DESCENDING)
            .find()

        if (items.isEmpty()) Result.failure(workDataOf("Failed" to "No data needing analytics"))

        val items2: RealmResults<WeeklyStorageDO> = realm.query<WeeklyStorageDO>("weekStartDate == '$startDate'")
            .find()

        if (items2.isNotEmpty()) Result.failure(workDataOf("Failed" to "Already done analytics"))

        val gptRequest = WorkerAIClient()
        val weeklyStorage = WeeklyStorageDO().apply {
            weekStartDate = startDate.toString()
            weekEndDate = endDate.toString()
        }

        var prompt = "The following are numerous journal entries, return a list of the recognized positives, problems, and things to work on that they have in common if any: "

        for ((index, entry) in items.withIndex()) {
            prompt += "${index + 1}. ${entry.entry} "
        }

        val response = gptRequest.makeWeekGptRequest(prompt)

        println(response)

        val positives = if (response.first.isNotEmpty()) response.first.map { it.capitalize() } else listOf("None recognized")
        val negatives = if (response.second.isNotEmpty()) response.second.map { it.capitalize() } else listOf("None recognized")
        val workOns = if (response.third.isNotEmpty()) response.third.map { it.capitalize() } else listOf("None recognized")

        weeklyStorage.continuingPositives.addAll(positives)
        weeklyStorage.problems.addAll(negatives)
        weeklyStorage.thingsToWorkOn.addAll(workOns)

        realm.writeBlocking {
            copyToRealm(WeeklyStorageDO().apply {
                id = UUID.randomUUID().toString()
                weekStartDate = weeklyStorage.weekStartDate
                weekEndDate = weeklyStorage.weekEndDate
                continuingPositives.addAll(weeklyStorage.continuingPositives)
                problems.addAll(weeklyStorage.problems)
                thingsToWorkOn.addAll(weeklyStorage.thingsToWorkOn)
            })
        }

        realm.close()
        return Result.success(workDataOf("Success" to "Weekly analytics updated"))
    }
}
