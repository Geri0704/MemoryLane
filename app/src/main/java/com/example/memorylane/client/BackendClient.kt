package com.example.memorylane.client

import android.util.Log
import androidx.compose.runtime.MutableState
import com.example.memorylane.BuildConfig

interface BackendEventListener : EventListener {
    override fun onSuccess(response: String)
    override fun onFailure(e: Exception)
}

interface BackendResponseListener {
    fun onSuccess(response: String)
    fun onFailure(e: Exception)
}

class BackendClient() {

    private val BASE_URL = BuildConfig.BASE_URL
    private var token = ""
    private val client = HTTPClient()

    fun loginUser(email: String, password: String, result: MutableState<String>) {
        val json = """
            {
                "email": "$email",
                "password": "$password"
            }
        """.trimIndent()

        client.post(BASE_URL+"/user/login", token, json, result);
    }

//
//    override fun onSuccess(response: String) {
//        Log.d("fdasddasd", "dsadasdasdasdasdasdasdsdhklfsdjdksljfkl")
//        listener.onSuccess(response)
//    }
//
//    override fun onFailure(e: Exception) {
//        listener.onFailure(e)
//    }
}
