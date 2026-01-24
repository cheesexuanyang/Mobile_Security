package com.example.inf2007_mad_j1847.view.patient

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.inf2007_mad_j1847.viewmodel.AuthViewModel

@Composable
fun PatientHomeScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    val user by authViewModel.currentUser.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Patient Dashboard", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(20.dp))

        user?.let {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Welcome, ${it.name}")
                    Text("Email: ${it.email}")
                }
            }
            // Add Patient-specific features: e.g., Booking buttons
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = {
            authViewModel.logout() // Clear the session first
            navController.navigate("auth_graph") {
                popUpTo(0) { inclusive = true } // Clear the entire backstack
            }
        }) {
            Text("Logout")
        }
    }
}