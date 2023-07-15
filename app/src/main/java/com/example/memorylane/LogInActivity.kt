package com.example.memorylane

import android.content.Context
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import com.example.memorylane.client.BackendClient
import com.google.gson.Gson
import kotlinx.coroutines.launch


data class LogInResponse(val token: String = "", val message: String = "")

@Composable
fun LogInPage(
    modifier: Modifier = Modifier,
    onLoginSuccessful: () -> Unit,
    onCreateAccountClick: () -> Unit,
    context: Context
) {
    val emailState = remember { mutableStateOf(TextFieldValue()) }
    val passwordState = remember { mutableStateOf(TextFieldValue()) }
    val client = BackendClient()

    var errMsg by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    fun onLoginClicked(email: String, password: String) {
        client.loginUser(email, password) { logInResponse, exception ->
            scope.launch {
                if (exception != null) {
                    errMsg = exception.toString()
                }

                val gson = Gson()
                val logInResponseObject =
                    gson.fromJson(logInResponse?.body?.string(), LogInResponse::class.java)

                if (logInResponse?.isSuccessful == true) {
                    if (!logInResponseObject?.token.isNullOrBlank()) {
                        val sharedPref = context.getSharedPreferences("UserSettings", Context.MODE_PRIVATE)
                        with(sharedPref.edit()){
                            putString("authToken", logInResponseObject.token)
                            apply()
                        }
                        onLoginSuccessful()
                    }
                }
                else {
                    errMsg = logInResponseObject.message
                }
            }
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

                if (!errMsg.isNullOrBlank()) {
                    Spacer(Modifier.height(16.dp))
                    Text(text = errMsg, color = Color.Red)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onLoginClicked(emailState.value.text, passwordState.value.text) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Log In")
                }

                Spacer(modifier = Modifier.height(5.dp))

                Button(
                    onClick = { onCreateAccountClick() },
                    modifier = Modifier.fillMaxWidth()
                ){
                    Text(text = "Create Account")
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
