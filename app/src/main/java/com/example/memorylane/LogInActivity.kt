package com.example.memorylane

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import com.example.memorylane.ui.components.CustomCard
import androidx.compose.material.*
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import com.example.memorylane.client.BackendClient
import com.google.gson.Gson


data class LogInResponse(val token: String = "", val message: String = "")

@Composable
fun LogInPage(modifier: Modifier = Modifier, token: MutableState<String>) {
    val emailState = remember { mutableStateOf(TextFieldValue()) }
    val passwordState = remember { mutableStateOf(TextFieldValue()) }
    val client = BackendClient()

    val response = remember {
        mutableStateOf("")
    }

    var errMsg by remember { mutableStateOf("") }

    val gson = Gson()
    var logInResponse = gson.fromJson(response.value, LogInResponse::class.java)

    if (logInResponse != null) {
        if (logInResponse.token != "") {
            token.value = logInResponse.token
        } else {
            errMsg = logInResponse.message
        }
    }

    fun onLoginClicked(email: String, password: String) {
        client.loginUser(email, password, response)
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
