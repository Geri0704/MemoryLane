package com.example.memorylane

import android.content.Context

class UserPreferences(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)

    var notificationTime: String
        get() = sharedPreferences.getString("notification_time", "12:00") ?: "12:00"
        set(value) = sharedPreferences.edit().putString("notification_time", value).apply()

    var notificationDays: Set<String>
        get() = sharedPreferences.getStringSet("notification_days", emptySet()) ?: emptySet()
        set(value) = sharedPreferences.edit().putStringSet("notification_days", value).apply()

    var aiPrompts: Boolean
        //TODO: set default value depending on if they accept or decline terms and conditions
        get() = sharedPreferences.getBoolean("ai_prompts", true)
        set(value) = sharedPreferences.edit().putBoolean("ai_prompts", value).apply()
}
