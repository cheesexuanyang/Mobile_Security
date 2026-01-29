package com.example.inf2007_mad_j1847.view.doctor

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
import com.example.inf2007_mad_j1847.viewmodel.AuthViewModel

@Composable
fun DoctorHomeScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val user by authViewModel.currentUser.collectAsState()

    Column(
        modifier = Modifier.Companion
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.Companion.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Doctor Dashboard", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.Companion.height(32.dp))

        // Doctor welcome (similar style to Admin)
        user?.let {
            Card(
                modifier = Modifier.Companion.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.Companion.padding(20.dp)) {
                    Text(
                        text = "Welcome Back,",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Dr. ${it.name}",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        } ?: run {
            // Fallback for debug bypass (no logged-in user yet)
            Card(
                modifier = Modifier.Companion.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.Companion.padding(20.dp)) {
                    Text(
                        text = "Welcome Back,",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Doctor",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.Companion.height(48.dp))

        // Big, squarish central buttons (vertical)
        Button(
            onClick = { navController.navigate("doctor_appointments") },
            modifier = Modifier.Companion
                .fillMaxWidth()
                .height(90.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(horizontalAlignment = Alignment.Companion.CenterHorizontally) {
                Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
                Spacer(modifier = Modifier.Companion.height(6.dp))
                Text("Appointments")
            }
        }

        Spacer(modifier = Modifier.Companion.height(16.dp))

        Button(
            onClick = { navController.navigate("doctor_messaging") },
            modifier = Modifier.Companion
                .fillMaxWidth()
                .height(90.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(horizontalAlignment = Alignment.Companion.CenterHorizontally) {
                Icon(imageVector = Icons.Default.Email, contentDescription = null)
                Spacer(modifier = Modifier.Companion.height(6.dp))
                Text("Messages")
            }
        }

        Spacer(modifier = Modifier.Companion.height(24.dp))

        OutlinedButton(
            onClick = {
                authViewModel.logout()
                navController.navigate("auth_graph") {
                    popUpTo(0) { inclusive = true }
                }
            },
            modifier = Modifier.Companion.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Logout")
        }
    }
}