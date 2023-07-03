package com.example.memorylane.client

import com.example.memorylane.BuildConfig
import org.json.JSONObject

class WorkerAIClient {
    private val KEY = BuildConfig.API_KEY
    private val URL = BuildConfig.API_URL
    private val client = WorkerHTTPClient()

    fun makeGptRequest(prompt: String): String {
        val json = """
           ... (rest of the JSON string) ...
        """.trimIndent()

        return client.makeRequest(URL, KEY, json)
    }

    fun parseResponse(response: String): Pair<String, List<String>> {
        val jsonObject = JSONObject(response)
        val choicesArray = jsonObject.getJSONArray("choices")
        var content = ""
        var contentList = listOf<String>()

        for (i in 0 until choicesArray.length()) {
            val choiceObject = choicesArray.getJSONObject(i)
            val messageObject = choiceObject.getJSONObject("message")
            content = messageObject.getString("content")
            contentList = content.replace(".", "").split(", ")
        }
        return Pair(content, contentList)
    }
}
