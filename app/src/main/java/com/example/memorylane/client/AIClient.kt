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
//        val json = """
//            {
//                "model": "gpt-3.5-turbo-0613",
//                "messages": [
//                    {
//                        "role": "system",
//                        "content": "You are a helpful assistant."
//                    },
//                    {
//                        "role": "user",
//                        "content": "Today, I failed my test. Disappointed but determined to learn from this setback and improve next time."
//                    },
//                    {
//                        "role": "user",
//                        "content": "Embarrassing moment in class today. Answered with the wrong entry. Apologized and reminded myself to be more attentive and think before speaking up. Lesson learned."
//                    }
//                ],
//                "functions": [
//                    {
//                      "name": "get_entry_themes",
//                      "description": "Show recognized common themes between multiple journal entries",
//                      "parameters": {
//                        "type": "object",
//                        "properties": {
//                          "themes": {
//                            "type": "array",
//                            "items": {
//                                "type": "string"
//                            },
//                            "description": "List of one word themes that represent common themes between the entries at a high level"
//                          }
//                        },
//                        "required": ["themes"]
//                      }
//                    }
//                  ]
//            }
//        """.trimIndent()

        val json = """
            {
                "model": "gpt-3.5-turbo-0613",
                "messages": [
                    {
                        "role": "system",
                        "content": "You are a helpful assistant."
                    },
                    {
                        "role": "user",
                        "content": "$prompt"
                    }
                ],
                "functions": [
                    {
                      "name": "get_entry_analysis",
                      "description": "Show analytics on journal entry",
                      "parameters": {
                        "type": "object",
                        "properties": {
                          "positives": {
                            "type": "array",
                            "items": {
                                "type": "string"
                            },
                            "description": "List of high level positives from the journal entry"
                          },
                          
                          "negatives": {
                            "type": "array",
                            "items": {
                                "type": "string"
                            },
                            "description": "List of high level negatives from the journal entry"
                          },
                          
                          "workOn": {
                            "type": "array",
                            "items": {
                                "type": "string"
                            },
                            "description": "List of things to work on/try to tackle the negatives"
                          }
                        },
                        "required": ["positives", "negatives", "workOn"]
                      }
                    }
                  ]
            }
        """.trimIndent()

        this.requestId = requestId

        client.makeRequest(URL, KEY, json, this);
    }

    override fun onSuccess(response: String) {
        val jsonObject = JSONObject(response)
        println(jsonObject)
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
