package com.example.memorylane.client

import com.example.memorylane.BuildConfig
import okhttp3.Response

class WeatherClient() {

    private val KEY = BuildConfig.WEATHER_API_KEY
    private val URL = BuildConfig.WEATHER_API_BASE_URL

    private val client = HTTPClient()

    fun getWeather(location: String, onComplete: (Response?, Exception?) -> Unit) {
        client.get("$URL/current.json?key=$KEY&q=$location&aqi=no", "", onComplete)
    }
}
