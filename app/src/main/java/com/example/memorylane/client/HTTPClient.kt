package com.example.memorylane.client

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

interface EventListener {
    fun onSuccess(response: String)
    fun onFailure(exception: Exception)
}

class HTTPClient() {
    fun makeRequest(url: String, auth: String, body: String, listener: EventListener) {
        val client = OkHttpClient()

        val jsonBody = body.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .post(jsonBody)
            .addHeader("Authorization", "Bearer $auth")
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                listener.onFailure(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    println(response.body?.string())
                    listener.onFailure(java.lang.Exception("Failed"))
                }

                val answer = response.body?.string()
                if (answer != null) {
                    listener.onSuccess(answer)
                } else {
                    listener.onFailure(java.lang.Exception("Failed"))
                }
            }
        })
    }
}
