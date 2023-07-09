package com.example.memorylane.client

import com.example.memorylane.BuildConfig
import org.json.JSONObject

class WorkerAIClient {
    private val KEY = BuildConfig.API_KEY
    private val URL = BuildConfig.API_URL
    private val client = WorkerHTTPClient()

    fun makeGptRequest(prompt: String): Triple<List<String>, List<String>, List<String>> {
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

        return parseResponse(client.makeRequest(URL, KEY, json))
    }

    fun parseResponse(response: String): Triple<List<String>, List<String>, List<String>> {
        val jsonObject = JSONObject(response)
        val choicesArray = jsonObject.getJSONArray("choices")

        var positives = listOf<String>()
        var negatives = listOf<String>()
        var workOn = listOf<String>()

        for (i in 0 until choicesArray.length()) {
            val choiceObject = choicesArray.getJSONObject(i)
            val messageObject = choiceObject.getJSONObject("message")
            val functionCallObject = messageObject.getJSONObject("function_call")
            val arguments = functionCallObject.getString("arguments")

            val argumentsObject = JSONObject(arguments)
            positives = argumentsObject.getJSONArray("positives").let { array ->
                List(array.length()) { array.getString(it) }
            }
            negatives = argumentsObject.getJSONArray("negatives").let { array ->
                List(array.length()) { array.getString(it) }
            }
            workOn = argumentsObject.getJSONArray("workOn").let { array ->
                List(array.length()) { array.getString(it) }
            }
        }
        return Triple(positives, negatives, workOn)
    }
}
