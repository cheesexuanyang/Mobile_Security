package com.example.inf2007_mad_j1847.view.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.inf2007_mad_j1847.viewmodel.AuthViewModel

@Composable
fun LoginScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    val uiState by authViewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Redirect if login successful
    LaunchedEffect(uiState) {
        if (uiState is AuthViewModel.AuthUiState.Success) {
            val role = (uiState as AuthViewModel.AuthUiState.Success).role
            when (role) {
                "ADMIN" -> navController.navigate("admin_home") {
                    popUpTo("auth_graph") {
                        inclusive = true
                    }
                }

                "DOCTOR" -> navController.navigate("doctor_home") {
                    popUpTo("auth_graph") {
                        inclusive = true
                    }
                }

                else -> navController.navigate("patient_home") {
                    popUpTo("auth_graph") {
                        inclusive = true
                    }
                }
            }
        }
    }

    // Login UI
    Column(
        modifier = Modifier.Companion.fillMaxSize(),
        horizontalAlignment = Alignment.Companion.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.Companion.height(16.dp))

        if (uiState is AuthViewModel.AuthUiState.Loading) {
            CircularProgressIndicator()
        } else {
            Button(onClick = { authViewModel.login(email, password) }) { Text("Login") }
        }

        Spacer(modifier = Modifier.Companion.height(16.dp))

        TextButton(
            onClick = { navController.navigate("signup_screen") },
            modifier = Modifier.Companion.fillMaxWidth()
        ) {
            Text("Don't have an account? Sign Up")
        }

        if (uiState is AuthViewModel.AuthUiState.Error) {
            Text(
                (uiState as AuthViewModel.AuthUiState.Error).message,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}