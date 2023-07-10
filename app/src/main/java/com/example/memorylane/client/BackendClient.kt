package com.example.memorylane.client

import android.util.Log
import androidx.compose.runtime.MutableState
import com.example.memorylane.BuildConfig
import okhttp3.Response

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

    fun loginUser(email: String, password: String, onComplete: (Response?, Exception?) -> Unit) {
        val json = """
            {
                "email": "$email",
                "password": "$password"
            }
        """.trimIndent()

        client.post(BASE_URL+"/user/login", token, json, onComplete);
    }

    fun signUpUser(name: String, email: String, password: String, onComplete: (Response?, Exception?) -> Unit){
        val json = """
            {
                "name": "$name",
                "email": "$email",
                "password": "$password"
            }
        """.trimIndent()

        client.post(BASE_URL+"/user/signup", token, json, onComplete)
    }
    fun getJournals(authToken: String, result: MutableState<String>) {
        client.get(BASE_URL+"/journal", authToken, result);
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
