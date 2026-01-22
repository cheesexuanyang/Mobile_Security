package com.example.inf2007_mad_j1847.view

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
fun SignUpScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    val uiState by authViewModel.uiState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }

    // Navigation logic upon successful registration
    LaunchedEffect(uiState) {
        if (uiState is AuthViewModel.AuthUiState.Success) {
            // New users are always patient by default
            navController.navigate("patient_graph") {
                popUpTo("auth_graph") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Create Account", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))

        TextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") })
        TextField(value = username, onValueChange = { username = it }, label = { Text("Username") })
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (uiState is AuthViewModel.AuthUiState.Loading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = { authViewModel.signUp(email, password, name, username) },
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text("Sign Up")
            }
        }

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Already have an account? Login")
        }

        if (uiState is AuthViewModel.AuthUiState.Error) {
            Text(
                text = (uiState as AuthViewModel.AuthUiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}