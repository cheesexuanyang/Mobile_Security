package com.example.inf2007_mad_j1847.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.inf2007_mad_j1847.viewmodel.AuthViewModel

@Composable
fun DoctorHomeScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    val user by authViewModel.currentUser.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Doctor Portal", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(20.dp))

        user?.let {
            Text("Logged in: Dr. ${it.name}", style = MaterialTheme.typography.bodyLarge)
            // Add Doctor-specific features: e.g., Patient list, Appointment schedule
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- Added Message Button for Doctor ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            HomeScreenButton(
                title = "Messages",
                icon = Icons.Default.Email,
                onClick = {
                    navController.navigate("conversation_list_screen") // Point to the list
                }
            )
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