package com.example.memorylane.client

import android.util.Log
import com.example.memorylane.BuildConfig

interface BackendEventListener : EventListener {
    override fun onSuccess(response: String)
    override fun onFailure(e: Exception)
}

interface BackendResponseListener {
    fun onSuccess(response: String)
    fun onFailure(e: Exception)
}

class BackendClient(private val listener: BackendResponseListener) : BackendEventListener {

    private val BASE_URL = BuildConfig.BASE_URL
    private var token = ""
    private val client = HTTPClient()

    private var requestId = 0

    fun loginUser(email: String, password: String, requestId: Int = 0) {
        val json = """
            {
                "email": "$email",
                "password": "$password"
            }
        """.trimIndent()

        this.requestId = requestId

        client.post(BASE_URL+"/user/login", token, json,this);
    }

    override fun onSuccess(response: String) {
        Log.d("fdasddasd", "dsadasdasdasdasdasdasdsdhklfsdjdksljfkl")
        listener.onSuccess(response)
    }

    override fun onFailure(e: Exception) {
        listener.onFailure(e)
    }
}
