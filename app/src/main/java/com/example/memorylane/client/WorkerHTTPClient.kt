package com.example.memorylane.client

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class WorkerHTTPClient {
    fun makeRequest(url: String, auth: String, body: String): String {
        val client = OkHttpClient()

        val jsonBody = body.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .post(jsonBody)
            .addHeader("Authorization", "Bearer $auth")
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            return response.body?.string() ?: throw IOException("Response body is null")
        }
    }
}
