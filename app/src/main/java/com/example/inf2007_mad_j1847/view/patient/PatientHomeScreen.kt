package com.example.inf2007_mad_j1847.view.patient

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.inf2007_mad_j1847.view.HomeScreenButton
import com.example.inf2007_mad_j1847.viewmodel.AuthViewModel

@Composable
fun PatientHomeScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val user by authViewModel.currentUser.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Patient Dashboard", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(32.dp))

        // Patient welcome card (same style pattern as DoctorHomeScreen)
        user?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Welcome Back,",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = it.name,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = it.email,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } ?: run {
            // Fallback for debug bypass (no logged-in user yet)
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Welcome Back,",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Patient",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

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
        Spacer(modifier = Modifier.height(48.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly // This spaces the buttons out evenly
        ){
        HomeScreenButton(
            title = "View Appointment",
            icon = Icons.Default.DateRange, // Using a suitable icon
            onClick = {
                navController.navigate("view_appointment_graph")
            }
        )
        HomeScreenButton(
            title = "",
            icon = Icons.Default.DateRange, // Using a suitable icon
            onClick = {
                //navController.navigate("view_appointment_graph")
            }
        )
        }
        // ---------------------------------------------------

        Spacer(modifier = Modifier.weight(1f)) // Pushes the logout button to the bottom

        OutlinedButton(
            onClick = {
                authViewModel.logout()
                navController.navigate("auth_graph") {
                    popUpTo(0) { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Logout")
        }
    }
}
