package com.example.memorylane

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.memorylane.client.BackendClient
import com.google.gson.Gson
import kotlinx.coroutines.launch

data class SignUpResponse(val token: String = "", val message: String = "")
@Composable
fun AccountCreation(
    modifier: Modifier = Modifier,
    onCreateAccountSuccessful: () -> Unit,
    onBackClick: () -> Unit,
    context: Context
) {
    val nameState = remember { mutableStateOf(TextFieldValue()) }
    val emailState = remember { mutableStateOf(TextFieldValue()) }
    val passwordState = remember { mutableStateOf(TextFieldValue()) }
    val confirmPasswordState = remember { mutableStateOf(TextFieldValue()) }
    val client = BackendClient()

    var errMsg by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    fun onCreateAccountClicked(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        if (name == "") {
            errMsg = "Name can not be empty!"
            return
        }
        if (email == "") {
            errMsg = "Email can not be empty!"
            return
        }
        if (password != confirmPassword) {
            errMsg = "Passwords must match!"
            return
        }
        if (password == "") {
            errMsg = "Password can not be empty!"
            return
        }

        client.signUpUser(name, email, password) { signUpResponse, exception ->
            scope.launch {
                if (exception != null) {
                    errMsg = exception.toString()
                }

                val gson = Gson()
                val signUpResponseObject = gson.fromJson(signUpResponse?.body?.string(), SignUpResponse::class.java)

                if (signUpResponse?.isSuccessful == true) {
                    if (!signUpResponseObject?.token.isNullOrBlank()) {
                        val sharedPref = context.getSharedPreferences("UserSettings", Context.MODE_PRIVATE)
                        with(sharedPref.edit()){
                            putString("authToken", signUpResponseObject.token)
                            apply()
                        }
                        onCreateAccountSuccessful()
                    }
                }
                else{
                    errMsg = signUpResponseObject.message
                }
            }
        }
    }

        Column(modifier.padding(16.dp)) {
            Text(text = "Create Account", style = MaterialTheme.typography.h5)

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = nameState.value,
                onValueChange = { nameState.value = it },
                label = { Text("Name") },
                singleLine = true,
                textStyle = MaterialTheme.typography.body1,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = emailState.value,
                onValueChange = { emailState.value = it },
                label = { Text("Email") },
                singleLine = true,
                textStyle = MaterialTheme.typography.body1,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = passwordState.value,
                onValueChange = { passwordState.value = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                textStyle = MaterialTheme.typography.body1,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = confirmPasswordState.value,
                onValueChange = { confirmPasswordState.value = it },
                label = { Text("Confirm Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                textStyle = MaterialTheme.typography.body1,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Text(text = errMsg, color = Color.Red)

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    onCreateAccountClicked(
                        nameState.value.text,
                        emailState.value.text,
                        passwordState.value.text,
                        confirmPasswordState.value.text
                    )
                }
            ) {
                Text("Create Account")
            }

            Spacer(Modifier.height(8.dp))

            TextButton(onClick = onBackClick) {
                Text("Back to Login")
            }
        }
    }