package com.example.memorylane.client

import com.example.memorylane.BuildConfig
import com.example.memorylane.GptResponseListener
import org.json.JSONObject

interface ClientGptResponseListener : EventListener {
    override fun onSuccess(response: String)
    override fun onFailure(e: Exception)
}

class AIClient(private val listener: GptResponseListener) : ClientGptResponseListener {

    private val KEY = BuildConfig.API_KEY
    private val URL = BuildConfig.API_URL

    private val client = HTTPClient()

    private var requestId = 0

    fun makeGptRequest(prompt: String, requestId: Int = 0) {
        val json = """
            {
                "model": "gpt-3.5-turbo",
                "messages": [
                    {
                        "role": "system",
                        "content": "You are a helpful assistant."
                    },
                    {
                        "role": "user",
                        "content": "$prompt"
                    }
                ]
            }
        """.trimIndent()

        this.requestId = requestId

        client.makeRequest(URL, KEY, json, this);
    }

    override fun onSuccess(response: String) {
        val jsonObject = JSONObject(response)
        val choicesArray = jsonObject.getJSONArray("choices")

        for (i in 0 until choicesArray.length()) {
            val choiceObject = choicesArray.getJSONObject(i)
            val messageObject = choiceObject.getJSONObject("message")
            val content = messageObject.getString("content")

            if (requestId == 0) {
                listener.onGptResponse(content)
            } else if (requestId == 1) {
                val contentList = content.replace(".", "").split(", ")
                listener.onGptThemeResponse(contentList)
            }
        }
    }

    override fun onFailure(e: Exception) {
        listener.onGptFailure(e)
    }
}
