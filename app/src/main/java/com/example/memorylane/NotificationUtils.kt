package com.example.memorylane

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ReminderBroadcast : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onReceive(context: Context, intent: Intent) {
        sendNotification(context)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun sendNotification(context: Context) {
        val builder = NotificationCompat.Builder(context, "com.example.memorylane")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Daily Reminder")
            .setContentText("It's time to open the app and jot down your memories.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        createNotificationChannel(context)

        with(NotificationManagerCompat.from(context)) {
            // Permission check for notifications
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request permission if not already granted
                ActivityCompat.requestPermissions(
                    context as MainActivity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            }
            notify(200, builder.build())
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Memorylane Reminder"
            val descriptionText = "Channel for daily reminder"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("com.example.memorylane", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

