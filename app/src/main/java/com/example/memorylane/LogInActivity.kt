package com.example.memorylane

import android.os.Bundle
import android.os.ProxyFileDescriptorCallback
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.memorylane.ui.theme.MemorylaneTheme
import androidx.compose.material3.MaterialTheme
import com.example.memorylane.ui.components.CustomCard
import androidx.compose.material.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import com.example.memorylane.client.BackendClient
import com.example.memorylane.client.BackendResponseListener

class LogInResponseListener : BackendResponseListener {
    var response = ""
    var error = ""
    var success = false
    override fun onSuccess(response: String) {
        this.response = response
        this.success = true
    }
    override fun onFailure(e: Exception) {
        this.error = "error"
    }
}

@Composable
fun LogInPage(modifier: Modifier = Modifier, logInSuccess: () -> Unit) {
    val emailState = remember { mutableStateOf(TextFieldValue()) }
    val passwordState = remember { mutableStateOf(TextFieldValue()) }
    val listener = remember{ mutableStateOf(LogInResponseListener())}
    val client = remember{ mutableStateOf(BackendClient(listener.value))}

    fun onLoginClicked(email: String, password: String) {
        client.value.loginUser(email, password)
    }

    LaunchedEffect(listener.value.success) {
        if (listener.value.success) {
            logInSuccess()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp, 10.dp, 10.dp, 10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomCard (
            modifier.background(Color(255, 255, 255))
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 16.dp, horizontal = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Log In",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                TextField(
                    value = emailState.value,
                    onValueChange = { emailState.value = it },
                    label = { Text(text = "Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = passwordState.value,
                    onValueChange = { passwordState.value = it },
                    label = { Text(text = "Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onLoginClicked(emailState.value.text, passwordState.value.text) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Log In")
                }
            }
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun LogInPagePreview() {
//    MemorylaneTheme {
//        LogInPage()
//    }
//}
