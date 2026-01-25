package com.example.inf2007_mad_j1847.view.patient

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange // Icon for booking
import androidx.compose.material.icons.filled.Email // Icon for messages
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.inf2007_mad_j1847.view.HomeScreenButton // <-- Import the new button
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
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- NEW BUTTONS IN A ROW, STYLED LIKE HomeScreen ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly // This spaces the buttons out evenly
        ) {
            // 1. Book Appointment Button (Styled)
            HomeScreenButton(
                title = "Book Appointment",
                icon = Icons.Default.DateRange, // Using a suitable icon
                onClick = {
                    navController.navigate("booking_graph")
                }
            )

            // 2. Message Button (Styled)
            HomeScreenButton(
                title = "Messages",
                icon = Icons.Default.Email, // Using a suitable icon
                onClick = {
                    // TODO: Navigate to a future messaging screen
                    navController.navigate("conversation_list_screen")
                }
            )
        }
        // ---------------------------------------------------

        Spacer(modifier = Modifier.weight(1f)) // Pushes the logout button to the bottom

        // Logout Button (keeping its original style for now)
        Button(
            onClick = {
                authViewModel.logout()
                navController.navigate("auth_graph") {
                    popUpTo(0) { inclusive = true }
                }
            }
        ) {
            Text("Logout")
        }
    }
}
